package ch.epfl.biop.bdv.scijava.gui.swing;

import ch.epfl.biop.bdv.scijava.command.BdvSourcesInspect;
import ch.epfl.biop.bdv.scijava.command.display.sources.BdvSourcesHide;
import ch.epfl.biop.bdv.scijava.command.display.sources.BdvSourcesSetColor;
import ch.epfl.biop.bdv.scijava.command.display.sources.BdvSourcesSetMinMax;
import ch.epfl.biop.bdv.scijava.command.display.sources.BdvSourcesShow;
import ch.epfl.biop.bdv.scijava.command.edit.BdvSourcesDuplicate;
import ch.epfl.biop.bdv.scijava.command.edit.transform.BdvSourcesAffineTransform;
import ch.epfl.biop.bdv.scijava.command.edit.transform.BdvSourcesResample;
import ch.epfl.biop.bdv.scijava.command.export.BdvSourcesExportToXMLHDF5_RecomputePyramid;
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
        JMenuItem menuItem = new JMenuItem("Make Visible");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesShow.class, true, paramSupplier.get()));
        popup.add(menuItem);
        // Hide
        menuItem = new JMenuItem("Hide");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesHide.class, true, paramSupplier.get()));
        popup.add(menuItem);
        // MinMax
        menuItem = new JMenuItem("Set Min Max Display Value");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesSetMinMax.class, true, paramSupplier.get()));
        popup.add(menuItem);

        // Color
        menuItem = new JMenuItem("Set Color");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesSetColor.class, true, paramSupplier.get()));
        popup.add(menuItem);

        popup.addSeparator();

        // Duplicate
        menuItem = new JMenuItem("Duplicate");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesDuplicate.class, true, paramSupplier.get()));
        popup.add(menuItem);

        // Resample
        menuItem = new JMenuItem("Resample");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesResample.class, true, paramSupplier.get()));
        popup.add(menuItem);

        // Inspect
        menuItem = new JMenuItem("Transform");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesAffineTransform.class, true, paramSupplier.get()));
        popup.add(menuItem);

        popup.addSeparator();

        // BdvSourcesExportToXMLHDF5_RecomputePyramid
        menuItem = new JMenuItem("Save To New Dataset");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesExportToXMLHDF5_RecomputePyramid.class, true, paramSupplier.get()));
        popup.add(menuItem);

        popup.addSeparator();

        // Inspect
        menuItem = new JMenuItem("Inspect");
        menuItem.addActionListener(e -> cmds.run(BdvSourcesInspect.class, true, paramSupplier.get()));
        popup.add(menuItem);
    }
}
