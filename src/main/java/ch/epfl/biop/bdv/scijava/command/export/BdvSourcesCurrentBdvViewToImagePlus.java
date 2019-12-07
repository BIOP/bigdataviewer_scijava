package ch.epfl.biop.bdv.scijava.command.export;

import bdv.util.BdvHandle;
import bdv.util.RealCropper;
import bdv.viewer.Interpolation;
import bdv.viewer.Source;
import bdv.viewer.state.ViewerState;
import ch.epfl.biop.bdv.scijava.command.spimdata.SpimdatasetOpenBigDataServer;
import ch.epfl.biop.bdv.scijava.command.CommandHelper;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.RGBStackMerge;
import ij.process.LUT;
import net.imagej.ImageJ;
import net.imglib2.*;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;
import static java.lang.Math.sqrt;

/**
 * Command to export a BigDataViewer window into an ImageJ Composite Image
 * The default location logic is that it's centered on the current center of the BigDataViewer window
 * Limitations:
 *  - Single timepoint
 *  - All sources should have an identical bit depth when wrapped in ImageJ
 *  - LUT based on single RGB color
 *  - Isotropic export
 * T needs to be RealType for ImageJ1 Wrapping
 * @param <T>
 *
 * @author Nicolas Chiaruttini
 * BIOP, EPFL, 2019
 */

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Export Sources>As ImagePlus (current view)", initializer = "initParams")
public class BdvSourcesCurrentBdvViewToImagePlus<T extends RealType<T>> implements Command {

    private static final Logger LOGGER = Logger.getLogger( BdvSourcesCurrentBdvViewToImagePlus.class.getName() );

    // ItemIO.BOTH required because it can be modified in case of appending new data to BDV (-> requires INPUT), or created (-> requires OUTPUT)
    @Parameter(label = "BigDataViewer Frame", type = ItemIO.BOTH, callback = "matchXYBDVFrame")
    public BdvHandle bdv_h;

    @Parameter(label="Source indexes ('2,3:5'), starts at 0")
    public String sourceIndexString = "0";

    @Parameter(label="Mipmap level, 0 for highest resolution")
    public int mipmapLevel = 0;

    @Parameter(label="Match bdv frame window size", persist=false, callback = "matchXYBDVFrame")
    public boolean matchWindowSize=false;

    //@Parameter(label = "Number of pixels X", callback = "matchXYBDVFrame")
    //public double xSizeInPix = 100;

    //@Parameter(label = "Number of pixels Y", callback = "matchXYBDVFrame")
    //public double ySizeInPix = 100;

    //@Parameter(label = "Number of slice Z (isotropic vs XY, 0 for single slice)")
    //public int zSizeInPix = 0;

    @Parameter(label = "Physical Size X", callback = "matchXYBDVFrame")
    public double xSize = 100;

    @Parameter(label = "Physical Size Y", callback = "matchXYBDVFrame")
    public double ySize = 100;

    @Parameter(label = "Physical Size Z")//, callback = "matchXYBDVFrame")
    public double zSize = 100;

    @Parameter(label = "Timepoint", persist = false)
    public int timepoint = 0;

    //@Parameter(label = "Pixel size sampling (px unit: 1 for current display)", callback = "matchXYBDVFrame")
    //public double samplingInXPixelUnit = 1;

    @Parameter(label = "XY Pixel size sampling (physical unit)", callback = "changePhysicalSampling")
    public double samplingXYInPhysicalUnit = 1;

    @Parameter(label = "Z Pixel size sampling (physical unit)", callback = "changePhysicalSampling")
    public double samplingZInPhysicalUnit = 1;

    @Parameter(label = "Interpolate")
    public boolean interpolate = true;

    @Parameter(label = "Parallelize when exporting several channels")
    public boolean wrapMultichannelParallel = true;

    @Parameter(label = "Ignore Source LUT (check for RGB)")
    public boolean ignoreSourceLut = false;

    // Output imageplus window
    @Parameter(type = ItemIO.OUTPUT)
    public ImagePlus imp;

    String unitOfFirstSource ="";

    // Map containing wrapped sources, can be accessed in parallel -> Concurrent
    ConcurrentHashMap<Integer,ImagePlus> genImagePlus = new ConcurrentHashMap<>();

    Consumer<String> errlog = (s) -> System.err.println(s);

    @Override
    public void run() {

        // Transform sourceIndexString to ArrayList of indexes
        ArrayList<Integer> sourceIndexes = CommandHelper.commaSeparatedListToArray(sourceIndexString);

        // No source specified, end of Command
        if (sourceIndexes.size()==0) {
            errlog.accept( "No source index defined.");
            return;
        }

        // Retrieve viewer state from big data viewer
        ViewerState viewerState = bdv_h.getViewerPanel().getState();

        //Center on the display center of the viewer ...
        double w = bdv_h.getViewerPanel().getDisplay().getWidth();
        double h = bdv_h.getViewerPanel().getDisplay().getHeight();

        RealPoint pt = new RealPoint(3); // Number of dimension

        //Get global coordinates of the central position  of the viewer
        bdv_h.getViewerPanel().displayToGlobalCoordinates(w/2.0, h/2.0, pt);
        double posX = pt.getDoublePosition(0);
        double posY = pt.getDoublePosition(1);
        double posZ = pt.getDoublePosition(2);

        // Stream is single threaded or multithreaded based on boolean parameter
        Stream<Integer> indexStream;
        if (wrapMultichannelParallel) {
            indexStream = sourceIndexes.parallelStream();
        } else {
            indexStream = sourceIndexes.stream();
        }

        // Wrap each source independently
        indexStream.forEach( sourceIndex -> {

            // Get the source
            Source<T> s = (Source<T>) viewerState.getSources().get(sourceIndex).getSpimSource();

            if (s.getNumMipmapLevels()<mipmapLevel) {
                errlog.accept("Error, mipmap level requested = "+mipmapLevel);
                errlog.accept("But there are only "+s.getNumMipmapLevels()+" in the source");
                errlog.accept("Highest level chosen instead.");
                mipmapLevel = s.getNumMipmapLevels()-1;
            }

            // Interpolation switch
            Interpolation interpolation;
            if (interpolate) {
                interpolation = Interpolation.NLINEAR;
            } else {
                interpolation = Interpolation.NEARESTNEIGHBOR;
            }

            // Get real random accessible from the source
            final RealRandomAccessible<T> ipimg = s.getInterpolatedSource(timepoint, mipmapLevel, interpolation);

            // Get current big dataviewer transformation : source transform and viewer transform
            AffineTransform3D transformedSourceToViewer = new AffineTransform3D(); // Empty Transform
            // viewer transform
            viewerState.getViewerTransform(transformedSourceToViewer); // Get current transformation by the viewer state and puts it into sourceToImgPlus

            // Center on the display center of the viewer ...
            transformedSourceToViewer.translate(-w / 2, -h / 2, 0);

            // Getting an image independent of the view scaling unit (not sure)
             double xNorm = getNormTransform(0, transformedSourceToViewer);//trans
             transformedSourceToViewer.scale(1/xNorm);//xNorm);//1/ samplingInXPixelUnit);

            // Alternative : Get a bounding box from - (TODO interesting related post : https://forum.image.sc/t/using-imglib2-to-shear-an-image/2534/3)

            // Source transform
            final AffineTransform3D sourceTransform = new AffineTransform3D();
            s.getSourceTransform(timepoint, mipmapLevel, sourceTransform); // Get current transformation of the source

            // Composition of source and viewer transform
            transformedSourceToViewer.concatenate(sourceTransform); // Concatenate viewer state transform and source transform to know the final slice of the source

            RandomAccessibleInterval<T> view = RealCropper.getCroppedSampledRRAI(
                    ipimg,
                    transformedSourceToViewer,
                    new FinalRealInterval(new double[]{-(xSize/2), -(ySize/2), -zSize}, new double[]{+(xSize/2), +(ySize/2), +zSize}),
                    samplingXYInPhysicalUnit,samplingXYInPhysicalUnit,samplingZInPhysicalUnit
            );

            // Wrap as ImagePlus
            ImagePlus impTemp = ImageJFunctions.wrap(view, "");

            // 'Metadata' for ImagePlus set as a Z stack (instead of a Channel stack by default)
            int nSlices = impTemp.getNSlices();
            impTemp.setDimensions(1, nSlices, 1); // Set 3 dimension as Z, not as Channel

            // Set ImagePlus display properties as in BigDataViewer
            // Min Max

            // Simple Color LUT
            if (!ignoreSourceLut) {
                impTemp.setDisplayRange(
                        bdv_h.getSetupAssignments().getConverterSetups().get(sourceIndex).getDisplayRangeMin(),
                        bdv_h.getSetupAssignments().getConverterSetups().get(sourceIndex).getDisplayRangeMax()
                );
                ARGBType c = bdv_h.getSetupAssignments().getConverterSetups().get(sourceIndex).getColor();
                impTemp.setLut(LUT.createLutFromColor(new Color(ARGBType.red(c.get()), ARGBType.green(c.get()), ARGBType.blue(c.get()))));
            }
            // Store result in ConcurrentHashMap
            genImagePlus.put(sourceIndex, impTemp);
        });

        // Merging stacks, if possible, by using RGBStackMerge IJ1 class
        ImagePlus[] orderedArray = sourceIndexes.stream().map(idx -> genImagePlus.get(idx)).toArray(ImagePlus[]::new);
        if (orderedArray.length>1) {
            boolean identicalBitDepth = sourceIndexes.stream().map(idx -> genImagePlus.get(idx).getBitDepth()).distinct().limit(2).count()==1;
            if (identicalBitDepth) {
                imp = RGBStackMerge.mergeChannels(orderedArray, false);
            } else {
                LOGGER.warning("All channels do not have the same bit depth, sending back first channel only");
                imp = orderedArray[0];
            }
        } else {
            imp = orderedArray[0];
        }

        // Title
        String title = bdv_h.toString() // TODO : find a relevant name / title from the bdv handle
                + " - [T=" + timepoint + ", MML=" + mipmapLevel + "]"
                +"[SRC="+sourceIndexString+"]"+"[XY,Z="+ samplingXYInPhysicalUnit +","+samplingZInPhysicalUnit+"]";
        imp.setTitle(title);

        // TODO : add affine transform in image plus
        //imp.getProperties().setProperty("AffineTransform3D", )

        // Calibration in the limit of what's possible to know and set
        Calibration calibration = new Calibration();
        calibration.setImage(imp);

        // Origin is in fact the center of the image
        calibration.xOrigin=posX;
        calibration.yOrigin=posY;
        calibration.zOrigin=posZ;

        calibration.pixelWidth=samplingXYInPhysicalUnit;
        calibration.pixelHeight=samplingXYInPhysicalUnit;
        calibration.pixelDepth=samplingZInPhysicalUnit;

        updateUnit();
        calibration.setUnit(unitOfFirstSource);


        if (viewerState.getSources().get(sourceIndexes.get(0)).getSpimSource().getVoxelDimensions()!=null) {
            calibration.setUnit(viewerState.getSources().get(sourceIndexes.get(0)).getSpimSource().getVoxelDimensions().unit());
        }

        // Set generated calibration to output image
        imp.setCalibration(calibration);
    }

    /**
     * Returns the norm of an axis after an affinetransform is applied
     * @param axis
     * @param t
     * @return
     */
    static public double getNormTransform(int axis, AffineTransform3D t) {
        double f0 = t.get(axis,0);
        double f1 = t.get(axis,1);
        double f2 = t.get(axis,2);
        return sqrt(f0 * f0 + f1 * f1 + f2 * f2);
    }

    /**
     * Returns the distance between two RealPoint pt1 and pt2
     * @param pt1
     * @param pt2
     * @return
     */
    static public double distance(RealPoint pt1, RealPoint pt2) {
        assert pt1.numDimensions()==pt2.numDimensions();
        double dsquared = 0;
        for (int i=0;i<pt1.numDimensions();i++) {
            double diff = pt1.getDoublePosition(i)-pt2.getDoublePosition(i);
            dsquared+=diff*diff;
        }
        return Math.sqrt(dsquared);
    }

    // -- Initializers --

    /**
     * Initializes xSize(Pix) and ySize(Pix) according to the current BigDataViewer window
     */
    public void matchXYBDVFrame() {
        if (matchWindowSize) {
            // Gets window size
            double w = bdv_h.getViewerPanel().getDisplay().getWidth();
            double h = bdv_h.getViewerPanel().getDisplay().getHeight();

            // Get global coordinates of the top left position  of the viewer
            RealPoint ptTopLeft = new RealPoint(3); // Number of dimension
            bdv_h.getViewerPanel().displayToGlobalCoordinates(0, 0, ptTopLeft);

            // Get global coordinates of the top right position  of the viewer
            RealPoint ptTopRight = new RealPoint(3); // Number of dimension
            bdv_h.getViewerPanel().displayToGlobalCoordinates(0, w, ptTopRight);

            // Get global coordinates of the top right position  of the viewer
            RealPoint ptBottomLeft = new RealPoint(3); // Number of dimension
            bdv_h.getViewerPanel().displayToGlobalCoordinates(h,0, ptBottomLeft);

            // Gets physical size of pixels based on window size, image sampling size and user requested pixel size
            this.xSize=distance(ptTopLeft, ptTopRight);
            this.ySize=distance(ptTopLeft, ptBottomLeft);
        }
    }

    public void updateUnit() {

        // Transform sourceIndexString to ArrayList of indexes
        ArrayList<Integer> sourceIndexes = CommandHelper.commaSeparatedListToArray(sourceIndexString);

        // Retrieve viewer state from big data viewer
        ViewerState viewerState = bdv_h.getViewerPanel().getState();
        if ((sourceIndexes.size()>0) &&(viewerState.getSources().get(sourceIndexes.get(0))!=null)) {
            if (viewerState.getSources().get(sourceIndexes.get(0)).getSpimSource().getVoxelDimensions() != null) {
                unitOfFirstSource = viewerState.getSources().get(sourceIndexes.get(0)).getSpimSource().getVoxelDimensions().unit();
            }
        }
    }

    public static void main(String... args) throws Exception {
        // Arrange
        //  create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        // Gets samples dataset
        ij.command().run(SpimdatasetOpenBigDataServer.class, true,
                "urlServer","http://fly.mpi-cbg.de:8081",
                "datasetName", "Drosophila").get();
        // Returns ImagePlus slice
        ij.command().run(BdvSourcesCurrentBdvViewToImagePlus.class, true);
    }

}
