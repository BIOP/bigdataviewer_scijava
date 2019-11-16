package ch.epfl.biop.bdv.scijava.command.display;

import bdv.util.BdvHandle;
import bdv.viewer.Source;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import static ch.epfl.biop.bdv.scijava.command.Info.ScijavaBdvRootMenu;
import static java.lang.Math.sqrt;

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Set Bdv Location On Source")
public class AlignBdvOnSource implements Command {

    @Parameter
    BdvHandle bdvh;

    @Parameter
    int sourceIndex;

    @Parameter
    CommandService cs;

    public void run() {

        AffineTransform3D at3D = new AffineTransform3D();
        int timepoint = bdvh.getViewerPanel().getState().getCurrentTimepoint();
        Source ss = bdvh.getViewerPanel().getState().getSources().get(sourceIndex).getSpimSource();
        ss.getSourceTransform(timepoint,0,at3D);

        AffineTransform3D atViewer = new AffineTransform3D();
        bdvh.getViewerPanel().getState().getViewerTransform(atViewer); // Get current transformation by the viewer state and puts it into sourceToImgPlus
        double bdvScale = getNormTransform(0,atViewer);

        AffineTransform3D at3Di = new AffineTransform3D();
        at3Di.set(at3D.inverse());
        at3Di = makeRigidOrthoNormal(at3Di);
        at3Di.scale(bdvScale);

        cs.run(BdvSetCurrentTransform.class, true, "bdv_h", bdvh, "at3D", at3Di);
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

    double scaleForShift = 1;

    public AffineTransform3D makeRigidOrthoNormal(AffineTransform3D at) {
        Pt3D xDir = new Pt3D(at.get(0,0), at.get(0,1), at.get(0,2));
        scaleForShift = xDir.getNorm();
        xDir.normalize();
        Pt3D yDir = new Pt3D(at.get(1,0), at.get(1,1), at.get(1,2));
        yDir.normalize();
        Pt3D zDir = new Pt3D(at.get(2,0), at.get(2,1), at.get(2,2));
        zDir.normalize();

        Pt3D zDirOrtho = xDir.prodVect(yDir);

        if (zDir.prodScal(zDirOrtho)<0) {
            zDirOrtho.x*=-1;
            zDirOrtho.y*=-1;
            zDirOrtho.z*=-1;
        }

        Pt3D yDirOrtho = zDirOrtho.prodVect(xDir);

        if (yDir.prodScal(yDirOrtho)<0) {
            yDirOrtho.x*=-1;
            yDirOrtho.y*=-1;
            yDirOrtho.z*=-1;
        }

        AffineTransform3D out = new AffineTransform3D();

        out.set(xDir.x,0,0);
        out.set(xDir.y,0,1);
        out.set(xDir.z,0,2);
        out.set(at.get(0,3)/scaleForShift,0,3);


        out.set(yDirOrtho.x,1,0);
        out.set(yDirOrtho.y,1,1);
        out.set(yDirOrtho.z,1,2);
        out.set(at.get(1,3)/scaleForShift,1,3);

        out.set(zDirOrtho.x,2,0);
        out.set(zDirOrtho.y,2,1);
        out.set(zDirOrtho.z,2,2);
        out.set(at.get(2,3)/scaleForShift,2,3);

        return out;
    }

    class Pt3D {
        double x,y,z;

        public Pt3D(double x, double y, double z) {
            this.x=x;
            this.y=y;
            this.z=z;
        }

        public double getNorm() {
            return sqrt(x*x+y*y+z*z);
        }

        public void normalize() {
            double norm = sqrt(x*x+y*y+z*z);
            x/=norm;
            y/=norm;
            z/=norm;
        }

        public double prodScal(Pt3D p) {
            return x*p.x + y*p.y + z*p.z;
        }

        public Pt3D prodVect(Pt3D p) {
            return new Pt3D (y*p.z-z*p.y, z*p.x-x*p.z, x*p.y-y*p.x);
        }
    }
}
