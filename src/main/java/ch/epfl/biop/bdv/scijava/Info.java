package ch.epfl.biop.bdv.scijava;

import net.imagej.ImageJ;

import org.scijava.command.Command;
import org.scijava.platform.PlatformService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.IOException;
import java.net.URL;

/**
 * Command which opens a page towards the source code of this repository
 * @author Nicolas Chiaruttini, BIOP, EPFL
 */

@Plugin(type = Command.class, menuPath = "Plugins>BigDataViewer>SciJava>Info")
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
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(Info.class, true);
    }
}
