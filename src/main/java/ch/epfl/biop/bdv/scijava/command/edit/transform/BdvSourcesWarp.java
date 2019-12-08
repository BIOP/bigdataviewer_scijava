package ch.epfl.biop.bdv.scijava.command.edit.transform;

import bdv.img.WarpedSource;
import bdv.viewer.SourceAndConverter;
import ch.epfl.biop.bdv.scijava.command.BdvSourceAndConverterFunctionalInterfaceCommand;
import net.imglib2.Volatile;
import net.imglib2.realtransform.RealTransform;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, initializer = "init",
        menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Transform>Transform Sources (realtransform)",
        label = "Takes a transform (rather not affine), and applies it on specified sources",
        description = "If the transform is affine, it is preferable to use the BdvSourceAffineTransform" +
                "command. If the transform is more general (like a Warping, typically an output of BigWarp), " +
                "then this method can be used")
public class BdvSourcesWarp extends BdvSourceAndConverterFunctionalInterfaceCommand {
    @Parameter(label = "RealTransform object")
    RealTransform rt;

    public BdvSourcesWarp() {
        this.f = src -> {
                WarpedSource ws = new WarpedSource(src.getSpimSource(),"Warped_"+src.getSpimSource().getName());
                ws.updateTransform(rt);
                ws.setIsTransformed(true);
                if (ws.getType() instanceof Volatile) {
                    return new SourceAndConverter<>(ws, src.getConverter());
                } else {
                    if (src.asVolatile()== null) {
                        return new SourceAndConverter<>(ws, src.getConverter());
                    } else {
                        WarpedSource vws = new WarpedSource(src.asVolatile().getSpimSource(),"Warped_"+src.asVolatile().getSpimSource().getName());
                        vws.updateTransform(rt);
                        vws.setIsTransformed(true);
                        SourceAndConverter vsac = new SourceAndConverter<>(vws, src.asVolatile().getConverter());
                        return new SourceAndConverter<>(ws, src.getConverter(), vsac);
                    }
                }
        };
    }

}
