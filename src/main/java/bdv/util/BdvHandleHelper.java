package bdv.util;

import ij.plugin.frame.Recorder;
import org.scijava.cache.CacheService;
import org.scijava.object.ObjectService;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.List;

public class BdvHandleHelper {

    public static void setBdvHandleCloseOperation(BdvHandle bdvh, ObjectService os, CacheService cs, boolean putWindowOnTop) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(bdvh.getViewerPanel());

        topFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                os.removeObject(bdvh);
                e.getWindow().dispose();
                if (Recorder.record) {
                    // run("Select Bdv Window", "bdvh=bdv.util.BdvHandleFrame@e6c7718");
                    String cmdrecord = "run(\"Close Bdv Window\", \"bdvh=" + bdvh + "\");\n";
                    Recorder.recordString(cmdrecord);
                }
            }

            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                cs.put("LAST_ACTIVE_BDVH", new WeakReference<>(bdvh));
                // Very old school
                if (Recorder.record) {
                    // run("Select Bdv Window", "bdvh=bdv.util.BdvHandleFrame@e6c7718");
                    String cmdrecord = "run(\"Select Bdv Window\", \"bdvh=" + bdvh + "\");\n";
                    Recorder.recordString(cmdrecord);
                }
            }
        });

        if (putWindowOnTop) {
            cs.put("LAST_ACTIVE_BDVH", new WeakReference<>(bdvh));
        }
    }

    public static void activateWindow(BdvHandle bdvh) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(bdvh.getViewerPanel());
        topFrame.toFront();
        topFrame.requestFocus();
    }

    public static void closeWindow(BdvHandle bdvh) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(bdvh.getViewerPanel());
        topFrame.dispatchEvent( new WindowEvent(topFrame, WindowEvent.WINDOW_CLOSING));
    }

    public static void setWindowTitle(BdvHandle bdvh, String title) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(bdvh.getViewerPanel());
        topFrame.setTitle(title);
    }

    public static String getWindowTitle(BdvHandle bdvh) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(bdvh.getViewerPanel());
        return topFrame.getTitle();
    }

    public static String getUniqueWindowTitle(ObjectService os, String iniTitle) {
        List<BdvHandle> bdvs = os.getObjects(BdvHandle.class);
        boolean duplicateExist = true;
        String iniTitleF = iniTitle;
        while (duplicateExist) {
            duplicateExist = bdvs.stream().filter(bdv ->
                    (bdv.toString().equals(iniTitleF))||(getWindowTitle(bdv).equals(iniTitleF)))
                    .findFirst().isPresent();
            if (iniTitle.matches("^.+?\\d$")) {
                int idx = Integer.valueOf(iniTitle.substring(iniTitle.lastIndexOf("_")));
                iniTitle = iniTitle.substring(0, iniTitle.lastIndexOf("_"));
                iniTitle += (idx+1);
            } else {
                iniTitle+="_00";
                System.out.println(iniTitle);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return iniTitle;
    }
}
