package ch.epfl.biop.bdv.scijava.converter;

import bdv.util.BdvFunctions;
import ij.ImagePlus;
import net.imglib2.RandomAccessibleInterval;
import org.scijava.command.Command;
import org.scijava.convert.ConvertService;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Test Cvt ImagePlus")
public class PutRAIInOS implements Command {

    @Parameter
    ObjectService os;

    @Parameter
    ConvertService cs;

    @Parameter
    ImagePlus imp;

    @Override
    public void run() {
        RandomAccessibleInterval rai = cs.convert(imp, RandomAccessibleInterval.class);
        if (rai!=null) {
            System.out.println("on a un rai");
            os.addObject(rai);
            BdvFunctions.show(rai,"test");
        }
    }
}
