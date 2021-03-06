package ch.epfl.biop.bdv.scijava.command.spimdata;

import bdv.AbstractSpimSource;
import bdv.tools.transformation.TransformedSource;
import bdv.util.BdvStackSource;
import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.List;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Reflection java trick to access private method
 */
@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>SpimDataset>Update Bdv",
        label = "Updates the associated Bdv to a Spimdataset. If the spimdata object has been modified" +
                "then the transformations will be updated in the Bdv Window")
public class SpimdatasetUpdateBdvWindow implements Command {
    @Parameter
    AbstractSpimData spimData;

    @Parameter
    int timePoint;

    @Parameter
    GuavaWeakCacheService cs;

    List<BdvStackSource<?>> lbss = null;

    @Override
    public void run() {

        if (cs.get(spimData)!=null) {
            try {
                Method updateBdvSource = Class.forName("bdv.AbstractSpimSource").getDeclaredMethod("loadTimepoint", int.class);
                updateBdvSource.setAccessible(true);

                for (int ivs =0; ivs< spimData.getSequenceDescription().getViewSetupsOrdered().size();ivs++) {
                    lbss = (List<BdvStackSource<?>>) cs.get(spimData);
                    TransformedSource src = (TransformedSource) lbss.get(ivs).getSources().get(0).getSpimSource();
                    AbstractSpimSource ass = (AbstractSpimSource) src.getWrappedSource();

                    updateBdvSource.invoke(ass, timePoint);

                    if (lbss.get(ivs).getSources().get(0).asVolatile() != null) {

                        src = (TransformedSource) lbss.get(ivs).getSources().get(0).asVolatile().getSpimSource();
                        ass = (AbstractSpimSource) src.getWrappedSource();

                        updateBdvSource.invoke(ass, timePoint);
                    }
                }

                lbss.get(0).getBdvHandle().getViewerPanel().requestRepaint();

            } catch (ClassCastException e) {
                System.err.println("Scijava GuavaWeakCacheService cache do not contain the except class for spimdata "+spimData);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Could not find cached instance Bdv Window");
        }
    }
}
