package ch.epfl.biop.bdv.scijava.gui;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.display.SynchronizeBDV;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

@Plugin(type = Display.class)
public class SyncBdvHandleDisplay extends AbstractDisplay<SynchronizeBDV.SyncBdvHandle> {
    public SyncBdvHandleDisplay() {
        super(SynchronizeBDV.SyncBdvHandle.class);
    }
}
