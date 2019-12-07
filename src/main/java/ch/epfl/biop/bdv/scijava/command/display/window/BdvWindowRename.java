package ch.epfl.biop.bdv.scijava.command.display.window;

import bdv.util.BdvHandle;
import bdv.util.BdvHandleHelper;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Renames the specified BdvWindow
 * Specifying a name facilitates the selection of a Bdv Window in IJ1 Macro language
 */
@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Rename Bdv Window")
public class BdvWindowRename implements Command {

    @Parameter(label = "Bdv Window", type = ItemIO.BOTH)
    BdvHandle bdvh;

    @Parameter(label = "New Bdv Window Title")
    String windowTitle;

    @Parameter
    ObjectService os;

    public void run() {
        windowTitle = BdvHandleHelper.getUniqueWindowTitle(os, windowTitle);
        BdvHandleHelper.setWindowTitle(bdvh, windowTitle);
    }

}
