package ch.epfl.biop.bdv.scijava.command.spimdata;

import bdv.spimdata.XmlIoSpimDataMinimal;
import bdv.util.*;
import mpicbg.spim.data.SpimDataException;
import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.IOException;
import java.util.Map;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"SpimDataset>Open>SpimDataset [BigDataServer]")
public class SpimdatasetOpenBigDataServer implements Command
{
    @Parameter(label = "Big Data Server URL")
    String urlServer = "http://fly.mpi-cbg.de:8081";

    @Parameter(label = "Dataset Name")
    String datasetName = "Drosophila";

    @Parameter(type = ItemIO.OUTPUT)
    AbstractSpimData spimData;

    @Override
    public void run()
    {
        try {
            Map<String,String> BDSList = BigDataServerUtilsSciJava.getDatasetList(urlServer);
            final String urlString = BDSList.get(datasetName);
            spimData = new XmlIoSpimDataMinimal().load(urlString);
        } catch (SpimDataException | IOException e) {
            e.printStackTrace();
        }
    }

}
