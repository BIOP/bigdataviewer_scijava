package ch.epfl.biop.bdv.scijava.util;

import net.imglib2.Volatile;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.volatiles.*;

public class VolatileUtils {
    /**
     * This function probably exists elsewhere
     * @param numType numericType, which needs to be wrapped in volatile
     * @return corresponding volatile type
     */
    public static Volatile getVolatileFromNumeric(NumericType numType) {
        if (numType instanceof ARGBType) return new VolatileARGBType();
        if (numType instanceof UnsignedByteType) return new VolatileUnsignedByteType();
        if (numType instanceof UnsignedShortType) return new VolatileUnsignedShortType();
        if (numType instanceof UnsignedIntType) return new VolatileUnsignedIntType();
        if (numType instanceof FloatType) return new VolatileFloatType();
        return null;
    }
}
