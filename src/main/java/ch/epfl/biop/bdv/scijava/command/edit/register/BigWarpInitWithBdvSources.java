package ch.epfl.biop.bdv.scijava.command.edit.register;

import bdv.tools.brightness.ConverterSetup;
import bdv.util.BWBdvHandle;
import bdv.util.BdvHandle;
import bdv.util.BdvHandleHelper;
import bdv.viewer.SourceAndConverter;
import bigwarp.BigWarp;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import mpicbg.spim.data.SpimDataException;
import org.scijava.ItemIO;
import org.scijava.cache.GuavaWeakCacheService;
import org.scijava.command.Command;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvCmdSuffix;
import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;

@Plugin(type = Command.class,
        menuPath = ScijavaBdvRootMenu+"Bdv>Edit Sources>Register>BigWarp"+ScijavaBdvCmdSuffix,
        label = "Initializes BigWarp using pre existing set of SourceAndConverter",
        description = "Initializes BigWarp using pre existing set of SourceAndConverter"
)
public class BigWarpInitWithBdvSources implements Command {

    private static final Logger LOGGER = Logger.getLogger( BigWarpInitWithBdvSources.class.getName() );

    @Parameter(type = ItemIO.OUTPUT)
    BdvHandle bdvHandleP;

    @Parameter(type = ItemIO.OUTPUT)
    BdvHandle bdvHandleQ;

    @Parameter(label = "Input Bdv Window containing fixed sources")
    BdvHandle bdv_h_fixed;

    @Parameter(label="Fixed source indexes ('2,3:5'), starts at 0")
    String idx_src_fixed;

    @Parameter(label = "Input Bdv Window containing moving sources")
    BdvHandle bdv_h_moving;

    @Parameter(label="Moving source indexes ('2,3:5'), starts at 0")
    String idx_src_moving;

    @Parameter
    ObjectService os;

    @Parameter
    GuavaWeakCacheService cs;

    @Override
    public void run() {
        // TODO fix window names : ensure uniqueness

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

            BdvHandleHelper.setBdvHandleCloseOperation(bdvHandleP,os, cs,true);
            BdvHandleHelper.setBdvHandleCloseOperation(bdvHandleQ,os, cs,true);

        } catch (SpimDataException e) {
            e.printStackTrace();
        }
    }

}
