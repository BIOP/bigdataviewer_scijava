package ch.epfl.biop.bdv.scijava.command.edit;

import ch.epfl.biop.bdv.scijava.command.BdvSourceAndConverterFunctionalInterfaceCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Removes Source being present in a Bdv Frame
 */

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Remove Sources",
        label = "Remove source from a Bdv Window",
        description = "Removing a source can cause issue at the moment and indexation problems," +
                "especially when working with SpimData. Try to avoid this command. One option" +
                "is to create a new Bdv Window and transfer only the needed source through the" +
                "BdvSourcesDuplicate command.")
public class BdvSourcesRemove extends BdvSourceAndConverterFunctionalInterfaceCommand {

    public BdvSourcesRemove() {
        this.f = (src) -> null;
    }
}
