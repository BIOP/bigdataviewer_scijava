package ch.epfl.biop.bdv.scijava;

import bdv.util.*;
import bdv.util.volatiles.SharedQueue;
import bdv.viewer.Source;
import mpicbg.spim.data.generic.AbstractSpimData;
import net.imglib2.Volatile;
import net.imglib2.type.numeric.NumericType;
import org.scijava.cache.CacheService;

import java.lang.ref.WeakReference;

public class ScijavaBdvSourceHelper {

    public static BdvHandle registerAndShowSource(CacheService cs, Source src, BdvHandle bdvh, AbstractSpimData asd) {
        if (cs.get(src)==null) {
            // First time registration
            // Let's try to create it Volatile
            Volatile vType = VolatileUtils.getVolatileFromNumeric((NumericType) src.getType());
            Source vSrc = new VolatileBdvSource(src,vType,new SharedQueue(1));

            BdvSourceMetaInfo bsmi = new BdvSourceMetaInfo(src,vSrc, asd);
            bsmi.registerBdvH(bdvh); // HashSet -> avoids duplicate
            bsmi.asd=new WeakReference<>(asd);

            cs.put(src, bsmi);
            cs.put(vSrc, bsmi);

            // Now displays the volatile source only
            BdvFunctions.show(vSrc, BdvOptions.options().addTo(bdvh));

        } else {
            BdvSourceMetaInfo bsmi = (BdvSourceMetaInfo) cs.get(src);
            if (!bsmi.bdvInstance.contains(bdvh)) {
                bsmi.bdvInstance.add(new WeakReference<>(bdvh));
                // does it contain a volatile source ?
                if (bsmi.volatileSource!=null) {
                    Source vSrc = bsmi.volatileSource.get();
                    if (vSrc!=null) {
                        BdvFunctions.show(vSrc, BdvOptions.options().addTo(bdvh));
                    }
                }

            }
        }
        return null;
    }

}
