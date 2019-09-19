package ch.epfl.biop.bdv.scijava.gui.swing;

import bdv.util.BdvHandle;
import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import java.awt.*;

@Plugin(type = DisplayViewer.class)
public class SwingAbstractSpimDataDisplay extends
        EasySwingDisplayViewer<AbstractSpimData> {

    public SwingAbstractSpimDataDisplay()
    {
        super( AbstractSpimData.class );
    }

    @Override
    protected boolean canView(AbstractSpimData abstractSpimData) {
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
        textInfo.setText(asd.toString());
    }

    JPanel panelInfo;
    JLabel nameLabel;
    JTextArea textInfo;

    AbstractSpimData asd;

    @Override
    protected JPanel createDisplayPanel(AbstractSpimData abstractSpimData) {
        this.asd = abstractSpimData;

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panelInfo = new JPanel();
        panel.add(panelInfo, BorderLayout.CENTER);
        nameLabel = new JLabel(asd.toString());
        panel.add(nameLabel, BorderLayout.NORTH);
        textInfo = new JTextArea();
        textInfo.setEditable(false);

        this.redraw();
        return panel;
    }
}
