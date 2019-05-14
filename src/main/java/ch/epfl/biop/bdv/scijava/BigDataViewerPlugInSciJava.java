package ch.epfl.biop.bdv.scijava;

import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import mpicbg.spim.data.SpimDataException;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = Command.class, menuPath = "Plugins>BigDataViewer>SciJava>Open XML/HDF5 (SciJava)")
public class BigDataViewerPlugInSciJava implements Command {

    @Parameter(label = "XML File")
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
            final SpimDataMinimal spimData = new XmlIoSpimDataMinimal().load( file.getAbsolutePath() );
            BdvOptions options = BdvOptions.options();
            if (createNewWindow == false && bdv_h!=null) {
                options.addTo(bdv_h);
            }
            bdv_h = BdvFunctions.show( spimData, options ).get(0).getBdvHandle(); // Returns handle from index 0

        }
        catch ( SpimDataException e )
        {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
