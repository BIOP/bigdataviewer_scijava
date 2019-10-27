package ch.epfl.biop.bdv.scijava.converter;

import bdv.util.RandomAccessibleSource;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Volatile;
import net.imglib2.display.RealARGBColorConverter;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;
import org.scijava.convert.AbstractConverter;
import org.scijava.plugin.Plugin;


@Plugin(type = org.scijava.convert.Converter.class)
public class RAIToSourceAndConverter<I extends RandomAccessibleInterval, O extends SourceAndConverter> extends AbstractConverter<I,O> {
    @Override
    public <T> T convert(Object src, Class<T> dest) {
        System.out.println("converter called");
        NumericType type = Util.getTypeFromInterval( (RandomAccessibleInterval) src );
        final Source s = new RandomAccessibleSource(
                (RandomAccessibleInterval) src, (Interval) src, type, new AffineTransform3D(), "");

        final RealARGBColorConverter converter ;

        final double typeMin = Math.max( 0, Math.min( 0, 65535 ) );
        final double typeMax = Math.max( 0, Math.min( 0, 65535 ) );
        if ( s.getType() instanceof Volatile)
            converter = new RealARGBColorConverter.Imp0<>( typeMin, typeMax );
        else
            converter = new RealARGBColorConverter.Imp1<>( typeMin, typeMax );

        converter.setColor( new ARGBType( 0xffffffff ) );

        return (T) new SourceAndConverter<>( s, converter, null );
    }

    @Override
    public Class<O> getOutputType() {
        return (Class<O>) SourceAndConverter.class;
    }

    @Override
    public Class<I> getInputType() {
        return (Class<I>) RandomAccessibleInterval.class;
    }
}
