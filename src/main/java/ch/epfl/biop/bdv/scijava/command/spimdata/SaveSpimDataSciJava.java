package ch.epfl.biop.bdv.scijava.command.spimdata;

import mpicbg.spim.data.SpimData;
import mpicbg.spim.data.XmlIoSpimData;
import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import mpicbg.spim.data.SpimDataException;
import org.scijava.plugin.Plugin;

import java.io.File;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,menuPath = ScijavaBdvRootMenu+"SpimDataset>Save SpimDataset")
public class SaveSpimDataSciJava implements Command {

    @Parameter
    AbstractSpimData spimData;

    @Parameter()
    File xmlFileName;

    @Override
    public void run() {
        try {
            System.out.println(xmlFileName.getAbsolutePath());
            spimData.setBasePath(xmlFileName.getParentFile());
            (new XmlIoSpimData()).save((SpimData) spimData, xmlFileName.getAbsolutePath());
        } catch (SpimDataException e) {
            e.printStackTrace();
        }
    }
}
