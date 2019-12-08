package ch.epfl.biop.bdv.scijava.command.display.sources;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * TODO : solve issue in case no convertersetup is found
 */
@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Display>Set Sources Min Max Display",
        label="Set the min and max display values of bdv sources",
        description="Set the min and max display values of bdv sources. Multiple sources can be specified.")
public class BdvSourcesSetMinMax implements Command {

    @Parameter(label = "Bdv Window")
    BdvHandle bdvh;

    @Parameter(label = "Minimum display value")
    double min;

    @Parameter(label = "Maximum display value")
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
