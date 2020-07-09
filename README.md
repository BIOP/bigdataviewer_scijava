ARCHIVED : OBSOLETE AND REPLACED BY https://github.com/bigdataviewer/bigdataviewer-playground

## Fiji plugins for starting BigDataViewer and exporting data, scijava-friendly.

### Project goal:
* Use SciJava Command, and BDVHandle objects, according to the initial discussion here: https://forum.image.sc/t/getting-bigdataviewer-instance-in-an-imagej-command/21110

### Documentation:
* List of command located in this readme file
* Documentation on imagej.net : https://imagej.net/Bigdataviewer_Scijava

### Demo video:

1. Simple https://www.youtube.com/watch?v=-q5qIdH9Idw (1 minute)
2. IJ Script example https://youtu.be/IjIW5bOn4P8 (3 minutes) 
3. Procedural + Warping + Export (xml hdf5 and ImagePlus) https://youtu.be/uOYWn7tUsf0 (7 minutes) 

## List of all commands of the repository

<details>
 <summary>Inspect sources</summary>

## [BdvSourcesInspect](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/BdvSourcesInspect.java) [BDV_SciJava>Bdv>Inspect BDV Sources]
Prints in the console informations about a bdv source.
Looks recursively through wrapped sources in order to understand the logic behind a source which could have been loaded from a dataset, affinetransformed, warped, affinetransformed again...
### Input
* [BdvHandle] **bdvh**:Input Bdv Window
* [boolean] **getFullInformations**:
* [String] **sourceIndexString**:Indexes ('0,3:5'), of the sources to inspect
* [int] **timepoint**:

</details>

<details>
 <summary>Create/close bdv window</summary>
 
## [BdvWindowCreate](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/BdvWindowCreate.java) [BDV_SciJava>Bdv>Create Empty BDV Frame]
Creates an empty Bdv window
### Input
* [GuavaWeakCacheService] **cacheService**:
* [boolean] **is2D**:Create a 2D Bdv window
* [ObjectService] **os**:
* [double] **px**:Location and size of the view of the new Bdv window
* [double] **py**:Location and size of the view of the new Bdv window
* [double] **pz**:Location and size of the view of the new Bdv window
* [double] **s**:Location and size of the view of the new Bdv window
* [String] **windowTitle**:Title of the new Bdv window
### Output
* [BdvHandle] **bdvh**:


## [BdvWindowClose](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/window/BdvWindowClose.java) [BDV_SciJava>Bdv>Display>Close Bdv Window]
Close Bdv Window
Scijava Command which closes a BdvHandle window
 The convert service is used to find the BdvHandle from its String representation.
 Valid Strings are:
 - the title of the JFrame containing the ViewerPanel of the BdvHandle Object
 - the result of the toString() method of the BdvHandle Object (= default SciJava name)
 -> Assumes the Bdv is containing within a single JFrame
### Input
* [String] **bdvh**:Name of the Bdv Window
* [ConvertService] **cs**:

</details>

<details>
 <summary>BigDataBrowser</summary>


## [BigDataBrowserPlugInSciJava](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/BigDataBrowserPlugInSciJava.java) [BDV_SciJava>Browse BigDataServer (SciJava)]
### Input
* [CommandService] **cs**:
* [LogService] **ls**:
* [String] **serverUrl**:

</details>

<details>
 <summary>BigDataViewer sources display commands</summary>

## [BdvSourcesHide](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/sources/BdvSourcesHide.java) [BDV_SciJava>Bdv>Display>Hide Sources]
Hide bdv sources
Hide bdv sources. Multiple sources can be specified.
### Input
* [BdvHandle] **bdvh**:Bdv window
* [String] **sourceIndexString**:Indexes of the sources, comma separated
Multiple sources can be specified; 0,4,7 or range 3:5


## [BdvSourcesSetColor](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/sources/BdvSourcesSetColor.java) [BDV_SciJava>Bdv>Display>Set Sources Color]
Set the color of bdv sources
Set the color of bdv sources. Multiple sources can be specified.
### Input
* [BdvHandle] **bdvh**:Bdv Window
* [ColorRGB] **c**:Color
* [String] **sourceIndexString**:Indexes ('0,3:5'), of the sources
description test


## [BdvSourcesSetMinMax](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/sources/BdvSourcesSetMinMax.java) [BDV_SciJava>Bdv>Display>Set Sources Min Max Display]
Set the min and max display values of bdv sources
Set the min and max display values of bdv sources. Multiple sources can be specified.
### Input
* [BdvHandle] **bdvh**:Bdv Window
* [double] **max**:Maximum display value
* [double] **min**:Minimum display value
* [String] **sourceIndexString**:Indexes ('0,3:5'), of the sources to process


## [BdvSourcesShow](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/sources/BdvSourcesShow.java) [BDV_SciJava>Bdv>Display>Show Sources]
Show bdv sources
Show bdv sources. Multiple sources can be specified.
### Input
* [BdvHandle] **bdvh**:Bdv Window
* [String] **sourceIndexString**:Indexes ('0,3:5'), of the sources to process

</details>

<details>
 <summary>BigDataViewer window display commands</summary>

## [BdvWindowGetCurrentTransform](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/window/BdvWindowGetCurrentTransform.java) [BDV_SciJava>Bdv>Display>Get Current Location]
Get current location of Bdv window
Scijava Command which returns the current transform ( = location ) of a Bdv window
 This correspond to storing the current view of a Bdv window
 Output: an AffineTransform3D object which corresponds to the current view of the input Bdv window
 an optional name can be given in order to label this view.
 The link between the name and the affine transform is stored in the cache service
### Input
* [BdvHandle] **bdv_h**:Bdv Window
* [GuavaWeakCacheService] **cs**:
* [String] **locationName**:Label for current Bdv Location
### Output
* [AffineTransform3D] **at3D**:AffineTransform3D object which corresponds to the current view of the bdv window


## [BdvWindowRename](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/window/BdvWindowRename.java) [BDV_SciJava>Bdv>Display>Rename Bdv Window]
Renames a Bdv Window
Specifying a name facilitates the selection of a Bdv Window in IJ1 Macro language
### Input
* [BdvHandle] **bdvh**:Bdv Window
* [ObjectService] **os**:
* [String] **windowTitle**:New Bdv Window Title
### Output
* [BdvHandle] **bdvh**:Bdv Window


## [BdvWindowSelect](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/window/BdvWindowSelect.java) [BDV_SciJava>Bdv>Display>Select Bdv Window]
Puts in front/focus a Bdv Window
Useful for IJ1 Macro Language programming
### Input
* [String] **bdvh**:Name of the Bdv window
* [ConvertService] **cs**:


## [BdvWindowSetCurrentTransform](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/window/BdvWindowSetCurrentTransform.java) [BDV_SciJava>Bdv>Display>Set Current Location]
Set the location of the current view of a Bdv Window
Do not check whether the new view keeps a direct orthonormal view -> take care!
### Input
* [AffineTransform3D] **at3D**:Affine Transform specifying the Bdv window view location
* [BdvHandle] **bdvh**:Input Bdv Window


## [BdvWindowSynchronize](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/window/BdvWindowSynchronize.java) [BDV_SciJava>Bdv>Display>Synchronize 2 Bdvs]
Synchronizes the location of 2 Bdv windows
Synchronizes the location of 2 Bdv windows
 * One is the master = controlling the slave window
 * A thread checks every syncDelayInMs ms if the views are identical or not, if not, then the slave window is updated
 * The syncrnoization can be stopped temporarily thanke to the SwingSyncBdvHandleViewer class which is triggered
 * Synchronization can be chained to synchrnoize more than two viewers
### Input
* [BdvHandle] **hMaster**:Master Bdv Window
* [BdvHandle] **hSlave**:Slave Bdv Window
* [int] **syncDelayInMs**:Synchronization delay (ms)
### Output
* [SyncBdvHandle] **sbh**:


## [BdvWindowTranslateOnSource](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/display/window/BdvWindowTranslateOnSource.java) [BDV_SciJava>Bdv>Display>Translate Bdv Location On Source]
Translate the location of the Bdv window to the right corner of the specified indexed source
Translate the location of the Bdv window to the right corner of the specified indexed source
 * No rotation or scaling is attempted to fit in a better way the specified source:
 * it is not guaranteed tha the specified source is not skewed or is in the direct orientation
 * thus it's complicated to keep a direct orthonormal referential when trying to align better the bdv window and the source
 
### Input
* [BdvHandle] **bdvh**:
* [CommandService] **cs**:
* [int] **sourceIndex**:

</details>

<details>
 <summary>Sources edition</summary>

## [BdvSourcesDuplicate](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/BdvSourcesDuplicate.java) [BDV_SciJava>Bdv>Edit Sources>Duplicate Sources]
Duplicate sources
Sources are duplicated by reference. So any modification of one of the duplicatedsource will affect all sources. One bug of this command is that the ConverterSetup isnot transfered -> It is not possible to change display settings (easily) on the duplicated source.


## [BdvSourcesRemove](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/BdvSourcesRemove.java) [BDV_SciJava>Bdv>Edit Sources>Remove Sources]
Remove source from a Bdv Window
Removing a source can cause issue at the moment and indexation problems,especially when working with SpimData. Try to avoid this command. One optionis to create a new Bdv Window and transfer only the needed source through theBdvSourcesDuplicate command.

</details>

<details>
 <summary>BigWarp</summary>


## [BigWarpGetTransform](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/register/BigWarpGetTransform.java) [BDV_SciJava>Bdv>Edit Sources>Register>Get BigWarp Transform]
Get the current transformation specified by a BigWarp instance
### Input
* [BdvHandle] **bdvh**:Input Bdv Window
### Output
* [RealTransform] **realtransform**:


## [BigWarpInitWithBdvSources](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/register/BigWarpInitWithBdvSources.java) [BDV_SciJava>Bdv>Edit Sources>Register>BigWarp (SciJava)]
Initializes BigWarp using pre existing set of SourceAndConverter
Initializes BigWarp using pre existing set of SourceAndConverter
### Input
* [BdvHandle] **bdv_h_fixed**:Input Bdv Window containing fixed sources
* [BdvHandle] **bdv_h_moving**:Input Bdv Window containing moving sources
* [GuavaWeakCacheService] **cs**:
* [String] **idx_src_fixed**:Fixed source indexes ('2,3:5'), starts at 0
* [String] **idx_src_moving**:Moving source indexes ('2,3:5'), starts at 0
* [ObjectService] **os**:
### Output
* [BdvHandle] **bdvHandleP**:
* [BdvHandle] **bdvHandleQ**:

</details>

<details>
 <summary>Sources transformations (affine, warp, resampling)</summary>

## [BdvSourcesAffineTransform](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/transform/BdvSourcesAffineTransform.java) [BDV_SciJava>Bdv>Edit Sources>Transform>Affine>Transform Sources (AffineTransform3D)]
Performs an affinetransform on bdv sources.
If transformInPlace is checked, then the source is transformed in place, which means that the output can be list only. If not, the transformationis made through a wrapping whithin a TransformedSource. An AffineTransform3D object should be availablewithin ObjectService to use this command in the GUI
### Input
* [AffineTransform3D] **at**:Affine Transform Matrix
* [boolean] **transformInPlace**:Transform the source in place = the original transform is lost


## [BdvSourcesAffineTransformWithString](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/transform/BdvSourcesAffineTransformWithString.java) [BDV_SciJava>Bdv>Edit Sources>Transform>Affine>Transform Sources (Affine, string)]
Performs an affinetransform on bdv sources.
 If transformInPlace is checked, then the source is transformed in place, which means that the output can be list only. If not, the transformationis made through a wrapping whithin a TransformedSource. The affine transform is a 4x3 matrix separated with comma
### Input
* [String] **stringMatrix**:Affine Transform Matrix
* [boolean] **transformInPlace**:Transform the source in place = the original transform is lost


## [BdvSourcesResample](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/transform/BdvSourcesResample.java) [BDV_SciJava>Bdv>Edit Sources>Transform>Resample Sources]
Resample a Bdv Source like another one
This command is useful to save any sort of source which is notsampled on a grid (procedural, warped), and to resample it ontoan appropriate grid (defined by the source template). Once resampled, the sourcecan be exported as a spim Xml Dataset, for instance.
### Input
* [BdvHandle] **bdv_dst**:Bdv Frame containing source resampling template
* [int] **idxSourceDst**:Index of the source resampling template
* [boolean] **reuseMipMaps**:Reuse mipmaps of the resampling template source


## [BdvSourcesWarp](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/transform/BdvSourcesWarp.java) [BDV_SciJava>Bdv>Edit Sources>Transform>Transform Sources (realtransform)]
Takes a transform (rather not affine), and applies it on specified sources
If the transform is affine, it is preferable to use the BdvSourceAffineTransformcommand. If the transform is more general (like a Warping, typically an output of BigWarp), then this method can be used
### Input
* [RealTransform] **rt**:RealTransform object


## [CreateAffineTransformCommand](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/edit/transform/CreateAffineTransformCommand.java) [BDV_SciJava>Bdv>Edit Sources>Transform>Affine>New Affine Transform]
Creates an affine transform and makes it accessible for other commands
Affine transform is a 4x3 matrix; elements are separated by comma.
### Input
* [String] **stringMatrix**:Affine Transform Matrix
### Output
* [AffineTransform3D] **at3D**:

</details>

<details>
 <summary>Sources export (ImagePlus and Dataset)</summary>

## [BdvSourcesBdvViewToImagePlus](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/export/BdvSourcesBdvViewToImagePlus.java) [BDV_SciJava>Bdv>Export Sources>As ImagePlus]
Export a Bdv View as an ImagePlus (an AffineTransform3D is required to specify the location)
Limitations : do not work with multiple ARGB source -> please loop this command
Do not work with multiple source of multiple Pixel Type -> please loop this command
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [boolean] **ignoreSourceLut**:Ignore Source LUT (check for RGB)
* [boolean] **interpolate**:Interpolate
* [int] **mipmapLevel**:Mipmap level, 0 for highest resolution
* [double] **samplingXYInPhysicalUnit**:XY Pixel size sampling (physical unit)
* [double] **samplingZInPhysicalUnit**:Z Pixel size sampling (physical unit)
* [String] **sourceIndexString**:Source indexes ('2,3:5'), starts at 0
* [int] **timepoint**:Timepoint
* [AffineTransform3D] **transformedSourceToViewer**:BigDataViewer View (affine transform 3D)
* [boolean] **wrapMultichannelParallel**:Parallelize when exporting several channels
* [double] **xSize**:Physical Size X
* [double] **ySize**:Physical Size Y
* [double] **zSize**:Physical Size Z
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [ImagePlus] **imp**:


## [BdvSourcesCurrentBdvViewToImagePlus](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/export/BdvSourcesCurrentBdvViewToImagePlus.java) [BDV_SciJava>Bdv>Export Sources>As ImagePlus (current view)]
Export current Bdv View as an ImagePlus
Limitations : do not work with multiple ARGB source -> please loop this command
Do not work with multiple source of multiple Pixel Type -> please loop this command
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [boolean] **ignoreSourceLut**:Ignore Source LUT (check for RGB)
* [boolean] **interpolate**:Interpolate
* [boolean] **matchWindowSize**:Match bdv frame window size
* [int] **mipmapLevel**:Mipmap level, 0 for highest resolution
* [double] **samplingXYInPhysicalUnit**:XY Pixel size sampling (physical unit)
* [double] **samplingZInPhysicalUnit**:Z Pixel size sampling (physical unit)
* [String] **sourceIndexString**:Source indexes ('2,3:5'), starts at 0
* [int] **timepoint**:Timepoint
* [boolean] **wrapMultichannelParallel**:Parallelize when exporting several channels
* [double] **xSize**:Physical Size X
* [double] **ySize**:Physical Size Y
* [double] **zSize**:Physical Size Z
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [ImagePlus] **imp**:


## [BdvSourcesExportToXMLHDF5_RecomputePyramid](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/export/BdvSourcesExportToXMLHDF5_RecomputePyramid.java) [BDV_SciJava>Bdv>Export Sources>As Xml/Hdf5 SpimDataset]
Export a set of Sources into a new Xml/Hdf5 bdv dataset
Mipmaps are recomputed. Do not work with RGB images. Other pixel types are truncated to their int value between 0 and 65535
### Input
* [boolean] **autoMipMap**:
* [BdvHandle] **bdvh**:BigDataViewer Frame
* [boolean] **convertToUnsignedShortType**:
* [int] **nThreads**:
* [int] **nTimePointBegin**:
* [int] **nTimePointEnd**:
* [int] **scaleFactor**:
* [String] **sourceIndexString**:Sources to save ('2,3:5'), starts at 0
* [int] **subDivX**:
* [int] **subDivY**:
* [int] **subDivZ**:
* [boolean] **tryMergeIntoChannelWheneverPossible**:
* [File] **xmlFile**:
### Output
* [AbstractSpimData] **spimData**:

</details>

<details>
 <summary>Sources import (ImagePlus and Dataset)</summary>

## [BdvAppendImagePlus](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/BdvAppendImagePlus.java) [BDV_SciJava>Bdv>Put Sources>Current IJ1 Image (buggy) []]
plugin to append the current image in a bdv window
### Input
* [BdvHandle] **bdv_h**:
* [ImagePlus] **curr**:
### Output
* [BdvHandle] **bdv_h**:


## [BdvAppendImgPlus](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/BdvAppendImgPlus.java) [BDV_SciJava>Bdv>Put Sources>Current IJ1 Image [ImgLib2]]
plugin to append the current image in a bdv window, using ImgLib2 wrapping (limited)
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [ImgPlus] **img**:
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame


## [BdvAppendSpimData](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/BdvAppendSpimData.java) [BDV_SciJava>Bdv>Put Sources>SpimDataset]
Plugin to append a spimdata dataset into a bdv window. A Spimdataset should be present in the ObjectService for this command to work. Use Spimdata commandfor that beforehand.
### Input
* [BdvHandle] **bdv_h**:
* [GuavaWeakCacheService] **cs**:
* [AbstractSpimData] **spimData**:Input Spimdataset
### Output
* [BdvHandle] **bdv_h**:


## [BdvAppendWithSciFIO](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/BdvAppendWithSciFIO.java) [BDV_SciJava>Bdv>Put Sources>Image File [SCIFIO]]
Command which opens a file using SciFIO and appends it into in a bdv window.
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [File] **file**:Image File
* [String] **sourceIndexString**:Source indexes ('2,3:5'), starts at 0
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame

</details>

<details>
 <summary>Sample Sources</summary>

## [GrayMandelbrotCommand](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/samples/GrayMandelbrotCommand.java) [BDV_SciJava>Bdv>Put Sources>Samples>Bdv example source - Fractal (Gray)]
Adds the mandelbrot set into a bdv window ( gray level  between 0 and 255)
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame


## [GrayWave3DSampleCommand](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/samples/GrayWave3DSampleCommand.java) [BDV_SciJava>Bdv>Put Sources>Samples>Bdv example source - Wave 3D (Gray)]
Procedurally generated wave3d image, gray levels.
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [ConvertService] **cs**:
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame


## [MandelbrotCommand](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/samples/MandelbrotCommand.java) [BDV_SciJava>Bdv>Put Sources>Samples>Bdv example source - Fractal ]
Adds the mandelbrot set into a bdv window with a lookuptable
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [String] **choice**:LUT name
* [ConvertService] **cs**:
* [LUTService] **lutService**:
* [ColorTable] **table**:LUT
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame


## [VoronoiLabel3DCommand](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/samples/VoronoiLabel3DCommand.java) [BDV_SciJava>Bdv>Put Sources>Samples>Bdv example source - Voronoi Label 3D]
Random 3D points defining voronoi cells, in 3D.
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [boolean] **computeImageBeforeDisplay**:Compute image before displaying it (avoid for big image)
* [int] **numLabels**:Number of Random Points = number of voronoi cells
* [int] **sx**:Number of Pixels in X
* [int] **sy**:Number of Pixels in Y
* [int] **sz**:Number of Pixels in Z
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame


## [Wave3DSampleCommand](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/open/samples/Wave3DSampleCommand.java) [BDV_SciJava>Bdv>Put Sources>Samples>Bdv example source - Wave 3D]
Procedurally generated wave3d image, with a lookuptable.
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [String] **choice**:LUT name
* [ConvertService] **cs**:
* [LUTService] **lutService**:
* [ColorTable] **table**:LUT
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame

</details>

<details>
 <summary>SpimDataset Import and Save</summary>

## [SpimdatasetOpenBigDataServer](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/spimdata/SpimdatasetOpenBigDataServer.java) [BDV_SciJava>SpimDataset>Open>SpimDataset [BigDataServer]]
Command that opens a Spimdata dataset from a BigDataServer. Click on Show to display it.
### Input
* [String] **datasetName**:Dataset Name
* [String] **urlServer**:Big Data Server URL
### Output
* [AbstractSpimData] **spimData**:


## [SpimdatasetOpenImaris](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/spimdata/SpimdatasetOpenImaris.java) [BDV_SciJava>SpimDataset>Open>SpimDataset [Imaris File]]
Command that opens a Spimdata dataset from an Imaris file. Click on Show to display it.
### Input
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [boolean] **createNewWindow**:Open in new BigDataViewer window
* [GuavaWeakCacheService] **cs**:
* [File] **file**:Imaris File
### Output
* [BdvHandle] **bdv_h**:BigDataViewer Frame
* [AbstractSpimData] **spimData**:


## [SpimdatasetOpenXML](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/spimdata/SpimdatasetOpenXML.java) [BDV_SciJava>SpimDataset>Open>SpimDataset [XML File]]
Command that opens a Spimdata dataset from a xml Spimdata file. Click on Show to display it.
### Input
* [File] **file**:XML File
### Output
* [AbstractSpimData] **sd**:


## [SpimdatasetSave](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/spimdata/SpimdatasetSave.java) [BDV_SciJava>SpimDataset>Save SpimDataset]
Command that saves a Spimdata dataset object
Save a spimdata dataset. Manual transform can be pushed into thedataset by looking recursively through wrapped Source. Limitations are to be expected.Only pushing transformations from timepoint 0 at the moment
### Input
* [GuavaWeakCacheService] **cs**:
* [boolean] **pushSourceTransformationsToDataset**:
* [AbstractSpimData] **spimData**:
* [File] **xmlFileName**:


## [SpimdatasetUpdateBdvWindow](https://github.com/BIOP/bigdataviewer_scijava/tree/master/src/main/java/ch/epfl/biop/bdv/scijava/command/spimdata/SpimdatasetUpdateBdvWindow.java) [BDV_SciJava>Bdv>Display>SpimDataset>Update Bdv]
Updates the associated Bdv to a Spimdataset. If the spimdata object has been modifiedthen the transformations will be updated in the Bdv Window
### Input
* [GuavaWeakCacheService] **cs**:
* [AbstractSpimData] **spimData**:
* [int] **timePoint**:

</details>





