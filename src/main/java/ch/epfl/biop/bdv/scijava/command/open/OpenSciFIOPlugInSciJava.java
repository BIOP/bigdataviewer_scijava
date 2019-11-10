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

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,menuPath = ScijavaBdvRootMenu+"Bdv>Add>Image File [SCIFIO]")
public class OpenSciFIOPlugInSciJava implements Command
{

    private static final Logger LOGGER = Logger.getLogger( OpenSciFIOPlugInSciJava.class.getName() );

    @Parameter(label = "Image File")
    public File file;

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h;

    @Parameter(label="Source indexes ('2,3:5'), starts at 0")
    public String sourceIndexString = "0";

    @Override
    public void run()
    {
        try
        {
            ImgOpener opener = new ImgOpener();

            BdvOptions options = BdvOptions.options().addTo(bdv_h);

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

                    {
                        // Appends to existing Bdv instance
                        if (img.numDimensions()==2) {
                            bdv_h = BdvFunctions.show(img, img.getName(), options.is2D()).getBdvHandle();
                        } else {
                            bdv_h = BdvFunctions.show(img, img.getName(), options.addTo(bdv_h)).getBdvHandle();
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
