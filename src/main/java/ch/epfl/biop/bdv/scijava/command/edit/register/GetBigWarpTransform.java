package ch.epfl.biop.bdv.scijava.command.edit.register;

import bdv.img.WarpedSource;
import bdv.util.BWBdvHandle;
import bdv.util.BdvHandle;
import net.imglib2.realtransform.RealTransform;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Register>Get BigWarp Transform")
public class GetBigWarpTransform implements Command {

    @Parameter
    BdvHandle bdvh;

    @Parameter(type = ItemIO.OUTPUT)
    RealTransform realtransform;

    public void run() {
        if (bdvh instanceof BWBdvHandle) {
            realtransform = ((WarpedSource) ((BWBdvHandle)bdvh).getBW().getViewerFrameQ()
                    .getViewerPanel().getState().getSources().get(0).getSpimSource())
                    .getTransform();
        } else {
            System.err.println("Bdv Window given as an input to this command is not a BigWarp frame -> no transformation retrieved");
            System.err.println("Select a bdv BigWarp window and try again.");
        }
    }
}
