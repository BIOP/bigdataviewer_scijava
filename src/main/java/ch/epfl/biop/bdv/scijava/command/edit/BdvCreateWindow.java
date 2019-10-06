package ch.epfl.biop.bdv.scijava.command.edit;

import bdv.util.*;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;


@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"New Bdv Frame")
public class BdvCreateWindow implements Command {

    @Parameter
    public boolean is2D = false;

    @Parameter
    public String windowTitle = "Bdv";

    @Parameter(type = ItemIO.OUTPUT)
    public BdvHandle bdv_h;

    @Override
    public void run() {
        BdvOptions opts = BdvOptions.options();
        if (is2D) {
            opts = opts.is2D();
        }

        // TODO : ask why BdvHandle constructor not public ?
        ArrayImg dummyImg = ArrayImgs.bytes(2,2,2);
        bdv_h = BdvFunctions.show(dummyImg, "dummy", opts.frameTitle(windowTitle)).getBdvHandle();
    }
}