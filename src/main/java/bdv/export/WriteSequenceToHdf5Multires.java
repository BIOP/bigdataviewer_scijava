package bdv.export;

import bdv.img.hdf5.Partition;
import mpicbg.spim.data.generic.sequence.AbstractSequenceDescription;
import mpicbg.spim.data.generic.sequence.BasicImgLoader;
import mpicbg.spim.data.generic.sequence.BasicSetupImgLoader;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import mpicbg.spim.data.sequence.ViewId;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class WriteSequenceToHdf5Multires extends WriteSequenceToHdf5 {
    /**
     * Create a hdf5 partition file containing image data for a subset of views
     * and timepoints in a chunked, mipmaped representation.
     *
     * Please note that the description of the <em>full</em> dataset must be
     * given in the <code>seq</code>, <code>perSetupResolutions</code>, and
     * <code>perSetupSubdivisions</code> parameters. Then only the part
     * described by <code>partition</code> will be written.
     *
     * @param seq
     *            description of the sequence to be stored as hdf5. (The
     *            {@link AbstractSequenceDescription} contains the number of
     *            setups and timepoints as well as an {@link BasicImgLoader}
     *            that provides the image data, Registration information is not
     *            needed here, that will go into the accompanying xml).
     * @param perSetupMipmapInfo
     *            this maps from setup {@link BasicViewSetup#getId() id} to
     *            {@link ExportMipmapInfo} for that setup. The
     *            {@link ExportMipmapInfo} contains for each mipmap level, the
     *            subsampling factors and subdivision block sizes.
     * @param deflate
     *            whether to compress the data with the HDF5 DEFLATE filter.
     * @param partition
     *            which part of the dataset to write, and to which file.
     * @param loopbackHeuristic
     *            heuristic to decide whether to create each resolution level by
     *            reading pixels from the original image or by reading back a
     *            finer resolution level already written to the hdf5. may be
     *            null (in this case always use the original image).
     * @param afterEachPlane
     *            this is called after each "plane of chunks" is written, giving
     *            the opportunity to clear caches, etc.
     * @param numCellCreatorThreads
     *            The number of threads that will be instantiated to generate
     *            cell data. Must be at least 1. (In addition the cell creator
     *            threads there is one writer thread that saves the generated
     *            data to HDF5.)
     * @param progressWriter
     *            completion ratio and status output will be directed here.
     */
    public static void writeHdf5PartitionFile(
            final AbstractSequenceDescription< ?, ?, ? > seq,
            final Map< Integer, ExportMipmapInfo > perSetupMipmapInfo,
            final boolean deflate,
            final Partition partition,
            final LoopbackHeuristic loopbackHeuristic,
            final AfterEachPlane afterEachPlane,
            final int numCellCreatorThreads,
            ProgressWriter progressWriter )
    {
        final int blockWriterQueueLength = 100;

        if ( progressWriter == null )
            progressWriter = new ProgressWriterConsole();
        progressWriter.setProgress( 0 );

        // get sequence timepointIds for the timepoints contained in this partition
        final ArrayList< Integer > timepointIdsSequence = new ArrayList<>( partition.getTimepointIdSequenceToPartition().keySet() );
        Collections.sort( timepointIdsSequence );
        final int numTimepoints = timepointIdsSequence.size();
        final ArrayList< Integer > setupIdsSequence = new ArrayList<>( partition.getSetupIdSequenceToPartition().keySet() );
        Collections.sort( setupIdsSequence );

        // get the BasicImgLoader that supplies the images
        final BasicImgLoader imgLoader = seq.getImgLoader();

        for ( final BasicViewSetup setup : seq.getViewSetupsOrdered() ) {
            final Object type = imgLoader.getSetupImgLoader( setup.getId() ).getImageType();
            if ( !( type instanceof UnsignedShortType) )
                throw new IllegalArgumentException( "Expected BasicImgLoader<UnsignedShortTyp> but your dataset has BasicImgLoader<"
                        + type.getClass().getSimpleName() + ">.\nCurrently writing to HDF5 is only supported for UnsignedShortType." );
        }


        // open HDF5 partition output file
        final File hdf5File = new File( partition.getPath() );
        if ( hdf5File.exists() )
            hdf5File.delete();
        final Hdf5BlockWriterThread writerQueue = new Hdf5BlockWriterThread( hdf5File, blockWriterQueueLength );
        writerQueue.start();

        // start CellCreatorThreads
        final CellCreatorThread[] cellCreatorThreads = createAndStartCellCreatorThreads( numCellCreatorThreads );

        // calculate number of tasks for progressWriter
        int numTasks = 1; // first task is for writing mipmap descriptions etc...
        for ( final int timepointIdSequence : timepointIdsSequence )
            for ( final int setupIdSequence : setupIdsSequence )
                if ( seq.getViewDescriptions().get( new ViewId( timepointIdSequence, setupIdSequence ) ).isPresent() )
                    numTasks++;
        int numCompletedTasks = 0;

        // write Mipmap descriptions
        for ( final Map.Entry< Integer, Integer > entry : partition.getSetupIdSequenceToPartition().entrySet() )
        {
            final int setupIdSequence = entry.getKey();
            final int setupIdPartition = entry.getValue();
            final ExportMipmapInfo mipmapInfo = perSetupMipmapInfo.get( setupIdSequence );
            writerQueue.writeMipmapDescription( setupIdPartition, mipmapInfo );
        }
        progressWriter.setProgress( ( double ) ++numCompletedTasks / numTasks );

        // write image data for all views to the HDF5 file
        int timepointIndex = 0;
        for ( final int timepointIdSequence : timepointIdsSequence )
        {
            final int timepointIdPartition = partition.getTimepointIdSequenceToPartition().get( timepointIdSequence );
            progressWriter.out().printf( "proccessing timepoint %d / %d\n", ++timepointIndex, numTimepoints );

            // assemble the viewsetups that are present in this timepoint
            final ArrayList< Integer > setupsTimePoint = new ArrayList<>();

            for ( final int setupIdSequence : setupIdsSequence )
                if ( seq.getViewDescriptions().get( new ViewId( timepointIdSequence, setupIdSequence ) ).isPresent() )
                    setupsTimePoint.add( setupIdSequence );

            final int numSetups = setupsTimePoint.size();

            int setupIndex = 0;
            for ( final int setupIdSequence : setupsTimePoint )
            {
                final int setupIdPartition = partition.getSetupIdSequenceToPartition().get( setupIdSequence );
                progressWriter.out().printf( "proccessing setup %d / %d\n", ++setupIndex, numSetups );

                @SuppressWarnings( "unchecked" )
                final RandomAccessibleInterval< UnsignedShortType > img = ( (BasicSetupImgLoader< UnsignedShortType >) imgLoader.getSetupImgLoader( setupIdSequence ) ).getImage( timepointIdSequence );
                final ExportMipmapInfo mipmapInfo = perSetupMipmapInfo.get( setupIdSequence );
                final double startCompletionRatio = ( double ) numCompletedTasks++ / numTasks;
                final double endCompletionRatio = ( double ) numCompletedTasks / numTasks;
                final ProgressWriter subProgressWriter = new SubTaskProgressWriter( progressWriter, startCompletionRatio, endCompletionRatio );

                writeViewToHdf5PartitionFile(
                        img, timepointIdPartition, setupIdPartition, mipmapInfo, false,
                        deflate, writerQueue, cellCreatorThreads, loopbackHeuristic, afterEachPlane, subProgressWriter );
            }
        }

        // shutdown and close file
        stopCellCreatorThreads( cellCreatorThreads );
        writerQueue.close();
        progressWriter.setProgress( 1.0 );
    }

}
