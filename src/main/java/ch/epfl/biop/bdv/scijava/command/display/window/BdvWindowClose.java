package ch.epfl.biop.bdv.scijava.command.display.window;

import bdv.util.BdvHandle;
import bdv.util.BdvHandleHelper;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Display>Close Bdv Window",
        label="Close Bdv Window",
        description = "Scijava Command which closes a BdvHandle window\n" +
                " The convert service is used to find the BdvHandle from its String representation.\n" +
                " Valid Strings are:\n" +
                " - the title of the JFrame containing the ViewerPanel of the BdvHandle Object\n" +
                " - the result of the toString() method of the BdvHandle Object (= default SciJava name)\n" +
                " -> Assumes the Bdv is containing within a single JFrame")
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
