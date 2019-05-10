import ch.epfl.biop.bdv.scijava.Info;
import net.imagej.ImageJ;
import org.junit.Test;
import org.scijava.command.CommandModule;
import java.util.concurrent.Future;

public class BDVScijavaTest {

    @Test
    public void run() throws Exception {
        // Arrange
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();

        // Act
        Future<CommandModule> m = ij.command().run(Info.class, true);

        // Assert
    }
}