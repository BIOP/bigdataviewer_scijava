package ch.epfl.biop.bdv.scijava.gui.swing;

import bdv.util.BdvHandle;
import ch.epfl.biop.bdv.scijava.command.InspectBdvSources;
import ch.epfl.biop.bdv.scijava.command.display.BdvHideSources;
import ch.epfl.biop.bdv.scijava.command.display.BdvSetColor;
import ch.epfl.biop.bdv.scijava.command.display.BdvSetMinMax;
import ch.epfl.biop.bdv.scijava.command.display.BdvShowSources;
import ch.epfl.biop.bdv.scijava.command.edit.BdvDuplicateSources;
import ch.epfl.biop.bdv.scijava.command.edit.transform.BDVSourceAffineTransform;
import ch.epfl.biop.bdv.scijava.command.edit.transform.BDVSourceResample;
import ch.epfl.biop.bdv.scijava.command.export.BdvSourceExportToXMLHDF5_RecomputePyramid;
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
        menuItem.addActionListener(e -> cmds.run(BdvShowSources.class, true, paramSupplier.get()));
        popup.add(menuItem);
        // Hide
        menuItem = new JMenuItem("Hide");
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

        popup.addSeparator();

        // Duplicate
        menuItem = new JMenuItem("Duplicate");
        menuItem.addActionListener(e -> cmds.run(BdvDuplicateSources.class, true, paramSupplier.get()));
        popup.add(menuItem);

        // Resample
        menuItem = new JMenuItem("Resample");
        menuItem.addActionListener(e -> cmds.run(BDVSourceResample.class, true, paramSupplier.get()));
        popup.add(menuItem);

        // Inspect
        menuItem = new JMenuItem("Transform");
        menuItem.addActionListener(e -> cmds.run(BDVSourceAffineTransform.class, true, paramSupplier.get()));
        popup.add(menuItem);

        popup.addSeparator();

        // BdvSourceExportToXMLHDF5_RecomputePyramid
        menuItem = new JMenuItem("Save To New Dataset");
        menuItem.addActionListener(e -> cmds.run(BdvSourceExportToXMLHDF5_RecomputePyramid.class, true, paramSupplier.get()));
        popup.add(menuItem);

        popup.addSeparator();

        // Inspect
        menuItem = new JMenuItem("Inspect");
        menuItem.addActionListener(e -> cmds.run(InspectBdvSources.class, true, paramSupplier.get()));
        popup.add(menuItem);
    }
}
