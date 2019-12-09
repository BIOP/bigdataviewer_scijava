package ch.epfl.biop.bdv.scijava.command.open.samples;

import bdv.util.*;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.DynamicCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type=Command.class, initializer = "init",
        menuPath = ScijavaBdvRootMenu+"Bdv>Put Sources>Samples>Bdv example source - Fractal (Gray)",
        label = "Adds the mandelbrot set into a bdv window ( gray level  between 0 and 255)")
public class GrayMandelbrotCommand extends DynamicCommand {

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h;

    public int maxIterations = 255;

    public void run() {

        Interval interval = new FinalInterval(
                new long[]{ -2, -1, -0},
                new long[]{ 1, 1, 0 });

        RealRandomAccessible<UnsignedShortType> rra = new Procedural3DImageShort(
                p -> {
                        double re = p[0];
                        double im = p[1];
                        int i = 0;
                        for ( ; i < maxIterations; ++i )
                        {
                            final double squre = re * re;
                            final double squim = im * im;
                            if ( squre + squim > 4 )
                                break;
                            im = 2 * re * im + p[1];
                            re = squre - squim + p[0];
                        }
                        return i;
                }
        ).getRRA();

        BdvOptions options = BdvOptions.options().addTo(bdv_h);
        BdvStackSource bss = BdvFunctions.show( rra, interval, "Mandelbrot Set", options );
        bss.setDisplayRange(0,255);
        bss.setColor(new ARGBType(ARGBType.rgba(101,164,227,255)));
        bdv_h = bss.getBdvHandle();

    }


}
