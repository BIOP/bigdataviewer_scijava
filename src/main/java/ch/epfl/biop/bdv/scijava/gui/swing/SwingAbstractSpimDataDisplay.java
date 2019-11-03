package ch.epfl.biop.bdv.scijava.gui.swing;

import mpicbg.spim.data.generic.AbstractSpimData;
import mpicbg.spim.data.generic.base.Entity;
import mpicbg.spim.data.generic.sequence.AbstractSequenceDescription;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import mpicbg.spim.data.sequence.*;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Plugin(type = DisplayViewer.class)
public class SwingAbstractSpimDataDisplay extends
        EasySwingDisplayViewer<AbstractSpimData> {

    public SwingAbstractSpimDataDisplay()
    {
        super( AbstractSpimData.class );
    }

    @Override
    protected boolean canView(AbstractSpimData abstractSpimData) {
        return true;
    }

    @Override
    protected void redoLayout() {
    }

    @Override
    protected void setLabel(String s) {
    }

    Map<Class, Set<Entity>> entitiesSortedByClass;

    static List<BasicViewSetup> getViewSetupFilteredByEntity(Entity e, List<BasicViewSetup> lbvs) {
        return      lbvs.stream()
                        .filter(vs -> vs.getAttribute(e.getClass())!=null)
                        .filter(vs ->  vs.getAttribute(e.getClass()).getId()==e.getId())
                        .collect(Collectors.toList());
    }

    SpimDataTree sdt;

    JTree tree;

    @Override
    protected void redraw() {
        textInfo.setText(asd.toString());
        //viewSetupSortedByAttributes = new HashMap<>();// null;
        entitiesSortedByClass =
                asd.getSequenceDescription()
                        .getViewSetups()
                        .values().stream()
                        .map(bvs -> bvs.getAttributes().values())
                        .reduce(new ArrayList<>(), (a, b) -> {a.addAll(b); return a;}).stream()
                        .collect(Collectors.groupingBy(e -> e.getClass(),Collectors.toSet()));

        sdt = new SpimDataTree(entities,null, asd.getSequenceDescription().getViewSetupsOrdered());

        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        root.removeAllChildren();
        addNodes(root, sdt);
        model.reload(root);
        panel.revalidate();

    }

    DefaultMutableTreeNode top;

    JPanel panelInfo;
    JLabel nameLabel;
    JTextArea textInfo;
    JTabbedPane tabbedPane;
    JScrollPane treeView;
    JPanel panel;
    // Class of entities that will be merged
    public List<Class<? extends Entity>> entities = new ArrayList<>();
    AbstractSpimData<AbstractSequenceDescription<BasicViewSetup,?,?>> asd;
    DefaultTreeModel model;

    @Override
    protected JPanel createDisplayPanel(AbstractSpimData abstractSpimData) {

        entities.add(Tile.class);
        entities.add(Channel.class);
        //entities.add(Angle.class);
        //entities.add(Illumination.class);

        this.asd = abstractSpimData;

        tabbedPane = new JTabbedPane();

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panelInfo = new JPanel();
        nameLabel = new JLabel(asd.toString());
        panel.add(nameLabel, BorderLayout.NORTH);
        textInfo = new JTextArea();
        textInfo.setEditable(false);
        tabbedPane.add("Name", nameLabel);


        top = new DefaultMutableTreeNode("SpimData");
        tree = new JTree(top);
        model = (DefaultTreeModel)tree.getModel();

        treeView = new JScrollPane(tree);
        tabbedPane.add("Tree", treeView);
        panel.add(tabbedPane, BorderLayout.CENTER);

        this.redraw();
        return panel;
    }

    private void addNodes(DefaultMutableTreeNode basenode, SpimDataTree sdt_in ) {
        String name = "Root";
        if (sdt_in.rootEntity!=null) {
            name = sdt_in.rootEntity.getClass().getSimpleName() + ":" + sdt_in.rootEntity.getId()+" ("+sdt_in.allVSFromBranch.size()+")";
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                name
        );
        sdt_in.leavesOnNode.stream()
                .map(vs -> new DefaultMutableTreeNode(vs.getName()+":"+vs.getId()))
                .forEach(n -> node.add(n));
        sdt_in.branches.values().forEach( branch -> {
            addNodes(node,branch);
        });
        basenode.add(node);
    }

    public class SpimDataTree {

        Map<Entity, SpimDataTree> branches = new HashMap<>();

        List<BasicViewSetup> allVSFromBranch;

        List<BasicViewSetup> leavesOnNode;

        Entity rootEntity;

        public SpimDataTree(List<Class<? extends Entity>> entitiesToSort, Entity rootEntity, List<BasicViewSetup> viewSetups) {
            this.rootEntity = rootEntity;
            allVSFromBranch = viewSetups;
            if (entitiesToSort.size()>0) {
                List<Class<? extends Entity>> entitiesToSortCopy = new ArrayList<>();

                entitiesToSort.forEach(cl -> entitiesToSortCopy.add(cl));

                Class<? extends Entity> entityClass = entitiesToSortCopy.get(entitiesToSort.size()-1);
                boolean stillContainsClass = true;
                while ((stillContainsClass)&&(!entitiesSortedByClass.containsKey(entityClass))) {
                    entitiesToSortCopy.remove(entitiesToSort.size()-1);
                    stillContainsClass = entitiesToSortCopy.size()>0;
                    if (stillContainsClass) {
                        entityClass = entitiesToSortCopy.get(entitiesToSort.size()-1);
                    }
                }
                if (stillContainsClass) {
                    entitiesToSortCopy.remove(entityClass);
                    entitiesSortedByClass.get(entityClass).forEach(
                            e -> {
                                List<BasicViewSetup> lvs = getViewSetupFilteredByEntity(e,allVSFromBranch);
                                if (lvs.size()>0)
                                branches.put(e,new SpimDataTree(entitiesToSortCopy,e,lvs));
                            }
                    );
                    final Class<? extends Entity> c = entityClass;
                    leavesOnNode = allVSFromBranch
                            .stream()
                            .filter(vs -> vs.getAttribute(c)==null)
                            .collect(Collectors.toList());
                } else {
                    leavesOnNode = allVSFromBranch;
                }
            } else {
                leavesOnNode = allVSFromBranch;
            }
        }
    }

}
