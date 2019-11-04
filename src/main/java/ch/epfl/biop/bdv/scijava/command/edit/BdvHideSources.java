package ch.epfl.biop.bdv.scijava.command.edit;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Display>Hide Sources")
public class BdvHideSources implements Command {

    @Parameter
    BdvHandle bdvh;

    @Parameter(label="Indexes ('0,3:5'), of the sources to process")
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
