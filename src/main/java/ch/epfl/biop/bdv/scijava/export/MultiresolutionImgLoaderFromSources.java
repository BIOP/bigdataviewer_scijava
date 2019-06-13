package ch.epfl.biop.bdv.scijava.export;

import bdv.viewer.Source;
import mpicbg.spim.data.generic.sequence.BasicMultiResolutionImgLoader;
import mpicbg.spim.data.generic.sequence.BasicMultiResolutionSetupImgLoader;
import net.imglib2.type.Type;

import java.util.List;

public class MultiresolutionImgLoaderFromSources< T extends Type< T >> implements BasicMultiResolutionImgLoader {

    List<Source<T>> srcs;

    public MultiresolutionImgLoaderFromSources(List<Source<T>> srcs) {
        this.srcs = srcs;
    }

    @Override
    public BasicMultiResolutionSetupImgLoader<?> getSetupImgLoader(int setupId) {
        return new MultiresolutionSetupImgLoaderFromSource<>(srcs.get(setupId));
    }
}
