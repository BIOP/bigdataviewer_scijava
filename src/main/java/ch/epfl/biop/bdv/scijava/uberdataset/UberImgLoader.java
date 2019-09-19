package ch.epfl.biop.bdv.scijava.uberdataset;

import bdv.ViewerImgLoader;
import bdv.cache.CacheControl;
import bdv.img.cache.VolatileGlobalCellCache;
import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.generic.sequence.AbstractSequenceDescription;
import mpicbg.spim.data.generic.sequence.BasicSetupImgLoader;
import mpicbg.spim.data.sequence.MultiResolutionImgLoader;
import net.imglib2.Volatile;
import net.imglib2.cache.queue.BlockingFetchQueues;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.volatiles.*;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Image Loader for Uber Datasets = a dataset which can contain a list of other datasets
 * Limitation :
 * Cache controls not implemented! TODO
 *  @author nicolas.chiaruttini@epfl.ch, BIOP, EPFL
 */
public class UberImgLoader implements ViewerImgLoader, MultiResolutionImgLoader {

    // sequence description of uber dataset
    final AbstractSequenceDescription<?, ?, ?> sequenceDescription;

    // Links File to SpimDataMinimal
    Map<File, SpimDataMinimal> spimDataFromFiles = new HashMap<>();

    // Links view Setups to Setup Loaders
    HashMap<Integer, UberSetupLoader> setupLoaders = new HashMap<>();

    // ViewSetup id offset for each xml dataset file
    LinkedList<Integer> viewSetupIndexStart = new LinkedList<>();

    // xml datasets files contained in this uberdataset
    public List<File> files;

    // cache control ? NOT IMPLEMENTED TODO
    protected VolatileGlobalCellCache cache;

    // For logging
    public Consumer<String> log = (s) -> {}; // (s) -> System.out.println(s); for logging

    // For logging errors
    public Consumer<String> errlog = (s) -> System.err.println(s); // (s) -> {System.out.println(s)}; for logging

    /**
     * @param iSetup index of the viewsetup in the uber dataset
     * @return the xml dataset file which contains this viewSetup
     */
    public File getFileFromViewSetup(int iSetup) {
        int indexFile = 0;
        while (!(viewSetupIndexStart.get(indexFile)>iSetup)) {
            indexFile++;
        }
        return files.get(indexFile-1);
    }

    /**
     *  Construct the uber dataset image loader
     * @param files xml dataset files which will be wrapped into this uber dataset
     * @param sequenceDescription
     */
    public UberImgLoader (List<File> files, final AbstractSequenceDescription<?, ?, ?> sequenceDescription) {
        // Stores fields
        this.files = files;
        this.sequenceDescription = sequenceDescription;
        viewSetupIndexStart.add(0); // first file starts at 0
        // Opens each linked dataset
        files.stream().forEach(f -> {
            try {
                // Fetch spimdata minimal
                SpimDataMinimal sdm = new XmlIoSpimDataMinimal().load(f.getAbsolutePath());
                spimDataFromFiles.put(f, sdm);
                // How many viewsetups ?
                int nViewSetup = sdm.getSequenceDescription().getViewSetups().size();
                // Stores index of first one
                viewSetupIndexStart.add(viewSetupIndexStart.getLast()+nViewSetup);
            } catch (SpimDataException e) {
                e.printStackTrace();
            }
        });

        // Q : should we ignore  the sequence description ?

        // NOT CORRECTLY IMPLEMENTED YET
        final BlockingFetchQueues<Callable<?>> queue = new BlockingFetchQueues<>(1);
        cache = new VolatileGlobalCellCache(queue);
    }

    public UberSetupLoader<?,?> getSetupImgLoader(int setupId) {
        if (setupLoaders.containsKey(setupId)) {
            return setupLoaders.get(setupId);
        } else {
            File f = getFileFromViewSetup(setupId);
            log.accept("Getting setup indexed "+setupId);
            int setupIndexInOriginalFile = setupId - viewSetupIndexStart.get(files.indexOf(f));
            log.accept("Getting setup originally indexed "+setupIndexInOriginalFile+" in dataset file "+f.getAbsolutePath());

            BasicSetupImgLoader bsil = spimDataFromFiles
                    .get(f)
                    .getSequenceDescription()
                    .getImgLoader()
                    .getSetupImgLoader(setupIndexInOriginalFile);

            Type t = (Type) bsil.getImageType();

            if (t instanceof NumericType) {

                NumericType numType = (NumericType) t.copy();
                Volatile volType = getVolatileFromNumeric(numType);
                UberSetupLoader usl = new UberSetupLoader(numType,volType,bsil);
                setupLoaders.put(setupId,usl);
                return usl;
            } else {
                errlog.accept("Impossible to cast Type "+t.getClass()+" to "+NumericType.class);
                return null;
            }
        }
    }

    /**
     * This function probably exists elsewhere
     * @param numType numericType, which needs to be wrapped in volatile
     * @return corresponding volatile type
     */
    private Volatile getVolatileFromNumeric(NumericType numType) {
        if (numType instanceof ARGBType) return new VolatileARGBType();
        if (numType instanceof UnsignedByteType) return new VolatileUnsignedByteType();
        if (numType instanceof UnsignedShortType) return new VolatileUnsignedShortType();
        if (numType instanceof UnsignedIntType) return new VolatileUnsignedIntType();
        if (numType instanceof FloatType) return new VolatileFloatType();
        return null;
    }

    /**
     * TODO : proper implementation of cache controls (but what is this ?)
     * @return dummy cache control
     */
    @Override
    public CacheControl getCacheControl() {
        return cache;
    }
}
