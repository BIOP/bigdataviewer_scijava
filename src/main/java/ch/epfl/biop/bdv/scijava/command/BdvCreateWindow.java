package ch.epfl.biop.bdv.scijava.command;

import bdv.util.*;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.ItemIO;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import java.awt.event.*;
import java.lang.ref.WeakReference;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;


@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Create Empty BDV Frame")
public class BdvCreateWindow implements Command {

    @Parameter
    public boolean is2D = false;

    @Parameter
    public String windowTitle = "Bdv";

    @Parameter(type = ItemIO.OUTPUT)
    public BdvHandle bdv_h;

    @Parameter
    public double px = 0, py = 0, pz = 0, s = 100;

    @Parameter
    GuavaWeakCacheService cacheService;

    @Parameter
    ObjectService os;

    @Override
    public void run() {
        BdvOptions opts = BdvOptions.options();
        if (is2D) {
            opts = opts.is2D();
        }

        // TODO : ask why BdvHandle constructor not public ?
        ArrayImg dummyImg = ArrayImgs.bytes(2, 2, 2);
        BdvStackSource bss = BdvFunctions.show(dummyImg, "dummy", opts.frameTitle(windowTitle));
        bdv_h = bss.getBdvHandle();
        AffineTransform3D at3D = new AffineTransform3D();
        at3D.translate(-px, -py, -pz);
        double scale = bdv_h.getViewerPanel().getWidth() / s;
        at3D.scale(scale, scale, 1);
        bdv_h.getViewerPanel().setCurrentViewerTransform(at3D);
        bdv_h.getViewerPanel().requestRepaint();
        bss.removeFromBdv();

        Runnable storeLastInCache = () -> {
            System.out.println("Focus gained " + bdv_h.toString());
            cacheService.put("LAST_FOCUSED_BDVH", new WeakReference<>(bdv_h));
        };

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(bdv_h.getViewerPanel());

        topFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                os.removeObject(bdv_h);
                e.getWindow().dispose();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                cacheService.put("LAST_ACTIVE_BDVH", new WeakReference<>(bdv_h));
            }
        });
    }
}
