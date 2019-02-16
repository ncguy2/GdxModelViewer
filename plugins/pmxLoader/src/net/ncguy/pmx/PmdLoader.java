package net.ncguy.pmx;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import net.ncguy.pmx.mmd.PmdReader;
import net.ncguy.pmx.mmd.Pmx;

import java.io.File;
import java.io.IOException;

public class PmdLoader extends CommonLoader {

    public PmdLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    protected Pmx readFile(File file) throws IOException {
        Pmx read = PmdReader.read(file);

        for (int i = 0; i < read.getTextures().length; i++) {
            read.getTextures()[i] = trimStringToTermination(read.getTextures()[i]);
        }

        return read;
    }

    private String trimStringToTermination(String str) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(c == '\u0000') {
                break;
            }
            sb.append(c);
        }

        return sb.toString();
    }

}
