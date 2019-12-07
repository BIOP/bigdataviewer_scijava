package ch.epfl.biop.bdv.scijava.command.display.window;

import bdv.util.BdvHandle;
import bdv.util.BdvHandleHelper;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Scijava Command which closes a BdvHandle window
 * The convert service is used to find the BdvHandle from its String representation.
 * Valid Strings are:
 * - the title of the JFrame containing the ViewerPanel of the BdvHandle Object
 * - the result of the toString() method of the BdvHandle Object (= default SciJava name)
 * -> Assumes the Bdv is containing wihtin a single JFrame
 */
@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Close Bdv Window")
public class BdvWindowClose implements Command {

    @Parameter(label = "Name of the Bdv Window")
    String bdvh;

    @Parameter
    ConvertService cs;

    @Override
    public void run() {
        BdvHandle bdvhInstance = cs.convert(bdvh, BdvHandle.class);
        if (bdvhInstance!=null) {
            BdvHandleHelper.closeWindow(bdvhInstance);
        } else {
            System.out.println("No bdv window found with the name: "+bdvhInstance);
        }
    }
}
