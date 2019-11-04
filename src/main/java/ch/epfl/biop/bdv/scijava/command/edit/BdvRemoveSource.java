package ch.epfl.biop.bdv.scijava.command.edit;

import ch.epfl.biop.bdv.scijava.command.BDVSourceAndConverterFunctionalInterfaceCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Removes Source being present in a Bdv Frame
 */

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Edit>Remove Sources")
public class BdvRemoveSource extends BDVSourceAndConverterFunctionalInterfaceCommand {

    public BdvRemoveSource() {
        this.f = (src) -> null;
    }
}
