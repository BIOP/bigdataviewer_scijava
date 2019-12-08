package ch.epfl.biop.bdv.scijava.command.edit.transform;

import bdv.util.BdvHandle;
import bdv.util.ResampledSource;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import ch.epfl.biop.bdv.scijava.command.BdvSourceAndConverterFunctionalInterfaceCommand;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;


@Plugin(type = Command.class, initializer = "init",
        menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Transform>Resample Sources",
        label = "Resample a Bdv Source like another one",
        description = "This command is useful to save any sort of source which is not" +
                "sampled on a grid (procedural, warped), and to resample it onto" +
                "an appropriate grid (defined by the source template). Once resampled, the source" +
                "can be exported as a spim Xml Dataset, for instance.")
public class BdvSourcesResample extends BdvSourceAndConverterFunctionalInterfaceCommand {

    @Parameter(label = "Bdv Frame containing source resampling template")
    BdvHandle bdv_dst;

    @Parameter(label = "Index of the source resampling template")
    int idxSourceDst;

    @Parameter(label = "Reuse mipmaps of the resampling template source")
    boolean reuseMipMaps;

    public BdvSourcesResample() {
        this.f = src -> {
            Source srcRsampled =
            new ResampledSource(
                            src.getSpimSource(),
                            bdv_dst.getViewerPanel()
                                   .getState()
                                   .getSources()
                                   .get(idxSourceDst)
                                   .getSpimSource(),
                            reuseMipMaps);

            SourceAndConverter sac;
            if (src.asVolatile()!=null) {
                SourceAndConverter vsac;
                Source vsrcRsampled =
                        new ResampledSource(
                                src.asVolatile().getSpimSource(),
                                bdv_dst.getViewerPanel()
                                        .getState()
                                        .getSources()
                                        .get(idxSourceDst)
                                        .getSpimSource(),
                                reuseMipMaps);
                vsac = new SourceAndConverter(vsrcRsampled, src.asVolatile().getConverter());
                sac = new SourceAndConverter<>(srcRsampled, src.getConverter(),vsac);
            } else {
                sac = new SourceAndConverter<>(srcRsampled, src.getConverter());
            }
            return sac;
        };
    }

}
