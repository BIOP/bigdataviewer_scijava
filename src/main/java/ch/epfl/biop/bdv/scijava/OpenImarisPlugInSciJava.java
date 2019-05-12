package ch.epfl.biop.bdv.scijava;

import java.io.File;
import java.io.IOException;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import bdv.img.imaris.Imaris;
import bdv.spimdata.SpimDataMinimal;

@Plugin(type = Command.class,menuPath = "Plugins>BigDataViewer>SciJava>Open Imaris (experimental) (SciJava)")
public class OpenImarisPlugInSciJava implements Command
{
    @Parameter(label = "Imaris File")
    public File file;

    @Parameter(label = "Open in new BigDataViewer window")
    public boolean createNewWindow;

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH, required = false)
    public BdvHandle bdv_h;

    @Override
    public void run()
    {
        try
        {
            final SpimDataMinimal spimData = Imaris.openIms( file.getAbsolutePath() );
            BdvOptions options = BdvOptions.options();
            if (createNewWindow == false && bdv_h!=null) {
                options.addTo(bdv_h);
            }
            bdv_h = BdvFunctions.show( spimData, options ).get(0).getBdvHandle(); // Returns handle from index 0

        }
        catch ( final IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
