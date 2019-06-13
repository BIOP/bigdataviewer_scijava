package ch.epfl.biop.bdv.scijava.export;

import bdv.export.*;
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
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Deeply copied from
 * https://github.com/bigdataviewer/bigdataviewer_fiji/blob/master/src/main/java/bdv/ij/ExportImagePlusPlugIn.java
 *
 * https://github.com/tischi/bdv-utils/blob/master/src/main/java/de/embl/cba/bdv/utils/io/BdvRaiVolumeExport.java#L38
 *
 * This export does not take advantage of potentially already computed mipmaps TODO take advantage of this, whenever possible
 *
 */

//@Plugin(type = Command.class, menuPath = "Plugins>BIOP>BDV>Save Source", initializer = "initParams")
public class BdvSourceExportToXMLHDF5_KeepPyramid {} /*implements Command{

    private static final Logger LOGGER = Logger.getLogger( BdvSourceExportToXMLHDF5_KeepPyramid.class.getName() );

    @Parameter(label="Sources to save ('2,3-5'), starts at 0")
    String index_srcs_to_save;

    @Parameter(label = "BigDataViewer Frame")
    public BdvHandle bdv_h;

    @Parameter
    int nThreads = 4;

    @Parameter
    File xmlFile;

    @Parameter
    int nTimePointBegin = 0;

    @Parameter
    int nTimePointEnd = 1;

    public void run() {

        ArrayList<Integer> idx_src = expressionToArray(index_srcs_to_save, i -> bdv_h.getViewerPanel().getState().getSources().size()-1);

        idx_src.forEach(idx -> System.out.println("id = "+idx));

        List<Source<?>> srcs = idx_src
                        .stream()
                        .map(idx -> bdv_h.getViewerPanel().getState().getSources().get(idx).getSpimSource())
                        .collect(Collectors.toList());

        MultiresolutionImgLoaderFromSources<?> imgLoader = new MultiresolutionImgLoaderFromSources(srcs);

        final int numTimepoints = this.nTimePointEnd - this.nTimePointBegin;

        final int numSetups = srcs.size();

        final ArrayList<TimePoint> timepoints = new ArrayList<>(numTimepoints);

        for (int t = nTimePointBegin; t < nTimePointEnd; ++t)
            timepoints.add(new TimePoint(t));

        final HashMap<Integer, BasicViewSetup> setups = new HashMap<>(numSetups);

        final SequenceDescriptionMinimal seq = new SequenceDescriptionMinimal(new TimePoints(timepoints), setups, imgLoader, null);

        //Map<Integer, ExportMipmapInfo> perSetupExportMipmapInfo = new HashMap<>();

        int idx_current_src = 0;

        for (Source<?> src: srcs) {
            RandomAccessibleInterval<?> refRai = src.getSource(0, 0);

            final VoxelDimensions voxelSize = src.getVoxelDimensions();
            long[] imgDims = new long[]{refRai.dimension(0), refRai.dimension(1), refRai.dimension(2)};
            final FinalDimensions imageSize = new FinalDimensions(imgDims);

            // propose mipmap settings
            final ExportMipmapInfo mipmapSettings;

            final BasicViewSetup basicviewsetup = new BasicViewSetup(idx_current_src, src.getName(), imageSize, voxelSize);



            basicviewsetup.setAttribute(new Channel(1));

            setups.put(idx_current_src, basicviewsetup); // Hum hum, order according to hashmap size TODO check

            //perSetupExportMipmapInfo.put( basicviewsetup.getId(), mipmapInfo);

        }
        //---------------------- End of setup handling



        final int numCellCreatorThreads = Math.max( 1, nThreads - 1 );

        final WriteSequenceToHdf5.LoopbackHeuristic loopbackHeuristic =
                ( originalImg,
                  factorsToOriginalImg,
                  previousLevel,
                  factorsToPreviousLevel,
                  chunkSize ) ->
                {
                    if ( previousLevel < 0 )
                        return false;

                    if ( WriteSequenceToHdf5.numElements( factorsToOriginalImg )
                            / WriteSequenceToHdf5.numElements( factorsToPreviousLevel ) >= 8 )
                        return true;

                    return false;
                };

        final WriteSequenceToHdf5.AfterEachPlane afterEachPlane = usedLoopBack -> { };

        final ArrayList< Partition > partitions;
        partitions = null;

        final ProgressWriter progressWriter = new ProgressWriterIJ();
        LOGGER.info( "Starting export..." );

        String seqFilename = xmlFile.getAbsolutePath();//.getParent();
        if ( !seqFilename.endsWith( ".xml" ) )
            seqFilename += ".xml";
        final File seqFile = new File( seqFilename );
        final File parent = seqFile.getParentFile();
        if ( parent == null || !parent.exists() || !parent.isDirectory() )
        {
            LOGGER.severe( "Invalid export filename " + seqFilename );
            return;
        }
        final String hdf5Filename = seqFilename.substring( 0, seqFilename.length() - 4 ) + ".h5";
        final File hdf5File = new File( hdf5Filename );
        boolean deflate = false;
        {
            WriteSequenceToHdf5.writeHdf5File( seq, perSetupExportMipmapInfo, deflate, hdf5File, loopbackHeuristic, afterEachPlane, numCellCreatorThreads, new SubTaskProgressWriter( progressWriter, 0, 0.95 ) );
        }

        // write xml sequence description
        final Hdf5ImageLoader hdf5Loader = new Hdf5ImageLoader( hdf5File, partitions, null, false );
        final SequenceDescriptionMinimal seqh5 = new SequenceDescriptionMinimal( seq, hdf5Loader );

        final ArrayList<ViewRegistration> registrations = new ArrayList<>();
        for ( int t = 0; t < numTimepoints; ++t )
            for ( int s = 0; s < numSetups; ++s )
                registrations.add( new ViewRegistration( t, s, getSrcTransform(srcs.get(s),t,0)));

        final File basePath = seqFile.getParentFile();
        final SpimDataMinimal spimData = new SpimDataMinimal( basePath, seqh5, new ViewRegistrations( registrations ) );

        //------------------------------------ SAVING NOW!

        try
        {
            new XmlIoSpimDataMinimal().save( spimData, seqFile.getAbsolutePath() );
            progressWriter.setProgress( 1.0 );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
        LOGGER.info( "Done" );

        idx_current_src++;

    }

    static public AffineTransform3D getSrcTransform(Source< ? > src, int timepoint, int level) {
        AffineTransform3D at = new AffineTransform3D();
        src.getSourceTransform(timepoint, level,at);
        return at;
    }

    static public ArrayList<Integer> expressionToArray(String expression, Function<Integer, Integer> fbounds) {
        String[] splitIndexes = expression.split(",");
        ArrayList<Integer> arrayOfIndexes = new ArrayList<>();
        for (String str : splitIndexes) {
            str.trim();
            if (str.contains(":")) {
                // Array of source, like 2:5 = 2,3,4,5
                String[] boundIndex = str.split(":");
                if (boundIndex.length==2) {
                    try {
                        int b1 = fbounds.apply(Integer.valueOf(boundIndex[0].trim()));
                        int b2 = fbounds.apply(Integer.valueOf(boundIndex[1].trim()));
                        if (b1<b2) {
                            for (int index = b1; index <= b2; index++) {
                                arrayOfIndexes.add(index);
                            }
                        }  else {
                            for (int index = b2; index >= b1; index--) {
                                arrayOfIndexes.add(index);
                            }
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Number format problem with expression:"+str+" - Expression ignored");
                    }
                } else {
                    LOGGER.warning("Cannot parse expression "+str+" to pattern 'begin-end' (2-5) for instance, omitted");
                }
            } else {
                // Single source
                try {
                    if (str.trim().equals("*")) {
                        int maxIndex = fbounds.apply(-1);
                        for (int index = 0; index <=maxIndex; index++) {
                            arrayOfIndexes.add(index);
                        }
                    } else {
                        int index = fbounds.apply(Integer.valueOf(str.trim()));
                        arrayOfIndexes.add(index);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warning("Number format problem with expression:"+str+" - Expression ignored");
                }
            }
        }
        return arrayOfIndexes;
    }

}

/*

 */