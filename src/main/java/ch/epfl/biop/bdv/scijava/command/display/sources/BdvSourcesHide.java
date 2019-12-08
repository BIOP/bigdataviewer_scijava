package ch.epfl.biop.bdv.scijava.command.display.sources;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.*;

@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Display>Hide Sources",
        label="Hide bdv sources",
        description="Hide bdv sources. Multiple sources can be specified.")

public class BdvSourcesHide implements Command {

    @Parameter(label = ScijavaBdvHandleLabel)
    BdvHandle bdvh;

    @Parameter(label=ScijavaBdvLabelIndexes, description=ScijavaBdvDescriptionIndexes)
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
