import ch.epfl.biop.bdv.scijava.command.OpenSciFIOPlugInSciJava;
import net.imagej.ImageJ;

public class OpenBioFormatPlugInSciJavaTest {
    public static void main(String[] args) {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(OpenSciFIOPlugInSciJava.class, true);
    }
}
