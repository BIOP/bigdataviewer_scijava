package ch.epfl.biop.bdv.scijava.gui;

import net.imglib2.realtransform.RealTransform;
import org.scijava.Priority;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

@Plugin(type = Display.class, priority = Priority.LOW)
public class RealTransformDisplay extends AbstractDisplay<RealTransform> {
    public RealTransformDisplay() {
        super(RealTransform.class);
    }
}
