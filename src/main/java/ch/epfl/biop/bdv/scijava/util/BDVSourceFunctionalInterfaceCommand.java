package ch.epfl.biop.bdv.scijava.util;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import net.imglib2.converter.Converter;
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
 * -> optionally transfers the converters
 */

abstract public class BDVSourceFunctionalInterfaceCommand extends DynamicCommand {

    @Parameter(label = "Input Bdv Frame", type = ItemIO.BOTH)
    BdvHandle bdv_h_in;

    @Parameter(label="Indexes ('0,3-5'), of the sources to process")
    public String sourceIndexString = "0";

    protected Function<Source<?>, Source<?>> f;

    @Parameter(label = "Output Bdv Frame", type = ItemIO.BOTH)
    BdvHandle bdv_h_out;

    @Parameter(choices = {"Replace In Bdv", "Add To Bdv", "Output As List Only"})
    String output_mode;

    @Parameter
    boolean keepConverters = true;

    @Parameter(type = ItemIO.OUTPUT)
    List<Source<?>> srcs_out;

    @Override
    public void run() {
        initCommand();
        List<Source<?>> srcs_in = BdvStringHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .map(idx -> bdv_h_in.getViewerPanel().getState().getSources().get(idx).getSpimSource())
                .collect(Collectors.toList());

        BdvOptions opts = BdvOptions.options().addTo(bdv_h_out);

        srcs_out = srcs_in.stream().map(s -> {
                    Source<?> src_out = f.apply(s);
                    if (output_mode.equals("Replace In Bdv")|| output_mode.equals("Add To Bdv")) {
                        if (keepConverters) {
                            Converter cvt = bdv_h_in.getViewerPanel()
                                    .getState()
                                    .getSources().stream()
                                    .filter(stest -> stest.getSpimSource().equals(s))
                                    .findFirst().get().getConverter();
                            bdv_h_out.getViewerPanel().addSource(new SourceAndConverter<>(src_out, cvt));
                        } else {
                            BdvFunctions.show(src_out, opts);
                        }
                    }
                    if (output_mode.equals("Replace In Bdv")) {
                        bdv_h_in.getViewerPanel().removeSource(s);
                    }
                    return src_out;
                }
        ).collect(Collectors.toList());
    }

    // --- Empty implementation to avoid writing it in subclasses
    public void initCommand() {

    }

}
