package ch.epfl.biop.bdv.scijava.command.display.window;

import bdv.util.BdvHandle;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import java.util.Arrays;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Display>Synchronize 2 Bdvs",
        label="Synchronizes the location of 2 Bdv windows",
        description="Synchronizes the location of 2 Bdv windows\n" +
                " * One is the master = controlling the slave window\n" +
                " * A thread checks every syncDelayInMs ms if the views are identical or not, if not, then the slave window is updated\n" +
                " * The syncrnoization can be stopped temporarily thanke to the SwingSyncBdvHandleViewer class which is triggered\n" +
                " * Synchronization can be chained to synchrnoize more than two viewers")
public class BdvWindowSynchronize implements Command {

    @Parameter(label = "Master Bdv Window")
    public BdvHandle hMaster;

    @Parameter(label = "Slave Bdv Window")
    public BdvHandle hSlave;

    @Parameter(label = "Synchronization delay (ms)")
    public int syncDelayInMs = 100;

    @Parameter(type = ItemIO.OUTPUT)
    public SyncBdvHandle sbh;

    @Override
    public void run() {
        if (hMaster==hSlave) {
            System.err.println("BDV windows are identical : a very logical person would say that they are indeed already synchronized.");
        } else {
            sbh = new SyncBdvHandle();
            sbh.hMaster=hMaster;
            sbh.hSlave=hSlave;
            sbh.start();
        }
    }

    boolean bdvStillHere () {
        return ((hMaster.getViewerPanel()!=null)&&(hSlave.getViewerPanel()!=null));
    }

    public class SyncBdvHandle {
        public Thread synchronizer;
        public BdvHandle hMaster;
        public BdvHandle hSlave;

        public void start() {
            synchronizer  = new Thread(() -> {
                while (bdvStillHere()) {
                    try {
                        Thread.sleep(syncDelayInMs);
                        AffineTransform3D atM = new AffineTransform3D();
                        hMaster.getViewerPanel().getState().getViewerTransform(atM);
                        AffineTransform3D atS = new AffineTransform3D();
                        hSlave.getViewerPanel().getState().getViewerTransform(atS);
                        if (!Arrays.equals(atS.getRowPackedCopy(), atM.getRowPackedCopy())) {
                            hSlave.getViewerPanel().setCurrentViewerTransform(atM.copy());
                            hSlave.getViewerPanel().requestRepaint();
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            });
            synchronizer.start();
        }

        public void stop() {
            synchronizer.interrupt();
            try {
                synchronizer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
