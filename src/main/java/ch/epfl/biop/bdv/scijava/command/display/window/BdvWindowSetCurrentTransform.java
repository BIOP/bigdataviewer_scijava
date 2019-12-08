package ch.epfl.biop.bdv.scijava.command.display.window;

import bdv.util.BdvHandle;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Display>Set Current Location",
        label="Set the location of the current view of a Bdv Window",
        description="Do not check whether the new view keeps a direct orthonormal view -> take care!")
public class BdvWindowSetCurrentTransform implements Command {
    @Parameter(label = "Input Bdv Window")
    BdvHandle bdvh;

    @Parameter(label = "Affine Transform specifying the Bdv window view location")
    AffineTransform3D at3D;

    public void run() {
        bdvh.getViewerPanel().setCurrentViewerTransform(at3D.copy());
        bdvh.getViewerPanel().requestRepaint();
    }
}
