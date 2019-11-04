package ch.epfl.biop.bdv.scijava.command.edit;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import net.imglib2.type.numeric.ARGBType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.ColorRGB;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Display>Set Sources Color")
public class BdvSetColor implements Command {

    @Parameter
    BdvHandle bdvh;

    @Parameter
    ColorRGB c;

    @Parameter(label="Indexes ('0,3:5'), of the sources to process")
    public String sourceIndexString = "0";

    @Override
    public void run() {

        ARGBType color = new ARGBType(ARGBType.rgba(c.getRed(), c.getGreen(), c.getBlue(),0));

        CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .forEach(id -> bdvh.getSetupAssignments().getConverterSetups().get(id).setColor(color));

    }
}
