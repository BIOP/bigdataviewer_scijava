package ch.epfl.biop.bdv.scijava.command.open.samples;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.util.Procedural3DImageShort;
import net.imagej.display.ColorTables;
import net.imagej.lut.LUTService;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.display.ColorTable;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.scijava.ItemIO;
import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.convert.ConvertService;
import org.scijava.module.MutableModuleItem;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type=Command.class, initializer = "init",
        menuPath = ScijavaBdvRootMenu+"Bdv>Put Sources>Samples>Bdv example source - Wave 3D",
        label = "Procedurally generated wave3d image, with a lookuptable.")
public class Wave3DSampleCommand extends DynamicCommand {

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h;

    @Parameter
    private LUTService lutService;

    @Parameter(label = "LUT name", persist = false, callback = "nameChanged")
    private String choice = "Gray";

    @Parameter(required = false, label = "LUT", persist = false, visibility = ItemVisibility.TRANSIENT)
    private ColorTable table = ColorTables.GRAYS;

    @Parameter
    private ConvertService cs;

    // -- other fields --

    private Map<String, URL> luts = null;

    public void run() {
        Converter bvdLut = cs.convert(table, Converter.class);

        Interval interval = new FinalInterval(
                new long[]{ -100, -100, -100 },
                new long[]{ 100, 100, 100 });

        RealRandomAccessible<UnsignedShortType> rra = new Procedural3DImageShort(
                p -> (int) ((Math.sin(p[0]/20)*Math.sin(p[1]/40)*Math.sin(p[2]/5)+1)*100)
        ).getRRA();


        final RealRandomAccessible<ARGBType> convertedrra =
                Converters.convert(
                        rra,
                        bvdLut,
                        new ARGBType() );

        BdvOptions options = BdvOptions.options().addTo(bdv_h);

        bdv_h = BdvFunctions.show( convertedrra, interval, "Wave3D", options ).getBdvHandle();

    }

    // -- initializers --

    protected void init() {
        luts = lutService.findLUTs();
        final ArrayList<String> choices = new ArrayList<>();
        for (final Map.Entry<String, URL> entry : luts.entrySet()) {
            choices.add(entry.getKey());
        }
        Collections.sort(choices);
        final MutableModuleItem<String> input =
                getInfo().getMutableInput("choice", String.class);
        input.setChoices(choices);
        input.setValue(this, choices.get(0));
        nameChanged();
    }

    // -- callbacks --

    protected void nameChanged() {
        try {
            table = lutService.loadLUT(luts.get(choice));
        }
        catch (final Exception e) {
            // nada
        }
    }


}
