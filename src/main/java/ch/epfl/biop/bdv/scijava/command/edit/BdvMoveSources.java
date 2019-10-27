package ch.epfl.biop.bdv.scijava.command.edit;

import ch.epfl.biop.bdv.scijava.command.BDVSourceAndConverterFunctionalInterfaceCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.function.Function;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Edit>Duplicate Source")
public class BdvMoveSources  extends BDVSourceAndConverterFunctionalInterfaceCommand {

    public BdvMoveSources() {
        this.f = Function.identity();
    }
}
