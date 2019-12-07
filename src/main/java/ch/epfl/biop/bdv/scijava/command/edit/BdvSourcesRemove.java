package ch.epfl.biop.bdv.scijava.command.edit;

import ch.epfl.biop.bdv.scijava.command.BdvSourceAndConverterFunctionalInterfaceCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Removes Source being present in a Bdv Frame
 */

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Remove Sources")
public class BdvSourcesRemove extends BdvSourceAndConverterFunctionalInterfaceCommand {

    public BdvSourcesRemove() {
        this.f = (src) -> null;
    }
}
