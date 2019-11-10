package ch.epfl.biop.bdv.scijava.gui.swing;

import ch.epfl.biop.bdv.scijava.command.display.BdvHideSources;
import ch.epfl.biop.bdv.scijava.command.display.BdvSetColor;
import ch.epfl.biop.bdv.scijava.command.display.BdvSetMinMax;
import ch.epfl.biop.bdv.scijava.command.display.BdvShowSources;
import org.scijava.command.CommandService;

import javax.swing.*;
import java.util.Map;
import java.util.function.Supplier;

public class SwingBdvPopupMenu {

    final JPopupMenu popup;

    public JPopupMenu getPopup() {
        return popup;
    }

    public SwingBdvPopupMenu(CommandService cmds, Supplier<Map<String, Object>> paramSupplier) {
        // PopupMenu
        popup = new JPopupMenu();

        // Show

        JMenuItem menuItem = new JMenuItem("Make Sources Visible");
        menuItem.addActionListener(e -> cmds.run(BdvShowSources.class, true, paramSupplier.get()));
        popup.add(menuItem);
        // Hide
        menuItem = new JMenuItem("Hide Sources");
        menuItem.addActionListener(e -> cmds.run(BdvHideSources.class, true, paramSupplier.get()));
        popup.add(menuItem);
        // MinMax
        menuItem = new JMenuItem("Set Min Max Display Value");
        menuItem.addActionListener(e -> cmds.run(BdvSetMinMax.class, true, paramSupplier.get()));
        popup.add(menuItem);
        // Color
        menuItem = new JMenuItem("Set Color");
        menuItem.addActionListener(e -> cmds.run(BdvSetColor.class, true, paramSupplier.get()));
        popup.add(menuItem);
    }
}
