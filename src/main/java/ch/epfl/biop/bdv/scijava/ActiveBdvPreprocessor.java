package ch.epfl.biop.bdv.scijava;
import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.BdvCreateWindow;
import net.imagej.display.process.SingleInputPreprocessor;

import org.scijava.Priority;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.CommandService;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Fills single, unresolved module inputs with the active {@link BdvHandle},
 * <em>or a newly created one if none</em>.
 * <p>
 * This behavior differs from other {@link SingleInputPreprocessor}s so that all
 * SciView operations can be used to create a SciView window on demand.
 * </p>
 *
 * @author Curtis Rueden
 */
@Plugin(type = PreprocessorPlugin.class, priority = Priority.VERY_HIGH)
public class ActiveBdvPreprocessor extends SingleInputPreprocessor<BdvHandle>  {

    @Parameter
    private ObjectService os;

    @Parameter
    CommandService cs;

    @Parameter
    GuavaWeakCacheService cacheService;

    public ActiveBdvPreprocessor() {
        super( BdvHandle.class );
    }

    // -- SingleInputProcessor methods --

    @Override
    public BdvHandle getValue() {

        List<BdvHandle> bdvhs = os.getObjects(BdvHandle.class);

        if ((bdvhs == null)||(bdvhs.size()==0)) {
            try {
                return (BdvHandle)
                        cs.run(BdvCreateWindow.class, true,
                            "is2D", false,
                            "windowTitle", "Bdv",
                            "px",0,
                            "py",0,
                            "pz",0,
                            "s",1).get().getOutput("bdv_h");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (bdvhs.size()==1) {
            return bdvhs.get(0);
        } else {

            // Get the one with the most recent focus ?
            Optional<BdvHandle> bdvh = bdvhs.stream().filter(b -> b.getViewerPanel().hasFocus()).findFirst();
            if (bdvh.isPresent()) {
                return bdvh.get();
            } else {
                if (cacheService.get("LAST_ACTIVE_BDVH")!=null) {
                    WeakReference<BdvHandle> wr_bdv_h = (WeakReference<BdvHandle>) cacheService.get("LAST_ACTIVE_BDVH");
                    return wr_bdv_h.get();
                } else {
                    return null;
                }

            }
        }
    }

}
