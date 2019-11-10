package ch.epfl.biop.bdv.scijava.converter;

import bdv.util.BdvHandle;
import bdv.viewer.SourceAndConverter;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;


@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Put ImagePlus into Bdv")
public class TakeSourceAndConverterTest implements Command {

    @Parameter
    SourceAndConverter soc;

    @Parameter
    BdvHandle bdv_h;

    @Override
    public void run() {
        bdv_h.getViewerPanel().getState().addSource(soc);
    }
}
