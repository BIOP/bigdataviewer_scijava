package ch.epfl.biop.bdv.scijava.command.display.window;

import bdv.util.BdvHandle;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.ItemIO;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Display>Get Current Location",
        label="Get current location of Bdv window",
        description="Scijava Command which returns the current transform ( = location ) of a Bdv window\n" +
                " This correspond to storing the current view of a Bdv window\n" +
                " Output: an AffineTransform3D object which corresponds to the current view of the input Bdv window\n" +
                " an optional name can be given in order to label this view.\n" +
                " The link between the name and the affine transform is stored in the cache service")
public class BdvWindowGetCurrentTransform implements Command {

    @Parameter(label = "Bdv Window")
    BdvHandle bdv_h;

    @Parameter(type = ItemIO.OUTPUT, label = "AffineTransform3D object which corresponds to the current view of the bdv window")
    AffineTransform3D at3D;

    @Parameter(label = "Label for current Bdv Location")
    String locationName;

    // Cache service used to link the AffineTransform object and its associated locationName
    @Parameter
    GuavaWeakCacheService cs;

    public void run() {
        at3D = new AffineTransform3D();
        bdv_h.getViewerPanel().getState().getViewerTransform(at3D);
        cs.put(at3D, locationName);
    }
}
