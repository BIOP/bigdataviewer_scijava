package ch.epfl.biop.bdv.scijava.gui.swing;

import bdv.util.BdvHandle;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;

@Plugin(type = DisplayViewer.class)
public class SwingAffineTransformViewer  extends
        EasySwingDisplayViewer<AffineTransform> {


    public SwingAffineTransformViewer()
    {
        super( AffineTransform.class );
    }

    @Override
    protected boolean canView(AffineTransform affineTransform) {
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

    AffineTransform at;

    JPanel mainPanel;
    JTextArea ta;

    @Override
    protected JPanel createDisplayPanel(AffineTransform affineTransform) {
        this.at = affineTransform;
        mainPanel = new JPanel();
        ta = new JTextArea();
        ta.setText(at.toString());

        ta.setEditable(false);


        return mainPanel;
    }
}
