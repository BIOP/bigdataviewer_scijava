package ch.epfl.biop.bdv.scijava.export;

import bdv.viewer.Source;
import mpicbg.spim.data.generic.sequence.BasicMultiResolutionSetupImgLoader;
import mpicbg.spim.data.generic.sequence.ImgLoaderHint;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.Type;

// TODO: understand if there's really the limitation of applying same transforms for different timepoints ?

public class MultiresolutionSetupImgLoaderFromSource< T extends Type< T >> implements BasicMultiResolutionSetupImgLoader< T > {

    Source<T> src;

    public MultiresolutionSetupImgLoaderFromSource(Source<T> src) {
        this.src=src;
    }

    @Override
    public RandomAccessibleInterval<T> getImage(int timepointId, int level, ImgLoaderHint... hints) {
        return src.getSource(timepointId, level);
    }

    @Override
    public double[][] getMipmapResolutions() {
        final int nMipMaps = src.getNumMipmapLevels();
        // Let's assume we cover the same area for all resolutions savings
        double[][] mmResolutions = new double[nMipMaps][3];
        RandomAccessibleInterval<T> raiLevel0 = this.getImage(0,0);
        double refScaleX = raiLevel0.dimension(0);
        double refScaleY = raiLevel0.dimension(1);
        double refScaleZ = raiLevel0.dimension(2);
        for (int i=0;i<nMipMaps;i++) {
            RandomAccessibleInterval<T> raiCurrentLevel = this.getImage(0,i);
            mmResolutions[i][0] = (double) raiCurrentLevel.dimension(0) / refScaleX;
            mmResolutions[i][1] = (double) raiCurrentLevel.dimension(1) / refScaleY;
            mmResolutions[i][2] = (double) raiCurrentLevel.dimension(2) / refScaleZ;
        }
        return mmResolutions;
    }

    @Override
    public AffineTransform3D[] getMipmapTransforms() {
        final int nMipMaps = src.getNumMipmapLevels();
        AffineTransform3D[] ats = new AffineTransform3D[nMipMaps];
        for (int i=0;i<nMipMaps;i++) {
            ats[i] = new AffineTransform3D();
            src.getSourceTransform(0,i, ats[i]); // Assuming all transforms are the same along time
        }
        return ats;
    }

    @Override
    public int numMipmapLevels() {
        return src.getNumMipmapLevels();
    }

    @Override
    public RandomAccessibleInterval<T> getImage(int timepointId, ImgLoaderHint... hints) {
        return src.getSource(timepointId,0); // ? why this function ?
    }

    @Override
    public T getImageType() {
        return src.getType();
    }
}
