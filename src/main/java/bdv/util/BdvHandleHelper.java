package bdv.util;

import org.scijava.cache.CacheService;
import org.scijava.object.ObjectService;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;

public class BdvHandleHelper {

    public static void setBdvHandleCloseOperation(BdvHandle bdvh, ObjectService os, CacheService cs, boolean putWindowOnTop) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(bdvh.getViewerPanel());

        topFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                os.removeObject(bdvh);
                e.getWindow().dispose();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                cs.put("LAST_ACTIVE_BDVH", new WeakReference<>(bdvh));
            }
        });

        if (putWindowOnTop) {
            cs.put("LAST_ACTIVE_BDVH", new WeakReference<>(bdvh));
        }
    }
}
