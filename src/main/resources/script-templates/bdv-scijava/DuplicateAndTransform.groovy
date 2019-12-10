
import ch.epfl.biop.bdv.scijava.command.BdvWindowCreate
import ch.epfl.biop.bdv.scijava.command.edit.transform.BdvSourcesAffineTransform
import bdv.util.BdvHandle

// bdv handle containing the source to transform
#@BdvHandle bdv_h_in
// output bdv window
#@output BdvHandle bdv_h_out

// index of the source in bdv handle input
#@int index_src

// Command Service
#@CommandService cs

bdv_h_out = (BdvHandle) cs.run(BdvWindowCreate.class, true,
        "is2D",false,
        "windowTitle","Bdv_out",
        "px",0.0,
        "py",0.0,
        "pz",-1.0,
        "s",12.0).get().getOutput("bdvh");

affine_matrix = "[3d-affine: (0.45, -0.05, 0.0, 0.4, 0.05, 0.5, 0.0, -0.2, 0.0, 0.0, 1.0, -0.6)]";

cs.run(BdvSourcesAffineTransform.class, true,
        "at", affine_matrix,
        "transformInPlace", false,
        "bdvh", bdv_h_in,
        "bdvh_out", bdv_h_out,
        "sourceIndexString", index_src,
        "output_mode", "Add To Bdv").get()

for (int i=0;i<2;i++) {
    cs.run(BdvSourcesAffineTransform.class, true,
            "at", affine_matrix,
            "transformInPlace", false,
            "bdvh", bdv_h_out,
            "bdvh_out", bdv_h_out,
            "sourceIndexString", i,
            "output_mode", "Add To Bdv").get()
}