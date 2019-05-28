package ch.epfl.biop.bdv.scijava;


import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.util.volatiles.VolatileViews;
import net.imagej.ImgPlus;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "Plugins>BigDataViewer>SciJava>Open Current Image - ImgLib2 (SciJava)")
public class OpenCurrentImgPlusPlugInSciJava implements Command {

    @Parameter(label = "Open in new BigDataViewer window")
    public boolean createNewWindow;

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH, required = false)
    public BdvHandle bdv_h;

    @Parameter(label = "Image Title")
    public String title = "New Title";

    @Parameter(label="Volatile view (works in certain cases only)")
    boolean wrapAsVolatile = true;

    @Parameter(type = ItemIO.INPUT)
    ImgPlus img;

    @Override
    public void run() {
        BdvOptions options = BdvOptions.options();
        if (createNewWindow == false && bdv_h!=null) {
            options.addTo(bdv_h);
        }
        if (wrapAsVolatile) {
            bdv_h = BdvFunctions.show(
                    VolatileViews.wrapAsVolatile(img),
                    title, options).getBdvHandle();
        } else {
            bdv_h = BdvFunctions.show(
                    img,
                    title, options).getBdvHandle();
        }
    }
}
