package ch.epfl.biop.bdv.scijava.command.display;

import bdv.util.BdvHandle;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.ItemIO;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Get Current Location")
public class BdvGetCurrentTransform implements Command {

    @Parameter
    BdvHandle bdv_h;

    @Parameter(type = ItemIO.OUTPUT)
    AffineTransform3D at3D;

    @Parameter
    String locationName;

    @Parameter
    GuavaWeakCacheService cs;

    public void run() {
        at3D = new AffineTransform3D();
        bdv_h.getViewerPanel().getState().getViewerTransform(at3D);
        cs.put(at3D, locationName);
    }
}
