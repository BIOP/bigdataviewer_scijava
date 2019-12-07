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

@Plugin(type = Command.class, menuPath = ScijavaBdvRootMenu+"Bdv>Display>Translate Bdv Location On Source")
public class TranslateBdvOnSource implements Command {

    @Parameter
    BdvHandle bdvh;

    @Parameter
    int sourceIndex;

    @Parameter
    CommandService cs;

    public void run() {

        AffineTransform3D atSrc = new AffineTransform3D();
        int timepoint = bdvh.getViewerPanel().getState().getCurrentTimepoint();
        Source ss = bdvh.getViewerPanel().getState().getSources().get(sourceIndex).getSpimSource();
        ss.getSourceTransform(timepoint,0,atSrc);

        double[] src = {0,0,0};
        double[] tgt = new double[3];

        atSrc.apply(src,tgt);

        AffineTransform3D atBdv = new AffineTransform3D();
        bdvh.getViewerPanel().getState().getViewerTransform(atBdv);

        atBdv.set(-tgt[0]*getNormTransform(0,atBdv),0,3);
        atBdv.set(-tgt[1]*getNormTransform(1,atBdv),1,3);
        atBdv.set(-tgt[2]*getNormTransform(2,atBdv),2,3);

        cs.run(BdvSetCurrentTransform.class, true, "bdvh", bdvh, "at3D", atBdv);
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

        double [][] m = new double[3][4];//out.getRowPackedCopy();
        m[0][0] = xDir.x;
        m[0][1] = xDir.y;
        m[0][2] = xDir.z;
        //m[0][3] = at.get(0,3);///scaleForShift;

        m[1][0] = yDirOrtho.x;
        m[1][1] = yDirOrtho.y;
        m[1][2] = yDirOrtho.z;
        //m[1][3] = at.get(1,3);///scaleForShift;

        m[2][0] = zDirOrtho.x;
        m[2][1] = zDirOrtho.y;
        m[2][2] = zDirOrtho.z;
        //m[2][3] = at.get(2,3);///scaleForShift;

        /*
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
        out.set(at.get(2,3),2,3);// /scaleForShift
        */

        out.set(m);


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
