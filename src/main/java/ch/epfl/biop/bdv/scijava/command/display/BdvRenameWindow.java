package ch.epfl.biop.bdv.scijava.command.display;

import bdv.util.BdvHandle;
import bdv.util.BdvHandleHelper;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Rename Bdv Window")
public class BdvRenameWindow implements Command {

    @Parameter(type = ItemIO.BOTH)
    BdvHandle bdvh;

    @Parameter
    String windowTitle;

    @Parameter
    ObjectService os;

    public void run() {
        windowTitle = BdvHandleHelper.getUniqueWindowTitle(os, windowTitle);
        BdvHandleHelper.setWindowTitle(bdvh, windowTitle);
    }

}
