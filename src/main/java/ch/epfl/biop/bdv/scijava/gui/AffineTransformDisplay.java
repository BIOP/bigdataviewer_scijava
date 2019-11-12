package ch.epfl.biop.bdv.scijava.gui;

import net.imglib2.realtransform.AffineTransform;
import org.scijava.Priority;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

@Plugin(type = Display.class, priority = Priority.NORMAL)
public class AffineTransformDisplay extends AbstractDisplay<AffineTransform> {
    public AffineTransformDisplay() {
        super(AffineTransform.class);
    }
}
