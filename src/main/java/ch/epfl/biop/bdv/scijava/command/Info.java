package ch.epfl.biop.bdv.scijava.command;

import loci.common.DebugTools;
import net.imagej.ImageJ;

import org.scijava.command.Command;
import org.scijava.platform.PlatformService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.IOException;
import java.net.URL;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

/**
 * Command which opens a page towards the source code of this repository
 * @author Nicolas Chiaruttini, BIOP, EPFL
 */

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"SciJava Bdv Info")
public class Info implements Command {

    @Parameter
    PlatformService ps;

    @Override
    public void run() {
        try {
            ps.open(new URL("https://github.com/BIOP/bigdataviewer_scijava"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        DebugTools.enableLogging("INFO");
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(Info.class, true);
    }

    /**
     * Define the Menu position of all BDV SciJava Commands
     */
    final public static String ScijavaBdvRootMenu = "BDV_SciJava>";

    /**
     * Suffix added to previously existing commands to avoid confusion in Command Names
     */
    final public static String ScijavaBdvCmdSuffix = " (SciJava)";

    /**
     * Label for selecting indexes of sources within bdv windows
     */
    final public static String ScijavaBdvLabelIndexes = "Indexes of the sources, comma separated";

    final public static String ScijavaBdvDescriptionIndexes = "Multiple sources can be specified; 0,4,7 or range 3:5";

    final public static String ScijavaBdvHandleLabel = "Bdv window";

}
