package ch.epfl.biop.bdv.scijava.command.display;

import bdv.util.BdvHandle;
import bdv.util.BdvHandleHelper;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Puts in front/focus the JFrame associated to a BdvHandle
 * Useful for IJ1 Macro Language programming
 */

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Select Bdv Window")
public class BdvWindowSelect implements Command {

    @Parameter(label = "Name of the Bdv window")
    String bdvh;

    @Parameter
    ConvertService cs;

    @Override
    public void run() {
        BdvHandle bdvhInstance = cs.convert(bdvh, BdvHandle.class);
        if (bdvhInstance!=null) {
            BdvHandleHelper.activateWindow(bdvhInstance);
        } else {
            System.out.println("No bdv window found with the name: "+bdvhInstance);
        }
    }
}
