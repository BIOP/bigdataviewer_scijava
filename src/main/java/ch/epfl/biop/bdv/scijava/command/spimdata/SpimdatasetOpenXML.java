package ch.epfl.biop.bdv.scijava.command.spimdata;

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

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"SpimDataset>Open>SpimDataset [XML File]")
public class SpimdatasetOpenXML implements Command {

    @Parameter(label = "XML File")
    public File file;

    @Parameter(type = ItemIO.OUTPUT)
    AbstractSpimData sd;

    @Override
    public void run()
    {
        try
        {
            sd = new XmlIoSpimDataMinimal().load( file.getAbsolutePath() );
        }
        catch ( SpimDataException e )
        {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
