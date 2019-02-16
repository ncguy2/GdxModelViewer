package net.ncguy.shader;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.text.MessageFormat;

public class Shaders {

    private Shaders() {}

    public static ShaderRef GetShader(String name, Files.FileType type) {
        ShaderRef ref = new ShaderRef();
        String pattern = "shaders/{0}/{0}.{1}";

        ref.vertHandle = Gdx.files.getFileHandle(MessageFormat.format(pattern, name, "vert"), type);
        ref.fragHandle = Gdx.files.getFileHandle(MessageFormat.format(pattern, name, "frag"), type);

        return ref;
    }

    public static class ShaderRef {
        public FileHandle vertHandle;
        public FileHandle fragHandle;
    }

}
