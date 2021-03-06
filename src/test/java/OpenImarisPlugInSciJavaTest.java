import ch.epfl.biop.bdv.scijava.command.spimdata.SpimdatasetOpenImaris;
import net.imagej.ImageJ;

public class OpenImarisPlugInSciJavaTest {

    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(SpimdatasetOpenImaris.class, true);
    }
}
