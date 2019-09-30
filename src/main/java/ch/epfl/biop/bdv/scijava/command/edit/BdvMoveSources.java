package ch.epfl.biop.bdv.scijava.command.edit;

import ch.epfl.biop.bdv.scijava.command.BDVSourceFunctionalInterfaceCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.function.Function;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Edit>Clone Source")
public class BdvMoveSources  extends BDVSourceFunctionalInterfaceCommand {

    public BdvMoveSources() {
        this.f = Function.identity();
    }
}
