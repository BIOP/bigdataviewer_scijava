import ch.epfl.biop.bdv.scijava.command.open.BdvAppendWithSciFIO;
import net.imagej.ImageJ;

public class OpenBioFormatPlugInSciJavaTest {
    public static void main(String[] args) {
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(BdvAppendWithSciFIO.class, true);
    }
}
