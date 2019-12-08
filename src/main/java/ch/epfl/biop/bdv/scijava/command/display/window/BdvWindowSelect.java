package ch.epfl.biop.bdv.scijava.command.display.window;

import bdv.util.BdvHandle;
import bdv.util.BdvHandleHelper;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Display>Select Bdv Window",
        label="Puts in front/focus a Bdv Window",
        description="Useful for IJ1 Macro Language programming")
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
