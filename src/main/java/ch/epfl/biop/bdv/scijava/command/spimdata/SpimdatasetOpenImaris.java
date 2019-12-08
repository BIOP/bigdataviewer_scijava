package ch.epfl.biop.bdv.scijava.command.spimdata;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

import bdv.img.imaris.Imaris;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvCmdSuffix;
import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

// TODO : Is this really necessary now ?

@Plugin(type = Command.class,menuPath = ScijavaBdvRootMenu+"SpimDataset>Open>SpimDataset [Imaris File]",
        label = "Command that opens a Spimdata dataset from an Imaris file. Click on Show to display it.")
public class SpimdatasetOpenImaris implements Command
{
    @Parameter(label = "Imaris File")
    public File file;

    @Parameter(label = "Open in new BigDataViewer window")
    public boolean createNewWindow;

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h;

    @Parameter(type = ItemIO.OUTPUT)
    AbstractSpimData spimData;

    @Parameter
    GuavaWeakCacheService cs;

    @Override
    public void run()
    {
        try
        {
            spimData = Imaris.openIms( file.getAbsolutePath() );
            List<BdvStackSource<?>> lbss = BdvFunctions.show(spimData, BdvOptions.options().addTo(bdv_h));
            bdv_h = lbss.get(0).getBdvHandle();
            cs.put(spimData, lbss);
        }
        catch ( final IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
