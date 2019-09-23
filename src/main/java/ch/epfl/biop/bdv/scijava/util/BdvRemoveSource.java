package ch.epfl.biop.bdv.scijava.util;

import bdv.util.BdvHandle;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Removes Source being present in a Bdv Frame
 */

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Remove Source (unsupported)", initializer = "initParams")
public class BdvRemoveSource implements Command {

    @Parameter(label = "Bdv Frame", type = ItemIO.BOTH)
    BdvHandle bdv_h_in;

    @Override
    public void run() {

    }
}
