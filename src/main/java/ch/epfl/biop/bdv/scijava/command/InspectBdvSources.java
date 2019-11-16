package ch.epfl.biop.bdv.scijava.command;

import bdv.SpimSource;
import bdv.img.WarpedSource;
import bdv.tools.transformation.TransformedSource;
import bdv.util.BdvHandle;
import bdv.viewer.Source;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.function.Consumer;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Inspect BDV Sources")
public class InspectBdvSources implements Command {

    @Parameter
    BdvHandle bdvh;

    @Parameter(label="Indexes ('0,3:5'), of the sources to process")
    public String sourceIndexString = "0";

    @Parameter
    int timepoint;

    Consumer<String> log = (str) -> System.out.println(str);

    public static int MaxRecursivityOfInspector = 40;

    @Override
    public void run() {
        CommandHelper.commaSeparatedListToArray(sourceIndexString)
                .stream()
                .map(id -> bdvh.getViewerPanel().getState().getSources().get(id))
                .forEach(sourceState ->{
                    if (sourceState.asVolatile()!=null) {
                        log.accept(sourceState.getSpimSource().getName()+":"+"has volatile view");
                    } else {
                        log.accept(sourceState.getSpimSource().getName()+":"+"has NO volatile view");
                    }
                    inspect(sourceState.getSpimSource(),"",MaxRecursivityOfInspector);
                });
    }


    public void inspect(Source bdvSrc, String logPrefix, int recurslevel) {
        if (recurslevel<0) {
            log.accept("Max recursivity level of inspector reached");
            return;
        }
        log.accept(logPrefix + "BdvSource:"+bdvSrc.getName()+" is of class "+bdvSrc.getClass().getSimpleName());

        AffineTransform3D at3D = new AffineTransform3D();
        log.accept(logPrefix + "#mipmaps: "+bdvSrc.getNumMipmapLevels());
        for (int i=0;i<bdvSrc.getNumMipmapLevels();i++) {
            bdvSrc.getSourceTransform(timepoint,i,at3D);
            log.accept(logPrefix + "- transform mipmap "+i+":"+at3D.toString());
        }

        if (bdvSrc instanceof TransformedSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a TRANSFORMED source:");
            ((TransformedSource)bdvSrc).getFixedTransform(at3D);
            log.accept(logPrefix + "- fixed transform:"+at3D.toString());
            ((TransformedSource)bdvSrc).getIncrementalTransform(at3D);
            log.accept(logPrefix + "- incremental transform:"+at3D.toString());
            log.accept(logPrefix + "- Source Wrapped:");
            inspect(((TransformedSource)bdvSrc).getWrappedSource(),logPrefix+"\t", recurslevel-1);
        }

        if (bdvSrc instanceof WarpedSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a WARPED source:");
            log.accept(logPrefix + "- transform:"+((WarpedSource)bdvSrc).getTransform());
            log.accept(logPrefix + "- is transformed ?:"+((WarpedSource)bdvSrc).isTransformed());
            log.accept(logPrefix + "- Source Wrapped:");
            inspect(((WarpedSource)bdvSrc).getWrappedSource(),logPrefix+"\t", recurslevel-1);
        }

        if (bdvSrc instanceof SpimSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a spim source:");
            log.accept(logPrefix + "- setup id:"+((SpimSource) bdvSrc).getSetupId());
            log.accept(logPrefix + "- type:"+((SpimSource) bdvSrc).getType().getClass().getSimpleName());
            if ((bdvSrc).getVoxelDimensions()!=null) {
                log.accept(logPrefix + "- voxel dimensions:"+(bdvSrc).getVoxelDimensions().toString());
            } else {
                log.accept(logPrefix + "- voxel dimensions:null");
            }
        }

    }
}
