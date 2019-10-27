package ch.epfl.biop.bdv.scijava;

import bdv.util.BdvHandle;
import bdv.viewer.Source;
import bdv.viewer.state.SourceState;
import mpicbg.spim.data.generic.AbstractSpimData;
import net.imglib2.converter.Converter;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;

/**
 * Because we cannot keep easily track each source with its volatile view
 * Because we do not always want to display them
 * Because we want to keep track of the linked AbstractSpimData
 * Because we want to keep track of converters...
 */

public class BdvSourceMetaInfo {
    final public WeakReference<Source> source;
    final public WeakReference<Source> volatileSource;
    HashSet<WeakReference<BdvHandle>> bdvInstance;
    public WeakReference<AbstractSpimData> asd;
    HashSet<WeakReference<Converter>> convertersInstance;

    public BdvSourceMetaInfo(Source src, Source vSrc) {
        source = new WeakReference<>(src);
        volatileSource = new WeakReference<>(vSrc);
        asd = null;
    }

    public BdvSourceMetaInfo(Source src, Source vSrc, AbstractSpimData asd) {
        source = new WeakReference<>(src);
        volatileSource = new WeakReference<>(vSrc);
        this.asd = new WeakReference<>(asd);
    }

    public void registerBdvH(BdvHandle bdvh) {
        if (!bdvInstance.stream().filter(wr -> bdvh.equals(wr.get())).findFirst().isPresent()) {
            bdvInstance.add(new WeakReference<>(bdvh));
            if (source.get()!=null) {
                List<SourceState<?>> lss = bdvh.getViewerPanel().getState().getSources();
                convertersInstance.add(new WeakReference<>(lss.get(lss.indexOf(source.get())).getConverter()));
            }
        }
    }

    public void registerSpimData(AbstractSpimData asd) {
        this.asd = new WeakReference<>(asd);
    }

    public Source getSource() {
        return source.get();
    }

    public Source getVolatileSource() {
        return volatileSource.get();
    }

}
