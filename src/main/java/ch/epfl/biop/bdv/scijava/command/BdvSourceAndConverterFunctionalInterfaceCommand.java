package ch.epfl.biop.bdv.scijava.command;

import bdv.util.*;
import bdv.viewer.SourceAndConverter;
import bdv.viewer.ViewerPanel;
import bdv.viewer.state.SourceState;
import bdv.viewer.state.ViewerState;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper Dynamic Command which takes care of specifying which sources need to be dealt with in subsequent BDV Commands
 * -> takes care of applying command on several sources identically
 * -> sets whether the output source are replacing the original sources or just appended
 */

abstract public class BdvSourceAndConverterFunctionalInterfaceCommand implements Command {//extends DynamicCommand {

    final public static String REPLACE = "Replace In Bdv";
    final public static String ADD = "Add To Bdv";
    final public static String LIST = "Output List Only";

    @Parameter(label = "Input Bdv Frame", type = ItemIO.BOTH)
    public BdvHandle bdvh;

    @Parameter(label="Indexes ('0,3:5'), of the sources to process")
    public String sourceIndexString = "0";

    public Function<SourceAndConverter<?>, SourceAndConverter<?>> f;

    @Parameter(label = "Output Bdv Frame", type = ItemIO.BOTH, autoFill = false)
    public BdvHandle bdvh_out;

    @Parameter(choices = {REPLACE, ADD, LIST})
    public String output_mode;

    @Parameter(type = ItemIO.OUTPUT)
    public List<SourceAndConverter<?>> srcs_out;

    public static final Consumer<String> log = str -> System.out.println(str);

    @Override
    public void run() {
        initCommand();

        ArrayList<Integer> listIdxToBdvIdx = new ArrayList<>();
        List<SourceAndConverter<?>> srcs_in = CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .map(idx -> {
                    listIdxToBdvIdx.add(idx);
                    return bdvh.getViewerPanel().getState().getSources().get(idx);
                })
                .collect(Collectors.toList());

        srcs_out = srcs_in.stream().map(s -> {
                    final SourceAndConverter src_inside = s;
                    SourceAndConverter<?> src_out = f.apply(src_inside);
                    if (src_out!=null) {
                        if (output_mode.equals(REPLACE)) {
                            if (bdvh==bdvh_out) {
                                // Proper replacement can be done -> sources is private...
                                // Lack a function to replace a source ? Or to add at a certain position
                                int original_index = listIdxToBdvIdx.get(srcs_in.indexOf(s));//map_srcs_in.get(src_inside);//bdv_h_out.getViewerPanel().getState().getSources().indexOf(s);

                                System.out.println("org index = "+original_index);
                                try {
                                    Field fSources = ViewerState.class.getDeclaredField("sources");
                                    Field fState = ViewerPanel.class.getDeclaredField("state");

                                    fState.setAccessible(true);
                                    fSources.setAccessible(true);

                                    SourceState ss = SourceState.create(src_out, (ViewerState) fState.get(bdvh_out.getViewerPanel()));
                                    ((ArrayList< SourceState< ? >>) fSources.get(fState.get(bdvh_out.getViewerPanel()))).set(original_index,ss);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                bdvh_out.getViewerPanel().addSource(src_out);
                                bdvh.getViewerPanel().removeSource(s.getSpimSource()); // ConverterSetup forgotten ...
                            }
                        }
                        if (output_mode.equals(ADD)) {
                            bdvh_out.getViewerPanel().addSource(src_out);
                        }
                    } else if (output_mode.equals(REPLACE)) {
                        bdvh.getViewerPanel().removeSource(s.getSpimSource()); // ConverterSetup forgotten ...
                    }
                    return src_out;
                }
        ).collect(Collectors.toList());

        bdvh_out.getViewerPanel().requestRepaint();
    }

    // --- Empty implementation to avoid writing it in subclasses
    public void initCommand() {

    }

}
