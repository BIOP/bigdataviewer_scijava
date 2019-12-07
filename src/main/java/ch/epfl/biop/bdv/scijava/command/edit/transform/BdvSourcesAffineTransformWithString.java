package ch.epfl.biop.bdv.scijava.command.edit.transform;

import bdv.tools.transformation.TransformedSource;
import bdv.viewer.SourceAndConverter;
import ch.epfl.biop.bdv.scijava.command.BdvSourceAndConverterFunctionalInterfaceCommand;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, initializer = "init", menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Transform>Affine>Transform Sources (Affine, string)")
public class BdvSourcesAffineTransformWithString extends BdvSourceAndConverterFunctionalInterfaceCommand {

    @Parameter(label = "Affine Transform Matrix", style = "text area")
    String stringMatrix = "1,0,0,0,\n 0,1,0,0,\n 0,0,1,0, \n 0,0,0,1";

    @Parameter
    boolean transformInPlace = true;

    public BdvSourcesAffineTransformWithString() {
        this.f = src -> {
            AffineTransform3D at = new AffineTransform3D();
            at.set(this.toDouble());

            if ((src.getSpimSource() instanceof TransformedSource) && (transformInPlace)) {
                TransformedSource ts = (TransformedSource) src.getSpimSource();
                AffineTransform3D at3d = new AffineTransform3D();
                ts.getFixedTransform(at3d);
                ts.setFixedTransform(at3d.concatenate(at));
                if (src.asVolatile()!=null) {
                    assert src.asVolatile().getSpimSource() instanceof TransformedSource;
                    TransformedSource vts = (TransformedSource) src.getSpimSource();
                    at3d = new AffineTransform3D();
                    vts.getFixedTransform(at3d);
                    vts.setFixedTransform(at3d.concatenate(at));
                }
                return src;
            } else
            {
                // wrap source within a TransformedSource
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

    public double[] toDouble() {
        String inputString = stringMatrix;
        // Test if the String is written using AffineTransform3D toString() method
        String[] testIfParenthesis = stringMatrix.split("[\\(\\)]+");// right of left parenthesis

        if (testIfParenthesis!=null) {
            for (String str : testIfParenthesis) {
                System.out.println(str);
            }

            if (testIfParenthesis.length > 1) {
                inputString = testIfParenthesis[1] + ",0,0,0,1";
            }
        }

        String[] strNumber = inputString.split(",");
        double[] mat = new double[16];
        if (strNumber.length!=16) {
            System.err.println("matrix has not enough elements");
            return null;
        }
        for (int i=0;i<16;i++) {
            mat[i] = Double.valueOf(strNumber[i].trim());
        }
        return mat;
    }
}
