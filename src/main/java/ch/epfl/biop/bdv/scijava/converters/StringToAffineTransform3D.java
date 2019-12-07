package ch.epfl.biop.bdv.scijava.converters;

import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.convert.AbstractConverter;
import org.scijava.plugin.Plugin;

@Plugin(type = org.scijava.convert.Converter.class)
public class StringToAffineTransform3D<I extends String, O extends AffineTransform3D> extends AbstractConverter<I, O> {

    @Override
    public <T> T convert(Object src, Class<T> dest) {

        AffineTransform3D at = new AffineTransform3D();
        String inputString = (String) src;
        // Test if the String is written using AffineTransform3D toString() method
        String[] testIfParenthesis = ((String) src).split("[\\(\\)]+");// right of left parenthesis

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

        at.set(mat);
        return (T) at;
    }

    @Override
    public Class<O> getOutputType() {
        return (Class<O>) AffineTransform3D.class;
    }

    @Override
    public Class<I> getInputType() {
        return (Class<I>) String.class;
    }
}
