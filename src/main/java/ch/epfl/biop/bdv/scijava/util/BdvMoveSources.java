package ch.epfl.biop.bdv.scijava.util;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.function.Function;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;


@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Clone Source", initializer = "initParams")
public class BdvMoveSources  extends BDVSourceFunctionalInterfaceCommand{

    public BdvMoveSources() {
        this.f = Function.identity();
    }
}
