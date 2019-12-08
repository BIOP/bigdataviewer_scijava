package ch.epfl.biop.bdv.scijava.command.display.sources;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import net.imglib2.type.numeric.ARGBType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.ColorRGB;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Display>Set Sources Color",
        label="Set the color of bdv sources",
        description="Set the color of bdv sources. Multiple sources can be specified.")

public class BdvSourcesSetColor implements Command {

    @Parameter(label = "Bdv Window")
    BdvHandle bdvh;

    @Parameter(label = "Color")
    ColorRGB c;

    @Parameter(label="Indexes ('0,3:5'), of the sources", description = "description test")
    public String sourceIndexString = "0";

    @Override
    public void run() {

        ARGBType color = new ARGBType(ARGBType.rgba(c.getRed(), c.getGreen(), c.getBlue(),0));

        CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .forEach(id -> bdvh.getSetupAssignments().getConverterSetups().get(id).setColor(color));

    }
}
