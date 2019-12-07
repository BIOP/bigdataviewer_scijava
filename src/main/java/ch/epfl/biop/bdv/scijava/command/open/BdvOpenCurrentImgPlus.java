package ch.epfl.biop.bdv.scijava.command.open;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import net.imagej.ImgPlus;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvCmdSuffix;
import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Put Sources>Current IJ1 Image [ImgLib2]")
public class BdvOpenCurrentImgPlus implements Command {

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h;

    @Parameter(type = ItemIO.INPUT)
    ImgPlus img;

    @Override
    public void run() {
        bdv_h = BdvFunctions.show(img, img.getName(), BdvOptions.options().addTo(bdv_h)).getBdvHandle();
    }
}
