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
import java.util.List;

@Plugin(type = Command.class,menuPath = "Plugins>BigDataViewer>SciJava>Open With BioFormats (SciJava)")
public class OpenBioFormatPlugInSciJava implements Command
{
    @Parameter(label = "Image File")
    public File file;

    @Parameter(label = "Open in new BigDataViewer window")
    public boolean createNewWindow;

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH, required = false)
    public BdvHandle bdv_h;

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
                cfg.imgOpenerSetOpenAllImages(true);
                imgsPlus = opener.openImgs(file.getAbsolutePath(), cfg);

                System.out.println("on a "+imgsPlus.size()+" images");

                for (SCIFIOImgPlus img: imgsPlus) {
                    //System.out.println("x size = "+img.dimension(0));
                    //System.out.println("y size = "+img.dimension(1));
                    bdv_h = BdvFunctions.show(img,"Test", options).getBdvHandle();
                }
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
