package ch.epfl.biop.bdv.scijava.command.edit.transform;

import bdv.tools.transformation.TransformedSource;
import bdv.viewer.SourceAndConverter;
import ch.epfl.biop.bdv.scijava.command.BDVSourceAndConverterFunctionalInterfaceCommand;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, initializer = "init", menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Transform>Affine>Transform Sources (AffineTransform3D)")
public class BDVSourceAffineTransform extends BDVSourceAndConverterFunctionalInterfaceCommand {

    @Parameter(label = "Affine Transform Matrix", style = "text area")
    AffineTransform3D at;

    @Parameter
    boolean transformInPlace = true;

    public BDVSourceAffineTransform() {
        this.f = src -> {
            if ((src.getSpimSource() instanceof TransformedSource) && (transformInPlace)) {
                this.output_mode = LIST; // Transform
                TransformedSource ts = (TransformedSource) src.getSpimSource();
                AffineTransform3D at3d = new AffineTransform3D();
                ts.getFixedTransform(at3d);
                at3d = at3d.preConcatenate(at);
                ts.setFixedTransform(at3d);
                if (src.asVolatile()!=null) {
                    assert src.asVolatile().getSpimSource() instanceof TransformedSource;
                    TransformedSource vts = (TransformedSource) src.asVolatile().getSpimSource();
                    vts.setFixedTransform(at3d);
                }
                return src;
            } else
            {
                // Wrap source within a TransformedSource
                SourceAndConverter sac;
                TransformedSource ts = new TransformedSource(src.getSpimSource());
                ts.setFixedTransform(at);
                if (src.asVolatile()!=null) {
                    SourceAndConverter vsac;
                    TransformedSource vts = new TransformedSource(src.asVolatile().getSpimSource());
                    vts.setFixedTransform(at);
                    vsac = new SourceAndConverter(vts, src.asVolatile().getConverter());
                    sac = new SourceAndConverter<>(ts, src.getConverter(),vsac);
                } else {
                    sac = new SourceAndConverter<>(ts, src.getConverter());
                }
                return sac;
            }
        };
    }

}
