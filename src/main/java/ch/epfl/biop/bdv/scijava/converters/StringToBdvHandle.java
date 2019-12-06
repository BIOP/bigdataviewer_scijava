package ch.epfl.biop.bdv.scijava.converters;

import bdv.util.BdvHandle;
import bdv.util.BdvHandleHelper;
import org.scijava.convert.AbstractConverter;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = org.scijava.convert.Converter.class)
public class StringToBdvHandle<I extends String, O extends BdvHandle> extends AbstractConverter<I, O> {
    @Parameter
    ObjectService os;

    @Override
    public <T> T convert(Object src, Class<T> dest) {
        os.getObjects(BdvHandle.class).stream().forEach(bdvh -> {
            System.out.println(bdvh.toString());
            System.out.println(BdvHandleHelper.getWindowTitle(bdvh));
            System.out.println("src="+src);
        });
        return (T) os.getObjects(BdvHandle.class).stream().filter(bdvh ->
                (bdvh.toString().equals(src))||(BdvHandleHelper.getWindowTitle(bdvh).equals(src))
                ).findFirst().get();
    }

    @Override
    public Class<O> getOutputType() {
        return (Class<O>) BdvHandle.class;
    }

    @Override
    public Class<I> getInputType() {
        return (Class<I>) String.class;
    }
}
