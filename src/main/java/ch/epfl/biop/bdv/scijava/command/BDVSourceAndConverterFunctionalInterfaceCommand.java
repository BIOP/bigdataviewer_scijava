package ch.epfl.biop.bdv.scijava.command;

import bdv.util.*;
import bdv.viewer.SourceAndConverter;
import org.scijava.ItemIO;
import org.scijava.command.DynamicCommand;
import org.scijava.plugin.Parameter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper Dynamic Command which takes care of specifying which sources need to be dealt with in subsequent BDV Commands
 * -> takes care of applying command on several sources identically
 * -> sets whether the output source are replacing the original sources or just appended
 */

abstract public class BDVSourceAndConverterFunctionalInterfaceCommand extends DynamicCommand {

    final public static String REPLACE = "Replace In Bdv";
    final public static String ADD = "Add To Bdv";
    final public static String LIST = "Output As List Only";

    @Parameter(label = "Input Bdv Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h_in;

    @Parameter(label="Indexes ('0,3-5'), of the sources to process")
    public String sourceIndexString = "0";

    public Function<SourceAndConverter<?>, SourceAndConverter<?>> f;

    @Parameter(label = "Output Bdv Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h_out;

    @Parameter(choices = {REPLACE, ADD, LIST})
    public String output_mode;

    @Parameter(type = ItemIO.OUTPUT)
    public List<SourceAndConverter<?>> srcs_out;

    @Override
    public void run() {
        initCommand();
        List<SourceAndConverter<?>> srcs_in = CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .map(idx -> bdv_h_in.getViewerPanel().getState().getSources().get(idx))
                .collect(Collectors.toList());

        srcs_out = srcs_in.stream().map(s -> {
                    final SourceAndConverter src_inside = s;
                    SourceAndConverter<?> src_out = f.apply(src_inside);
                    if (src_out!=null) {
                        if (output_mode.equals(REPLACE) || output_mode.equals(ADD)) {
                            bdv_h_out.getViewerPanel().addSource(src_out);
                        }
                    }
                    if (output_mode.equals(REPLACE)) {
                        bdv_h_in.getViewerPanel().removeSource(s.getSpimSource()); // ConverterSetup forgotten ...
                    }
                    return src_out;
                }
        ).collect(Collectors.toList());
    }

    // --- Empty implementation to avoid writing it in subclasses
    public void initCommand() {

    }

}
