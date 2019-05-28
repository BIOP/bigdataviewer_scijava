package ch.epfl.biop.bdv.scijava;

import java.io.File;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import io.scif.config.SCIFIOConfig;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.SCIFIOImgPlus;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Plugin(type = Command.class,menuPath = "Plugins>BigDataViewer>SciJava>Open With SCIFIO + BioFormats (SciJava)")
public class OpenBioFormatPlugInSciJava implements Command
{

    private static final Logger LOGGER = Logger.getLogger( OpenBioFormatPlugInSciJava.class.getName() );

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

            List<SCIFIOImgPlus<?>> imgsPlus;
            BdvOptions options = BdvOptions.options();
            if (createNewWindow == false && bdv_h!=null) {
                options.addTo(bdv_h);
            }

            try {

                SCIFIOConfig cfg = new SCIFIOConfig();

                // Transform sourceIndexString to ArrayList of indexes
                ArrayList<Integer> sourceIndexes = commaSeparatedListToArray(sourceIndexString);

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
                        bdv_h = BdvFunctions.show(img, "Test", options.is2D()).getBdvHandle();
                    } else {
                        // Appends to existing Bdv instance
                        bdv_h = BdvFunctions.show(img, "Test", options.is2D()).getBdvHandle();
                        options.addTo(bdv_h);
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


    /**
     * Convert a comma separated list of indexes into an arraylist of integer
     *
     * For instance 1,2,5-7,10-12,14 returns an ArrayList containing
     * [1,2,5,6,7,10,11,12,14]
     *
     * Invalid format are ignored and an error message is displayed
     *
     * @param expression
     * @return list of indexes in ArrayList
     */

    static public ArrayList<Integer> commaSeparatedListToArray(String expression) {
        String[] splitIndexes = expression.split(",");
        ArrayList<java.lang.Integer> arrayOfIndexes = new ArrayList<>();
        for (String str : splitIndexes) {
            str.trim();
            if (str.contains("-")) {
                // Array of source, like 2-5 = 2,3,4,5
                String[] boundIndex = str.split("-");
                if (boundIndex.length==2) {
                    try {
                        int binf = java.lang.Integer.valueOf(boundIndex[0].trim());
                        int bsup = java.lang.Integer.valueOf(boundIndex[1].trim());
                        for (int index = binf; index <= bsup; index++) {
                            arrayOfIndexes.add(index);
                        }
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Number format problem with expression:"+str+" - Expression ignored");
                    }
                } else {
                    LOGGER.warning("Cannot parse expression "+str+" to pattern 'begin-end' (2-5) for instance, omitted");
                }
            } else {
                // Single source
                try {
                    int index = java.lang.Integer.valueOf(str.trim());
                    arrayOfIndexes.add(index);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Number format problem with expression:"+str+" - Expression ignored");
                }
            }
        }
        return arrayOfIndexes;
    }


}
