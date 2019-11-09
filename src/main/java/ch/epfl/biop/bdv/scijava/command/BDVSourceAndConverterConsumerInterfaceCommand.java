package ch.epfl.biop.bdv.scijava.command;

import bdv.util.BdvHandle;
import bdv.viewer.SourceAndConverter;
import org.scijava.ItemIO;
import org.scijava.command.DynamicCommand;
import org.scijava.plugin.Parameter;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Helper Dynamic Command which takes care of specifying which sources need to be dealt with in subsequent BDV Commands
 * -> takes care of applying command on several sources identically
 * -> sets whether the output source are replacing the original sources or just appended
 */

abstract public class BDVSourceAndConverterConsumerInterfaceCommand extends DynamicCommand {

    final public static String REPLACE = "Replace In Bdv";
    final public static String ADD = "Add To Bdv";
    final public static String LIST = "Output As List Only";

    @Parameter(label = "Input Bdv Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h_in;

    @Parameter(label="Indexes ('0,3:5'), of the sources to process")
    public String sourceIndexString = "0";

    public Consumer<SourceAndConverter<?>> f;

    public static final Consumer<String> log = str -> System.out.println(str);

    @Override
    public void run() {
        initCommand();
        List<SourceAndConverter<?>> srcs_in = CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .map(idx -> bdv_h_in.getViewerPanel().getState().getSources().get(idx))
                .collect(Collectors.toList());

        srcs_in.stream().forEach(s -> {
                    final SourceAndConverter src_inside = s;
                    f.accept(src_inside);});

        bdv_h_in.getViewerPanel().requestRepaint();
    }

    // --- Empty implementation to avoid writing it in subclasses
    public void initCommand() {

    }

}
