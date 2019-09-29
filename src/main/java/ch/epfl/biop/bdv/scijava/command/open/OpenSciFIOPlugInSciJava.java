package ch.epfl.biop.bdv.scijava.command.open;

import java.io.File;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import io.scif.config.SCIFIOConfig;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.logging.Logger;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvCmdSuffix;
import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,menuPath = ScijavaBdvRootMenu+"Open>Open with SCIFIO"+ScijavaBdvCmdSuffix)
public class OpenSciFIOPlugInSciJava implements Command
{

    private static final Logger LOGGER = Logger.getLogger( OpenSciFIOPlugInSciJava.class.getName() );

    @Parameter(label = "Image File")
    public File file;

    @Parameter(label = "Open in new BigDataViewer window")
    public boolean createNewWindow;

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH, required = false)
    public BdvHandle bdv_h;

    @Parameter(label="Source indexes ('2,3-5'), starts at 0")
    public String sourceIndexString = "0";

    @Override
    public void run()
    {
        try
        {
            ImgOpener opener = new ImgOpener();

            BdvOptions options = BdvOptions.options();

            if (createNewWindow) {
                bdv_h=null;
            }

            if (createNewWindow == false && bdv_h!=null) {
                options.addTo(bdv_h);
            }

            try {

                SCIFIOConfig cfg = new SCIFIOConfig();

                // Transform sourceIndexString to ArrayList of indexes
                ArrayList<Integer> sourceIndexes = CommandHelper.commaSeparatedListToArray(sourceIndexString);

                // Open each source independently
                sourceIndexes.stream().forEach( sourceIndex -> {

                    cfg.imgOpenerSetIndex(sourceIndex);
                    SCIFIOImgPlus img = opener.openImgs(file.getAbsolutePath(), cfg).get(0);

                    LOGGER.info(sourceIndex+" will be opened ");
                    LOGGER.info("imgdim="+img.numDimensions());
                    LOGGER.info("x size = "+img.dimension(0));
                    LOGGER.info("y size = "+img.dimension(1));

                    if (bdv_h==null) {
                        // Creates bdv instance if none is existing
                        if (img.numDimensions()==2) {
                            bdv_h = BdvFunctions.show(img, img.getName(), BdvOptions.options().is2D()).getBdvHandle();
                        } else {
                            bdv_h = BdvFunctions.show(img, img.getName(), BdvOptions.options()).getBdvHandle();
                        }
                    } else {
                        // Appends to existing Bdv instance
                        if (img.numDimensions()==2) {
                            bdv_h = BdvFunctions.show(img, img.getName(), BdvOptions.options().addTo(bdv_h).is2D()).getBdvHandle();
                        } else {
                            bdv_h = BdvFunctions.show(img, img.getName(), BdvOptions.options().addTo(bdv_h)).getBdvHandle();
                        }
                    }
                });

            }
            catch (final ImgIOException e) {
                e.printStackTrace();
            }

        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }




}
