import ch.epfl.biop.bdv.scijava.command.spimdata.SpimdatasetOpenXML;
import net.imagej.ImageJ;

public class BigDataViewerPlugInSciJavaTest {

    public static void main(String[] args) {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(SpimdatasetOpenXML.class, true);
    }
}
