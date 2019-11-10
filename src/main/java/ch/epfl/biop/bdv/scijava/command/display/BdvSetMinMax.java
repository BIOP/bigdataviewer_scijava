package ch.epfl.biop.bdv.scijava.command.display;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Set Sources Min Max Display")
public class BdvSetMinMax implements Command {
    @Parameter
    BdvHandle bdvh;

    @Parameter
    double min;

    @Parameter
    double max;

    @Parameter(label="Indexes ('0,3:5'), of the sources to process")
    public String sourceIndexString = "0";

    @Override
    public void run() {

        CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .forEach(id -> bdvh.getSetupAssignments().getConverterSetups().get(id).setDisplayRange(min,max));

    }
}
