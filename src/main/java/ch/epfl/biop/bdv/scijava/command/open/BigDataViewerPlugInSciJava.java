package ch.epfl.biop.bdv.scijava.command.open;

import bdv.spimdata.XmlIoSpimDataMinimal;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.util.BdvStackSource;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.ItemIO;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.List;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvCmdSuffix;
import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Open>XML Bdv Dataset"+ScijavaBdvCmdSuffix)
public class BigDataViewerPlugInSciJava implements Command {

    @Parameter(label = "XML File")
    public File file;

    @Parameter(label = "Open in new BigDataViewer window")
    public boolean createNewWindow;

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH, required = false)
    public BdvHandle bdv_h;

    @Parameter(type = ItemIO.OUTPUT)
    AbstractSpimData sd;

    @Parameter
    GuavaWeakCacheService cs;

    @Override
    public void run()
    {
        try
        {
            sd = new XmlIoSpimDataMinimal().load( file.getAbsolutePath() );
            BdvOptions options = BdvOptions.options();
            if (createNewWindow == false && bdv_h!=null) {
                options.addTo(bdv_h);
            }

            List<BdvStackSource<?>> lbss = BdvFunctions.show(sd, options);
            bdv_h = lbss.get(0).getBdvHandle(); // Returns bdv handle from index 0

            cs.put(sd,lbss); // If one needs to retrieve display options > limitation : only one place to display it

        }
        catch ( SpimDataException e )
        {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
