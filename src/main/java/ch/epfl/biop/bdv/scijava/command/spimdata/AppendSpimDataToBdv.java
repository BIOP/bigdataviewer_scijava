package ch.epfl.biop.bdv.scijava.command.spimdata;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import ch.epfl.biop.bdv.bioformats.BioformatsBdvDisplayHelper;
import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.List;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Add to Bdv>SpimDataset")
public class AppendSpimDataToBdv implements Command {

    @Parameter
    AbstractSpimData spimData;

    @Parameter
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
