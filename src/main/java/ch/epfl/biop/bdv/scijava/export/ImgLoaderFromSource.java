package ch.epfl.biop.bdv.scijava.export;

import bdv.viewer.Source;
import mpicbg.spim.data.generic.sequence.BasicSetupImgLoader;
import mpicbg.spim.data.generic.sequence.TypedBasicImgLoader;
import net.imglib2.type.Type;

public class ImgLoaderFromSource<T extends Type<T>> implements TypedBasicImgLoader< T > {

    Source<T> src;

    public ImgLoaderFromSource(Source<T> src) {
        this.src = src;
    }

    @Override
    public BasicSetupImgLoader<T> getSetupImgLoader(int setupId) {
        return new BasicSetupImgLoaderFromSource<>(src);
    }
}
