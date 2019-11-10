package ch.epfl.biop.bdv.scijava.gui.swing;

import ch.epfl.biop.bdv.scijava.command.display.SynchronizeBDV;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import java.awt.*;

@Plugin(type = DisplayViewer.class)
public class SwingSyncBdvHandleViewer extends
        EasySwingDisplayViewer<SynchronizeBDV.SyncBdvHandle> {


    public SwingSyncBdvHandleViewer()
    {
        super( SynchronizeBDV.SyncBdvHandle.class );
    }

    @Override
    protected boolean canView(SynchronizeBDV.SyncBdvHandle syncBdvHandle) {
        return true;
    }

    @Override
    protected void redoLayout() {

    }

    @Override
    protected void setLabel(String s) {

    }

    @Override
    protected void redraw() {

    }

    SynchronizeBDV.SyncBdvHandle syncBdvHandle;
    JPanel mainPanel;

    @Override
    protected JPanel createDisplayPanel(SynchronizeBDV.SyncBdvHandle syncBdvHandle) {
        this.syncBdvHandle = syncBdvHandle;
        mainPanel = new JPanel();

        mainPanel.setLayout(new BorderLayout());
        String str = "Synchronize "+syncBdvHandle.hMaster.toString()+" -> "+syncBdvHandle.hSlave.toString();
        mainPanel.add(new JLabel(str), BorderLayout.NORTH);

        final JToggleButton toggleButton = new JToggleButton("Disable");

        toggleButton.addActionListener(e -> {
            if (((JToggleButton)e.getSource()).isSelected()) {
                this.syncBdvHandle.stop();
                ((JToggleButton)e.getSource()).setText("Enable");
            } else {
                this.syncBdvHandle.start();
                ((JToggleButton)e.getSource()).setText("Disable");
            }
        });
        mainPanel.add(toggleButton, BorderLayout.CENTER);

        return mainPanel;
    }
}
