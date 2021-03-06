package ch.epfl.biop.bdv.scijava.command.spimdata;

import bdv.img.WarpedSource;
import bdv.tools.transformation.TransformedSource;
import bdv.util.BdvHandle;
import bdv.util.BdvStackSource;
import bdv.viewer.Source;
import bdv.viewer.state.SourceState;
import bigwarp.BigWarp;
import mpicbg.spim.data.SpimData;
import mpicbg.spim.data.XmlIoSpimData;
import mpicbg.spim.data.generic.AbstractSpimData;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import mpicbg.spim.data.registration.ViewTransform;
import mpicbg.spim.data.registration.ViewTransformAffine;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import mpicbg.spim.data.SpimDataException;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.*;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,menuPath = ScijavaBdvRootMenu+"SpimDataset>Save SpimDataset",
        label = "Command that saves a Spimdata dataset object",
        description = "Save a spimdata dataset. Manual transform can be pushed into the" +
                "dataset by looking recursively through wrapped Source. Limitations are to be expected." +
                "Only pushing transformations from timepoint 0 at the moment")
public class SpimdatasetSave implements Command {

    @Parameter
    AbstractSpimData spimData;

    @Parameter()
    File xmlFileName;

    @Parameter
    boolean pushSourceTransformationsToDataset;

    @Parameter
    GuavaWeakCacheService cs;

    @Override
    public void run() {
        try {
            if (pushSourceTransformationsToDataset) {
                if (cs.get(spimData)!=null) {
                    // Ok -> Loop through bdv sources to recursively find affine transforms

                    List<BdvStackSource<?>> lbss = (List<BdvStackSource<?>>) cs.get(spimData);
                    BdvHandle bdvh = lbss.get(0).getBdvHandle();

                    // TODO : push for all timepoints - currently only pushing to TimePoint 0
                    int timePoint = 0;
                    spimData.getSequenceDescription().getViewSetupsOrdered().forEach(vs -> {
                        int id = ((BasicViewSetup) vs).getId();
                        SourceState ss = bdvh.getViewerPanel().getState().getSources().get(id);
                        ArrayList<AffineTransform3D> transform3DS = getTransformsRecursively(ss.getSpimSource());
                        System.out.println("Found "+transform3DS.size()+" transforms for setup "+id);
                        //Collections.reverse(transform3DS);
                        transform3DS.forEach(tr -> {
                            ViewTransform vta = new ViewTransformAffine("AffineTransform", tr);
                            spimData.getViewRegistrations().getViewRegistration(timePoint,id).preconcatenateTransform(vta);
                        });
                        spimData.getViewRegistrations().getViewRegistration(timePoint,id).updateModel();
                    });

                } else {
                    System.out.println("No cached Bdv - cannot push any transformation.");
                }
            }

            System.out.println(xmlFileName.getAbsolutePath());
            spimData.setBasePath(xmlFileName.getParentFile());
            (new XmlIoSpimData()).save((SpimData) spimData, xmlFileName.getAbsolutePath());
        } catch (SpimDataException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<AffineTransform3D> getTransformsRecursively(Source src) {
       ArrayList<AffineTransform3D> listOfTransforms = new ArrayList<>();
        return getTransformsRecursively(src,listOfTransforms);
    }

    public ArrayList<AffineTransform3D> getTransformsRecursively(Source src, ArrayList<AffineTransform3D> listOfTransforms) {
        if (src instanceof TransformedSource) {
            listOfTransforms = getTransformsRecursively(((TransformedSource) src).getWrappedSource());
            AffineTransform3D at3D = new AffineTransform3D();
            ((TransformedSource) src).getFixedTransform(at3D);
            if (!at3D.isIdentity()) {
                listOfTransforms.add(at3D);
            }
        }
        if (src instanceof WarpedSource) {
            // Is it an affine transform ?
            //BigWarp.WrappedCoordinateTransform wct;
            WarpedSource wsrc = (WarpedSource) src;
            if (wsrc.getTransform() instanceof BigWarp.WrappedCoordinateTransform) {
                listOfTransforms = getTransformsRecursively(((TransformedSource) src).getWrappedSource());
                AffineTransform3D at3D = new AffineTransform3D();
                BigWarp.WrappedCoordinateTransform wct = (BigWarp.WrappedCoordinateTransform) wsrc.getTransform();
                wct.getTransform();
                // TODO : get affine transform from wct...
                if (!at3D.isIdentity()) {
                    listOfTransforms.add(at3D);
                }
            } else {
                System.out.println("Source Warping cannot be pushed to spimdata...");
                System.out.println("Found realTransform of class"+wsrc.getTransform().getClass().getSimpleName());
            }
        }
        return listOfTransforms;
    }

}
