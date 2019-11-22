package ch.epfl.biop.bdv.scijava.command.display;

import bdv.util.BdvHandle;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Set Current Location")
public class BdvSetCurrentTransform implements Command {
    @Parameter
    BdvHandle bdv_h;

    @Parameter()
    AffineTransform3D at3D;

    public void run() {
        bdv_h.getViewerPanel().setCurrentViewerTransform(at3D.copy());
        bdv_h.getViewerPanel().requestRepaint();
    }
}