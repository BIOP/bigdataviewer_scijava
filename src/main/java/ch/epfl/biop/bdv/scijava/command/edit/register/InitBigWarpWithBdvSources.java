package ch.epfl.biop.bdv.scijava.command.edit.register;

import bdv.img.WarpedSource;
import bdv.tools.brightness.ConverterSetup;
import bdv.util.BWBdvHandle;
import bdv.util.BdvHandle;
import bdv.viewer.SourceAndConverter;
import bigwarp.BigWarp;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import mpicbg.spim.data.SpimDataException;
import net.imglib2.realtransform.RealTransform;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvCmdSuffix;
import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;


@Plugin(type = Command.class,menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Register>BigWarp"+ScijavaBdvCmdSuffix)
public class InitBigWarpWithBdvSources implements Command {

    private static final Logger LOGGER = Logger.getLogger( InitBigWarpWithBdvSources.class.getName() );

    @Parameter(type = ItemIO.OUTPUT)
    BdvHandle bdvHandleP;

    @Parameter(type = ItemIO.OUTPUT)
    BdvHandle bdvHandleQ;

    @Parameter
    BdvHandle bdv_h_fixed;

    @Parameter(label="Fixed source indexes ('2,3:5'), starts at 0")
    String idx_src_fixed;

    @Parameter
    BdvHandle bdv_h_moving;

    @Parameter(label="Moving source indexes ('2,3:5'), starts at 0")
    String idx_src_moving;



    @Override
    public void run() {
        // TODO fix names, transfer converters

        List<SourceAndConverter<?>> fxSrcs =
                CommandHelper.commaSeparatedListToArray(idx_src_fixed)
                        .stream()
                        .map(idx -> bdv_h_fixed.getViewerPanel().getState().getSources().get(idx))
                        .collect(Collectors.toList());

        List<SourceAndConverter<?>> mvSrcs =
                CommandHelper.commaSeparatedListToArray(idx_src_moving)
                        .stream()
                        .map(idx -> bdv_h_moving.getViewerPanel().getState().getSources().get(idx))
                        .collect(Collectors.toList());

        List<SourceAndConverter> allSources = new ArrayList<>();
        allSources.addAll(fxSrcs);
        allSources.addAll(mvSrcs);

        List<ConverterSetup> allConverterSetups = new ArrayList<>();

        int[] mvSrcIndices = new int[mvSrcs.size()];
        for (int i = 0; i < mvSrcs.size(); i++) {
            mvSrcIndices[i] = i;
        }

        int[] fxSrcIndices = new int[fxSrcs.size()];
        for (int i = 0; i < fxSrcs.size(); i++) {
            fxSrcIndices[i] = i+mvSrcs.size();
        }

        BigWarp.BigWarpData<?> bwd = new BigWarp.BigWarpData(allSources, allConverterSetups, null, mvSrcIndices, fxSrcIndices);

        try {
            BigWarp<?> bw = new BigWarp(bwd, "Big Warp", null);

            bw.getViewerFrameP().setVisible(true);
            bw.getViewerFrameQ().setVisible(true);

            bdvHandleP = new BWBdvHandle(bw, true);
            bdvHandleQ = new BWBdvHandle(bw, false);


        } catch (SpimDataException e) {
            e.printStackTrace();
        }
    }

}
