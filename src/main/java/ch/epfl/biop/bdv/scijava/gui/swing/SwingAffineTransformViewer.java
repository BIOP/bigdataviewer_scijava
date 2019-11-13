package ch.epfl.biop.bdv.scijava.gui.swing;

import bdv.util.BdvHandle;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.Priority;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import java.awt.*;
import java.util.Map.Entry;

@Plugin(type = DisplayViewer.class, priority = Priority.HIGH)
public class SwingAffineTransformViewer  extends
        EasySwingDisplayViewer<AffineTransform3D> {

    public SwingAffineTransformViewer()
    {
        super( AffineTransform3D.class );
    }

    @Override
    protected boolean canView(AffineTransform3D affineTransform) {
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

    AffineTransform3D at;

    JPanel mainPanel;
    JTextArea ta;

    @Parameter
    GuavaWeakCacheService cs;

    @Override
    protected JPanel createDisplayPanel(AffineTransform3D affineTransform) {
        this.at = affineTransform;
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        ta = new JTextArea();
        if (cs.get(at)!=null) {
            ta.setText((String)cs.get(at));
        } else {
            ta.setText(at.toString());
        }
        ta.setEditable(false);
        mainPanel.add(ta, BorderLayout.CENTER);
        return mainPanel;
    }
}
