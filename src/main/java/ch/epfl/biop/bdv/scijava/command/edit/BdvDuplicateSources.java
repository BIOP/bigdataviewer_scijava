package ch.epfl.biop.bdv.scijava.command.edit;

import ch.epfl.biop.bdv.scijava.command.BDVSourceAndConverterFunctionalInterfaceCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.function.Function;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Duplicate Sources")
public class BdvDuplicateSources extends BDVSourceAndConverterFunctionalInterfaceCommand {

    public BdvDuplicateSources() {
        this.f = Function.identity();
    }
}
