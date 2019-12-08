package ch.epfl.biop.bdv.scijava.command;

import bdv.AbstractSpimSource;
import bdv.SpimSource;
import bdv.VolatileSpimSource;
import bdv.img.WarpedSource;
import bdv.tools.transformation.TransformedSource;
import bdv.util.BdvHandle;
import bdv.util.RandomAccessibleIntervalSource;
import bdv.viewer.Source;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.function.Consumer;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Inspect BDV Sources",
    label = "Prints in the console informations about a bdv sources.",
    description = "Looks recursively through wrapped sources in order to understand the logic " +
            "behind a source which could have been loaded from a dataset, affinetransformed, " +
            "warped, affinetransformed again...")
public class BdvSourcesInspect implements Command {

    @Parameter(label = "Input Bdv Window")
    BdvHandle bdvh;

    @Parameter(label="Indexes ('0,3:5'), of the sources to inspect")
    public String sourceIndexString = "0";

    @Parameter
    int timepoint;

    @Parameter
    boolean getFullInformations = true;

    Consumer<String> log = (str) -> System.out.println(str);

    Consumer<String> logFull = (str) -> System.out.println(str);

    public static int MaxRecursivityOfInspector = 40;

    @Override
    public void run() {
        if (!getFullInformations) {
            logFull = (str) -> {};
        }
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
                    if (sourceState.asVolatile()!=null) {
                        inspect(sourceState.asVolatile().getSpimSource(),"",MaxRecursivityOfInspector);
                    }
                });
    }


    public void inspect(Source bdvSrc, String logPrefix, int recurslevel) {
        if (recurslevel<0) {
            log.accept("--- Max recursivity level of inspector reached ---");
            return;
        }
        log.accept(logPrefix + "BdvSource:"+bdvSrc.getName()+" is of class "+bdvSrc.getClass().getSimpleName());

        AffineTransform3D at3D = new AffineTransform3D();
        logFull.accept(logPrefix + "#mipmaps: "+bdvSrc.getNumMipmapLevels());
        for (int i=0;i<bdvSrc.getNumMipmapLevels();i++) {
            bdvSrc.getSourceTransform(timepoint,i,at3D);
            logFull.accept(logPrefix + "- transform mipmap "+i+":"+at3D.toString());
        }

        if (bdvSrc instanceof TransformedSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a "+TransformedSource.class.getSimpleName());
            ((TransformedSource)bdvSrc).getFixedTransform(at3D);
            logFull.accept(logPrefix + "- fixed transform:"+at3D.toString());
            ((TransformedSource)bdvSrc).getIncrementalTransform(at3D);
            logFull.accept(logPrefix + "- incremental transform:"+at3D.toString());
            logFull.accept(logPrefix + "- Source Wrapped:");
            inspect(((TransformedSource)bdvSrc).getWrappedSource(),logPrefix+"\t", recurslevel-1);
        }

        if (bdvSrc instanceof WarpedSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a "+WarpedSource.class.getSimpleName());
            logFull.accept(logPrefix + "- transform:"+((WarpedSource)bdvSrc).getTransform());
            logFull.accept(logPrefix + "- is transformed ?:"+((WarpedSource)bdvSrc).isTransformed());
            logFull.accept(logPrefix + "- Source Wrapped:");
            inspect(((WarpedSource)bdvSrc).getWrappedSource(),logPrefix+"\t", recurslevel-1);
        }

        if (bdvSrc instanceof SpimSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a "+SpimSource.class.getSimpleName());
            logFull.accept(logPrefix + "- setup id:"+((SpimSource) bdvSrc).getSetupId());
            logFull.accept(logPrefix + "- type:"+((SpimSource) bdvSrc).getType().getClass().getSimpleName());
            if ((bdvSrc).getVoxelDimensions()!=null) {
                logFull.accept(logPrefix + "- voxel dimensions:"+(bdvSrc).getVoxelDimensions().toString());
            } else {
                logFull.accept(logPrefix + "- voxel dimensions:null");
            }
        }

        if (bdvSrc instanceof VolatileSpimSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a "+VolatileSpimSource.class.getSimpleName());
            logFull.accept(logPrefix + "- type:"+((VolatileSpimSource) bdvSrc).getType().getClass().getSimpleName());
            if (((VolatileSpimSource)bdvSrc).nonVolatile()!=null)
                // Potential circularity ?
                inspect(((VolatileSpimSource)bdvSrc).nonVolatile(),logPrefix+"\t", recurslevel-1);
        }

        if (bdvSrc instanceof AbstractSpimSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a "+AbstractSpimSource.class.getSimpleName());
            AbstractSpimSource ass = (AbstractSpimSource) bdvSrc;
            // Can access fields to get more informations ?
        }

        if (bdvSrc instanceof RandomAccessibleIntervalSource) {
            log.accept(logPrefix + bdvSrc.getName()+" is a "+RandomAccessibleIntervalSource.class.getSimpleName());
            RandomAccessibleIntervalSource ris = (RandomAccessibleIntervalSource) bdvSrc;
            logFull.accept(logPrefix + "- type:"+ris.getType().getClass().getSimpleName());
        }

    }
}
