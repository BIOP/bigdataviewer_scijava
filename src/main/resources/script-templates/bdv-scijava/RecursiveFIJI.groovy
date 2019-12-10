
import bdv.util.Procedural3DImageShort
import bdv.util.BdvFunctions
import bdv.util.BdvOptions
import bdv.util.BdvHandle
import net.imglib2.FinalInterval
import net.imglib2.Interval
import net.imglib2.type.numeric.ARGBType
// Input : provided by Single Input Preprocessor in case no widow is present
#@BdvHandle bdv_h
// Output : allow to updates list of sources
#@output BdvHandle bdv_h

// Simple FIJI Image stored as an array
fijiData =
        [[0,0,0,0,0,0,0,0,0],
         [0,1,1,1,1,1,1,1,0],
         [0,1,0,0,0,0,0,0,0],
         [0,1,0,1,0,1,0,1,0],
         [0,1,0,1,0,1,0,1,0],
         [0,1,0,1,0,1,0,1,0],
         [0,1,0,0,0,1,0,0,0],
         [0,1,0,1,1,1,0,0,0],
         [0,0,0,0,0,0,0,0,0]] as short[][];

// Declare a procedural image
def s = new Procedural3DImageShort({p -> getRecursiveFiji(p[1], p[0], p[2])}).getRRA();

// Interval (mainly useless here, but required by BdvFunctions
Interval interval = new FinalInterval([ 0, 0, 0] as long[], [ 9, 9, 0 ] as long[]);

// Display the source in the bdv_h window
bss = BdvFunctions.show( s , interval, "FIJI", BdvOptions.options().addTo(bdv_h) );

// Display options
bss.setDisplayRange(0,1);
bss.setColor(new ARGBType(ARGBType.rgba(101,164,227,255)));


//------------- FUNCTION for recursive FIJI Image generation

int getRecursiveFiji(double x, double y, double level) {
    def valueLevel = (int) (fijiData[((int)x%9)][((int)y%9)])
    if (level<=0) {
        return valueLevel
    } else {
        if (valueLevel==1) {
            if (level>2) {
                level=2;
            }
            return getRecursiveFiji(x*9,y*9,level-1)
        } else {
            return 0
        }
    }
}