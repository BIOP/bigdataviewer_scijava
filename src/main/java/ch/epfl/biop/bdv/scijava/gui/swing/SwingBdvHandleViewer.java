package ch.epfl.biop.bdv.scijava.gui.swing;

import bdv.util.Bdv;
import bdv.util.BdvHandle;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import java.awt.*;

@Plugin(type = DisplayViewer.class)
public class SwingBdvHandleViewer extends
        EasySwingDisplayViewer<BdvHandle> {

    public SwingBdvHandleViewer()
    {
        super( BdvHandle.class );
    }

    @Override
    protected boolean canView(BdvHandle bdv_h) {
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
        // Needs to update the display
        textInfo.setText(bdv_h.toString());

    }

    BdvHandle bdv_h = null;

    JPanel panelInfo;
    JLabel nameLabel;
    JTextArea textInfo;
    @Override
    protected JPanel createDisplayPanel(BdvHandle bdv_h) {
        this.bdv_h = bdv_h;
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panelInfo = new JPanel();
        panel.add(panelInfo, BorderLayout.CENTER);
        nameLabel = new JLabel(bdv_h.toString());
        panel.add(nameLabel, BorderLayout.NORTH);
        textInfo = new JTextArea();
        textInfo.setEditable(false);
        panelInfo.add(textInfo);
        this.redraw();
        return panel;
    }
}