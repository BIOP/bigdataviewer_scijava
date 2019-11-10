import ch.epfl.biop.bdv.scijava.command.spimdata.BigDataServerPlugInSciJava;
import net.imagej.ImageJ;

public class BigDataServerPlugInSciJavaTest {

    public static void main(String[] args) {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(BigDataServerPlugInSciJava.class, true,
                "urlServer","http://fly.mpi-cbg.de:8081",
                "datasetName", "Drosophila");
    }
}
