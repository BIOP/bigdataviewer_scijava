package ch.epfl.biop.bdv.scijava.converter;

import bdv.viewer.SourceAndConverter;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;


@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Test SOC")
public class TakeSourceAndConverterTest implements Command {

    @Parameter
    SourceAndConverter soc;

    @Override
    public void run() {
        System.out.println("On a un soc!");
    }
}
