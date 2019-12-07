package ch.epfl.biop.bdv.scijava.gui;

import ch.epfl.biop.bdv.scijava.command.display.window.BdvWindowSynchronize;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

@Plugin(type = Display.class)
public class SyncBdvHandleDisplay extends AbstractDisplay<BdvWindowSynchronize.SyncBdvHandle> {
    public SyncBdvHandleDisplay() {
        super(BdvWindowSynchronize.SyncBdvHandle.class);
    }
}
