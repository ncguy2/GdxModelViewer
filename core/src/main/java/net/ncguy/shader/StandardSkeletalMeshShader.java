package net.ncguy.shader;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.ncguy.utils.ShaderPreprocessor;

public class StandardSkeletalMeshShader extends DefaultShader {

    static Config getDefaultConfig(Renderable renderable) {
        Config cfg = new Config();

        Shaders.ShaderRef ref = Shaders.GetShader("skeletalMesh", Files.FileType.Internal);

        ShaderProgram.prependVertexCode = "#version 330 core\n";
        ShaderProgram.prependFragmentCode = "#version 330 core\n";

        cfg.vertexShader = ShaderPreprocessor.ReadShader(ref.vertHandle);
        cfg.fragmentShader = ShaderPreprocessor.ReadShader(ref.fragHandle);

        String prefix = createPrefix(renderable, cfg);

        cfg.vertexShader = prefix + cfg.vertexShader;
        cfg.fragmentShader = prefix + cfg.fragmentShader;

        return cfg;
    }

    public StandardSkeletalMeshShader(Renderable renderable) {
        super(renderable);
    }
}
