package ch.epfl.biop.bdv.scijava.command.open;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.ItemIO;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.List;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Put Sources>SpimDataset")
public class BdvAppendSpimData implements Command {

    @Parameter
    AbstractSpimData spimData;

    @Parameter(type = ItemIO.BOTH)
    BdvHandle bdv_h;

    @Parameter
    GuavaWeakCacheService cs;

    @Override
    public void run() {
        List<BdvStackSource<?>> lbss = BdvFunctions.show(spimData, BdvOptions.options().addTo(bdv_h));
        bdv_h = lbss.get(0).getBdvHandle();
        cs.put(spimData, lbss);
    }
}
