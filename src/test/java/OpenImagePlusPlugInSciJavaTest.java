import ch.epfl.biop.bdv.scijava.command.open.BdvAppendImagePlus;
import ij.IJ;
import ij.ImagePlus;
import net.imagej.ImageJ;
import org.scijava.command.CommandService;

public class OpenImagePlusPlugInSciJavaTest {

    public static void main( final String[] args )
    {
        ImagePlus image = IJ.openImage("http://imagej.nih.gov/ij/images/confocal-series.zip");
        image.show();
        final net.imagej.ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.get(CommandService.class).run(BdvAppendImagePlus.class,true, "imp", image);
    }

}
