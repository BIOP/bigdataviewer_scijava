package ch.epfl.biop.bdv.scijava.command;

import bdv.util.BdvFunctions;
import bdv.util.BdvHandle;
import bdv.util.BdvOptions;
import bdv.util.volatiles.SharedQueue;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import bdv.util.VolatileBdvSource;
import bdv.util.VolatileUtils;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.numeric.NumericType;
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

    final public static String REPLACE = "Replace In Bdv";
    final public static String ADD = "Add To Bdv";
    final public static String LIST = "Output As List Only";

    @Parameter(label = "Input Bdv Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h_in;

    @Parameter(label="Indexes ('0,3-5'), of the sources to process")
    public String sourceIndexString = "0";

    public Function<Source<?>, Source<?>> f;

    @Parameter(label = "Output In New Bdv Window", required = false)
    public boolean outputInNewBdv=false;

    @Parameter(label = "Output Bdv Frame", type = ItemIO.BOTH)
    public BdvHandle bdv_h_out;

    @Parameter(choices = {REPLACE, ADD, LIST})
    public String output_mode;

    @Parameter
    boolean keepConverters = true;

    @Parameter(required = false)
    boolean makeInputVolatile = false;

    @Parameter(type = ItemIO.OUTPUT)
    public List<Source<?>> srcs_out;

    @Override
    public void run() {
        initCommand();
        List<Source<?>> srcs_in = CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .map(idx -> bdv_h_in.getViewerPanel().getState().getSources().get(idx).getSpimSource())
                .collect(Collectors.toList());

        Source<?> src_out_first=null;
        if (outputInNewBdv) {
            Source firstSource = srcs_in.get(0);

            final Source src_inside;
            if (makeInputVolatile) {
                Volatile vType = VolatileUtils.getVolatileFromNumeric((NumericType) firstSource.getType());
                src_inside = new VolatileBdvSource(firstSource,vType,new SharedQueue(1));
            } else {
                src_inside = firstSource;
            }

            src_out_first = f.apply(src_inside);
            if (output_mode.equals(REPLACE)|| output_mode.equals(ADD)) {
                if (keepConverters) {
                    Converter cvt = bdv_h_in.getViewerPanel()
                            .getState()
                            .getSources().stream()
                            .filter(stest -> stest.getSpimSource().equals(src_inside))
                            .findFirst().get().getConverter();

                    bdv_h_out = BdvFunctions.show(src_out_first).getBdvHandle();
                    bdv_h_out.getViewerPanel().addSource(new SourceAndConverter<>(src_out_first, cvt));
                    bdv_h_out.getViewerPanel().remove(0);
                } else {
                    bdv_h_out = BdvFunctions.show(src_out_first).getBdvHandle();
                }
            }
            if (output_mode.equals(REPLACE)) {
                bdv_h_in.getViewerPanel().removeSource(firstSource);
            }
            //return src_out;
            srcs_in.remove(0);

        }

        BdvOptions opts = BdvOptions.options().addTo(bdv_h_out);

        srcs_out = srcs_in.stream().map(s -> {

                    final Source src_inside;
                    if (makeInputVolatile) {
                        Volatile vType = VolatileUtils.getVolatileFromNumeric((NumericType) s.getType());
                        src_inside = new VolatileBdvSource(s,vType,new SharedQueue(1));
                    } else {
                        src_inside = s;
                    }

                    Source<?> src_out = f.apply(src_inside);
                    if (src_out!=null) {
                        if (output_mode.equals(REPLACE) || output_mode.equals(ADD)) {
                            if (keepConverters) {
                                Converter cvt = bdv_h_in.getViewerPanel()
                                        .getState()
                                        .getSources().stream()
                                        .filter(stest -> stest.getSpimSource().equals(src_inside))
                                        .findFirst().get().getConverter();
                                bdv_h_out.getViewerPanel().addSource(new SourceAndConverter<>(src_out, cvt));
                            } else {
                                BdvFunctions.show(src_out, opts);
                            }
                        }
                    }
                    if (output_mode.equals(REPLACE)) {
                        bdv_h_in.getViewerPanel().removeSource(s); // Converter forgotten ...
                    }
                    return src_out;
                }
        ).collect(Collectors.toList());

        if (outputInNewBdv) {
            srcs_out.add(0,src_out_first);
        }
    }

    // --- Empty implementation to avoid writing it in subclasses
    public void initCommand() {

    }

}
