package ch.epfl.biop.bdv.scijava.command.display;

import bdv.util.BdvHandle;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Set the location of the current view of a Bdv Window
 * Do not check whether the new view keeps a direct orthonormal view -> take care!
 */
@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Set Current Location")
public class BdvWindowSetCurrentTransform implements Command {
    @Parameter
    BdvHandle bdvh;

    @Parameter()
    AffineTransform3D at3D;

    public void run() {
        bdvh.getViewerPanel().setCurrentViewerTransform(at3D.copy());
        bdvh.getViewerPanel().requestRepaint();
    }
}
