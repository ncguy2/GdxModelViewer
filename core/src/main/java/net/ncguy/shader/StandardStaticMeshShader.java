package net.ncguy.shader;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import net.ncguy.utils.ShaderPreprocessor;

public class StandardStaticMeshShader extends DefaultShader {

    static Config getDefaultConfig(Renderable renderable) {
        Config cfg = new Config();

        Shaders.ShaderRef ref = Shaders.GetShader("staticMesh", Files.FileType.Internal);

        cfg.vertexShader = ShaderPreprocessor.ReadShader(ref.vertHandle);
        cfg.fragmentShader = ShaderPreprocessor.ReadShader(ref.fragHandle);

        String prefix = createPrefix(renderable, cfg);

        cfg.vertexShader = prefix + cfg.vertexShader;
        cfg.fragmentShader = prefix + cfg.fragmentShader;

        return cfg;
    }

    public StandardStaticMeshShader(Renderable renderable) {
        super(renderable, getDefaultConfig(renderable), "");
    }

}
