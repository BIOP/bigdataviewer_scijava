import ch.epfl.biop.bdv.scijava.BigDataBrowserPlugInSciJava;
import net.imagej.ImageJ;

public class BigDataBrowserPlugInSciJavaTest {
    public static void main( final String[] args )
    {
        final net.imagej.ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(BigDataBrowserPlugInSciJava.class, true, "serverUrl", "http://fly.mpi-cbg.de:8081");
    }
}
