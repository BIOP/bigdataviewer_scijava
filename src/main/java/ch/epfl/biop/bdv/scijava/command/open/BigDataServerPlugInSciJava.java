package ch.epfl.biop.bdv.scijava.command.open;

import bdv.spimdata.SpimDataMinimal;
import bdv.spimdata.XmlIoSpimDataMinimal;
import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import ij.ImagePlus;
import mpicbg.spim.data.SpimDataException;
import net.imagej.DatasetService;
import net.imagej.display.ImageDisplayService;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.IOException;
import java.util.Map;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvCmdSuffix;
import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Open>Dataset from BigDataServer"+ScijavaBdvCmdSuffix)
public class BigDataServerPlugInSciJava implements Command
{
    @Parameter(label = "Big Data Server URL")
    String urlServer = "http://fly.mpi-cbg.de:8081";

    @Parameter(label = "Dataset Name")
    String datasetName = "Drosophila";

    @Parameter(label = "Open in new BigDataViewer window")
    public boolean createNewWindow;

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH, required = false)
    public BdvHandle bdv_h;

    @Parameter
    DatasetService ds;

    @Parameter
    ImageDisplayService ids;

    @Override
    public void run()
    {
        ds.getDatasets().size();
        ds.getObjectService().getObjects(ImagePlus.class).size();
        ids.getImageDisplays().size();
        try {
            Map<String,String> BDSList = BigDataServerUtilsSciJava.getDatasetList(urlServer);
            final String urlString = BDSList.get(datasetName);

            final SpimDataMinimal spimData = new XmlIoSpimDataMinimal().load(urlString);// xmlFilename );
            BdvOptions options = BdvOptions.options();
            if (createNewWindow == false && bdv_h!=null) {
                options.addTo(bdv_h);
            }
            bdv_h = BdvFunctions.show( spimData, options ).get(0).getBdvHandle(); // Returns handle from index 0
        } catch (SpimDataException | IOException e) {
            e.printStackTrace();
        }
    }

}
