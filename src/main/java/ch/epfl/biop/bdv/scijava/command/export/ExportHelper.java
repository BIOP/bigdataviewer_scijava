package ch.epfl.biop.bdv.scijava.command.export;

import bdv.viewer.Source;
import mpicbg.spim.data.sequence.VoxelDimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.AffineTransform3D;

import java.util.Arrays;

public class ExportHelper {

    static public boolean SourcesAreIdenticallySampled(Source<?> src1, Source<?> src2, int tp) {
        // Test presence of timepoint
        if (!SourcesArePresent(src1,src2,tp)) {
            return false;
        }

        // Test identity of units (sampling could be the same with different units, but
        if (!SourcesHaveIdenticalVoxelDimensions(src1,src2)) {
            return false;
        }

        // Test identity of transformations
        if (!SourcesHaveIdenticalTransformations(src1,src2,tp)) {
            return false;
        }

        // Test identity of dimensions
        if (!SourcesHaveIdenticalDimensions(src1,src2,tp)) {
            return false;
        }
        return true;
    }

    static public boolean SourcesHaveIdenticalTypes(Source<?> src1, Source<?> src2) {
        return src1.getType().getClass().equals(src2.getType().getClass()); // A bit restrictive I believe...
    }

    static public boolean SourcesArePresent(Source<?> src1, Source<?> src2, int tp) {
        return (src1.isPresent(tp))&&(src2.isPresent(tp));
    }

    static public boolean SourcesHaveIdenticalVoxelDimensions(Source<?> src1, Source<?> src2) {
        VoxelDimensions vd1 = src1.getVoxelDimensions();
        VoxelDimensions vd2 = src2.getVoxelDimensions();

        boolean ans = true;

        // Same dimensionality
        ans = ans && (vd1.numDimensions()==vd2.numDimensions());

        // For each dimension same size
        for (int d=0;d<vd1.numDimensions();d++) {
            ans = ans && (vd1.dimension(d)==vd2.dimension(d));
        }

        // Same unit
        ans = ans && vd1.unit().equals(vd2.unit());
        return ans;
    }

    static public boolean SourcesHaveIdenticalDimensions(Source<?> src1, Source<?> src2, int tp) {
        boolean ans = true;

        if (!SourcesArePresent(src1, src2, tp)) {
            return false;
        }

        RandomAccessibleInterval<?> rai1 = src1.getSource(tp,0);
        RandomAccessibleInterval<?> rai2 = src1.getSource(tp,0);

        // Same dimensionality
        if (!(rai1.numDimensions()==rai2.numDimensions())) {
            return false;
        }

        // For each dimension same size ?
        for (int d=0;d<rai1.numDimensions();d++) {
            ans = ans && (rai1.dimension(d)==rai2.dimension(d));
        }

        return ans;
    }

    static public boolean SourcesHaveIdenticalTransformations(Source<?> src1, Source<?> src2, int tp) {
        boolean ans = true;

        if (!SourcesArePresent(src1, src2, tp)) {
            return false;
        }

        AffineTransform3D at1 = new AffineTransform3D();
        src1.getSourceTransform(tp,0,at1);

        double[] m1 = at1.getRowPackedCopy();

        AffineTransform3D at2 = new AffineTransform3D();
        src1.getSourceTransform(tp,0,at2);

        double[] m2 = at2.getRowPackedCopy();

        return Arrays.equals(m1,m2);
    }


}
