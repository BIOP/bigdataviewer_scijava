package ch.epfl.biop.bdv.scijava.gui.swing;

import bdv.util.BdvHandle;
import bdv.viewer.state.SourceState;
import net.imglib2.Volatile;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import java.awt.*;

@Plugin(type = DisplayViewer.class)
public class SwingBdvHandleViewer extends
        EasySwingDisplayViewer<BdvHandle> {

    public SwingBdvHandleViewer()
    {
        super( BdvHandle.class );
    }

    @Override
    protected boolean canView(BdvHandle bdv_h) {
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
        // Needs to update the display
        textInfo.setText(bdv_h.toString());
        DefaultListModel<SourceState<?>> listModel = new DefaultListModel();
        bdv_h.getViewerPanel().getState().getSources().forEach(src -> {
            listModel.addElement(src);
        });

        listOfSources.setModel(listModel);
    }

    BdvHandle bdv_h = null;

    JPanel panelInfo;
    JLabel nameLabel;
    JTextArea textInfo;

    JList<SourceState<?>> listOfSources;

    @Override
    protected JPanel createDisplayPanel(BdvHandle bdv_h) {
        this.bdv_h = bdv_h;
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panelInfo = new JPanel();
        panel.add(panelInfo, BorderLayout.CENTER);
        nameLabel = new JLabel(bdv_h.toString());
        panel.add(nameLabel, BorderLayout.NORTH);
        textInfo = new JTextArea();
        textInfo.setEditable(false);

        DefaultListModel<SourceState<?>> listModel = new DefaultListModel();
        bdv_h.getViewerPanel().getState().getSources().forEach(src -> {
           listModel.addElement(src);
        });

        listOfSources = new JList(listModel);

        listOfSources.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listOfSources.setLayoutOrientation(JList.VERTICAL);
        listOfSources.setVisibleRowCount(-1);

        SourceStateCellRenderer sscr = new SourceStateCellRenderer();
        listOfSources.setCellRenderer(sscr);


        JScrollPane listScroller = new JScrollPane(listOfSources);
        listScroller.setPreferredSize(new Dimension(250, 300));
        panelInfo.setLayout(new BorderLayout());
        panelInfo.add(listScroller, BorderLayout.CENTER);

        this.redraw();
        return panel;
    }

    class SourceStateCellRenderer extends JLabel implements ListCellRenderer<SourceState<?>> {
        public SourceStateCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }
        @Override
        public Component getListCellRendererComponent(JList<? extends SourceState<?>> list, SourceState<?> value, int index, boolean isSelected, boolean cellHasFocus) {

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if (value.getSpimSource().getType() instanceof Volatile) {
                if (value.getSpimSource().getName().endsWith("(Volatile)")) {
                    setText(value.getSpimSource().getName());
                } else {
                    setText(value.getSpimSource().getName() + " (Volatile)");
                }
            } else {
                setText(value.getSpimSource().getName());
            }

            return this;
        }
    }

}