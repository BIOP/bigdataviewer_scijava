package ch.epfl.biop.bdv.scijava.command.edit;

import ch.epfl.biop.bdv.scijava.command.BdvSourceAndConverterFunctionalInterfaceCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.function.Function;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Duplicate Sources",
        label = "Duplicate sources",
        description = "Sources are duplicated by reference. So any modification of one of the duplicated" +
                "source will affect all sources. One bug of this command is that the ConverterSetup is" +
                "not transfered -> It is not possible to change display settings (easily) on the " +
                "duplicated source.")
public class BdvSourcesDuplicate extends BdvSourceAndConverterFunctionalInterfaceCommand {

    public BdvSourcesDuplicate() {
        this.f = Function.identity();
    }
}
