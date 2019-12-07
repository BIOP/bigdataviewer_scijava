package ch.epfl.biop.bdv.scijava.command.display;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Scijava Command which hides bdv sources
 * Multiple sources can be specified through the input parameter sourceIndexString
 */
@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Hide Sources")
public class BdvSourcesHide implements Command {

    @Parameter(label = "Bdv Window")
    BdvHandle bdvh;

    @Parameter(label="Indexes ('0,3:5'), of the sources to hide")
    public String sourceIndexString = "0";

    @Override
    public void run() {
        CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .forEach(id ->
                        bdvh.getViewerPanel().getVisibilityAndGrouping().setSourceActive(
                                bdvh.getViewerPanel().getState().getSources().get(id).getSpimSource(),
                                false )
                );
    }
}
