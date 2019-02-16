package net.ncguy.pmx;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import net.ncguy.pmx.mmd.Pmx;
import net.ncguy.pmx.mmd.PmxReader;

import java.io.File;
import java.io.IOException;

public class PmxLoader extends CommonLoader {
    public PmxLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    protected Pmx readFile(File file) throws IOException {
        return PmxReader.read(file);
    }
}
