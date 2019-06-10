package ch.epfl.biop.bdv.scijava.export;

import bdv.export.ExportMipmapInfo;
import bdv.export.ProgressWriter;
import bdv.export.SubTaskProgressWriter;
import bdv.export.WriteSequenceToHdf5;
import bdv.ij.util.ProgressWriterIJ;
import bdv.img.hdf5.Hdf5ImageLoader;
import bdv.img.hdf5.Partition;
import bdv.spimdata.SequenceDescriptionMinimal;
import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import bdv.util.BdvHandle;
import bdv.viewer.Source;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import mpicbg.spim.data.registration.ViewRegistration;
import mpicbg.spim.data.registration.ViewRegistrations;
import mpicbg.spim.data.sequence.Channel;
import mpicbg.spim.data.sequence.TimePoint;
import mpicbg.spim.data.sequence.TimePoints;
import mpicbg.spim.data.sequence.VoxelDimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Plugin(type = Command.class, menuPath = "Plugins>BIOP>BDV>Save Source", initializer = "initParams")
public class BdvSourceExportToXMLHDF5 implements Command{

    @Parameter(label="Source index, starts at 0")
    public int sourceIndex = 0;

    @Parameter(label = "BigDataViewer Frame")
    public BdvHandle bdv_h;

    @Parameter
    public boolean split = true;

    @Parameter
    int nThreads = 4;

    @Parameter
    File xmlFile;

    public void run() {
        // Check image dimensionality

        // en fait on suppose 3D


        Source<?> src = bdv_h.getViewerPanel().getState().getSources().get(sourceIndex).getSpimSource();//srcs_in.get(0);

        VoxelDimensions voxelSize = src.getVoxelDimensions();

        RandomAccessibleInterval<?> refRai = src.getSource(0,0);

        final FinalDimensions size = new FinalDimensions( new long[] { refRai.dimension(0), refRai.dimension(1), refRai.dimension(2) } );

        // get mipmap settings from the source. asserts the same volume is covered all the time
        /**
         * subsampling factors. indexed by mipmap level, dimension.
         */
        int[][] resolutions = new int[src.getNumMipmapLevels()][3];

        /**
         * subdivision block sizes. indexed by mipmap level, dimension.
         */
        final int[][] subdivisions = new int[src.getNumMipmapLevels()][3];;


        final AffineTransform3D[] transforms = new AffineTransform3D[src.getNumMipmapLevels()];

        for (int iMipMap = 0; iMipMap<src.getNumMipmapLevels(); iMipMap++) {

            RandomAccessibleInterval<?> refRaiMipMap = src.getSource(0,iMipMap);

            double scaleX = (double)size.dimension(0)/(double)refRaiMipMap.dimension(0);
            double scaleY = (double)size.dimension(1)/(double)refRaiMipMap.dimension(1);
            double scaleZ = (double)size.dimension(2)/(double)refRaiMipMap.dimension(2);

            resolutions[iMipMap][0]=(int)scaleX;
            resolutions[iMipMap][1]=(int)scaleY;
            resolutions[iMipMap][2]=(int)scaleZ;

            subdivisions[iMipMap][0]=refRaiMipMap.dimension(0)>1?64:1;
            subdivisions[iMipMap][1]=refRaiMipMap.dimension(1)>1?64:1;
            subdivisions[iMipMap][2]=refRaiMipMap.dimension(2)>1?64:1;

            transforms[iMipMap] = new AffineTransform3D();

            src.getSourceTransform(0,iMipMap,transforms[iMipMap]);
        }

        //final MipmapInfo mipmapSettings = new MipmapInfo(resolutions, transforms, subdivisions);

        final int numTimepoints = 1;
        final int numSetups = 1;

        final HashMap< Integer, BasicViewSetup> setups = new HashMap<>( numSetups );
        final BasicViewSetup basicviewsetup = new BasicViewSetup( 0, src.getName(), size, voxelSize );
        basicviewsetup.setAttribute( new Channel( 1 ) );
        setups.put( 0, basicviewsetup );

        /*for ( int s = 0; s < numSetups; ++s )
        {
            final BasicViewSetup setup = new BasicViewSetup( s, String.format( "channel %d", s + 1 ), size, voxelSize );
            setup.setAttribute( new Channel( s + 1 ) );
            setups.put( s, setup );
        }*/
        final ArrayList<TimePoint> timepoints = new ArrayList<>( numTimepoints );
        for ( int t = 0; t < numTimepoints; ++t )
            timepoints.add( new TimePoint( t ) );

        ImgLoaderFromSource< ? > imgLoader = new ImgLoaderFromSource(src);

        final SequenceDescriptionMinimal seq = new SequenceDescriptionMinimal( new TimePoints( timepoints ), setups, imgLoader, null );

        Map< Integer, ExportMipmapInfo> perSetupExportMipmapInfo;
        perSetupExportMipmapInfo = new HashMap<>();
        for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() )
            perSetupExportMipmapInfo.put( setup.getId(), new ExportMipmapInfo(resolutions, subdivisions) );


        // LoopBackHeuristic:
        // - If saving more than 8x on pixel reads use the loopback image over
        //   original image
        // - For virtual stacks also consider the cache size that would be
        //   required for all original planes contributing to a "plane of
        //   blocks" at the current level. If this is more than 1/4 of
        //   available memory, use the loopback image.
        final boolean isVirtual = true;//imp.getStack().isVirtual();
        long planeSizeInBytes = size.dimension(0)*size.dimension(1);
        if (src.getType() instanceof UnsignedByteType) {
            //* imp.getBytesPerPixel();
        } else  if (src.getType() instanceof UnsignedShortType) {
            planeSizeInBytes*=2;
        } else  if (src.getType() instanceof ARGBType) {
            planeSizeInBytes*=3;
        }
        final long ijMaxMemory =  Runtime.getRuntime().maxMemory();//IJ.maxMemory();


        final int numCellCreatorThreads = Math.max( 1, nThreads - 1 );

        final long finalPlaneSizeInBytes = planeSizeInBytes;
        final WriteSequenceToHdf5.LoopbackHeuristic loopbackHeuristic = new WriteSequenceToHdf5.LoopbackHeuristic()
        {
            @Override
            public boolean decide( final RandomAccessibleInterval< ? > originalImg, final int[] factorsToOriginalImg, final int previousLevel, final int[] factorsToPreviousLevel, final int[] chunkSize )
            {
                if ( previousLevel < 0 )
                    return false;

                if ( WriteSequenceToHdf5.numElements( factorsToOriginalImg ) / WriteSequenceToHdf5.numElements( factorsToPreviousLevel ) >= 8 )
                    return true;

                if ( isVirtual )
                {
                    final long requiredCacheSize = finalPlaneSizeInBytes * factorsToOriginalImg[ 2 ] * chunkSize[ 2 ];
                    if ( requiredCacheSize > ijMaxMemory / 4 )
                        return true;
                }

                return false;
            }
        };

        final WriteSequenceToHdf5.AfterEachPlane afterEachPlane = new WriteSequenceToHdf5.AfterEachPlane()
        {
            @Override
            public void afterEachPlane( final boolean usedLoopBack )
            {
                if ( !usedLoopBack && isVirtual )
                {
                    final long free = Runtime.getRuntime().freeMemory();
                    final long total = Runtime.getRuntime().totalMemory();
                    final long max = Runtime.getRuntime().maxMemory();
                    final long actuallyFree = max - total + free;

                    //if ( actuallyFree < max / 2 )
                    //    imgLoader.clearCache();
                }
            }

        };

        final ArrayList<Partition> partitions;
        /*if ( split )
        {
            final String xmlFilename = xmlFile.getAbsolutePath();
            int timepointsPerPartition = 0;
            int setupsPerPartition = 0;

            final String basename = xmlFilename.endsWith( ".xml" ) ? xmlFilename.substring( 0, xmlFilename.length() - 4 ) : xmlFilename;
            partitions = Partition.split( timepoints, seq.getViewSetupsOrdered(), timepointsPerPartition, setupsPerPartition, basename );

            for ( int i = 0; i < partitions.size(); ++i )
            {
                final Partition partition = partitions.get( i );
                final ProgressWriter p = new SubTaskProgressWriter( progressWriter, 0, 0.95 * i / partitions.size() );
                WriteSequenceToHdf5.writeHdf5PartitionFile( seq, perSetupExportMipmapInfo, params.deflate, partition, loopbackHeuristic, afterEachPlane, numCellCreatorThreads, p );
            }
            WriteSequenceToHdf5.writeHdf5PartitionLinkFile( seq, perSetupExportMipmapInfo, partitions, params.hdf5File );
        }
        else*/


        final ProgressWriter progressWriter = new ProgressWriterIJ();
        progressWriter.out().println( "starting export..." );

        String seqFilename = xmlFile.getParent();
        if ( !seqFilename.endsWith( ".xml" ) )
            seqFilename += ".xml";
        final File seqFile = new File( seqFilename );
        final File parent = seqFile.getParentFile();
        if ( parent == null || !parent.exists() || !parent.isDirectory() )
        {
            System.out.println( "Invalid export filename " + seqFilename );
            return;
        }
        final String hdf5Filename = seqFilename.substring( 0, seqFilename.length() - 4 ) + ".h5";
        final File hdf5File = new File( hdf5Filename );
        boolean deflate = false;
        {
            partitions = null;
            WriteSequenceToHdf5.writeHdf5File( seq, perSetupExportMipmapInfo, deflate, hdf5File, loopbackHeuristic, afterEachPlane, numCellCreatorThreads, new SubTaskProgressWriter( progressWriter, 0, 0.95 ) );
        }

        // write xml sequence description
        final Hdf5ImageLoader hdf5Loader = new Hdf5ImageLoader( hdf5File, partitions, null, false );
        final SequenceDescriptionMinimal seqh5 = new SequenceDescriptionMinimal( seq, hdf5Loader );

        final ArrayList<ViewRegistration> registrations = new ArrayList<>();
        for ( int t = 0; t < numTimepoints; ++t )
            for ( int s = 0; s < numSetups; ++s )
                registrations.add( new ViewRegistration( t, s, transforms[0]) );

        final File basePath = seqFile.getParentFile();
        final SpimDataMinimal spimData = new SpimDataMinimal( basePath, seqh5, new ViewRegistrations( registrations ) );

        try
        {
            new XmlIoSpimDataMinimal().save( spimData, seqFile.getAbsolutePath() );
            progressWriter.setProgress( 1.0 );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
        progressWriter.out().println( "done" );

    }

}
