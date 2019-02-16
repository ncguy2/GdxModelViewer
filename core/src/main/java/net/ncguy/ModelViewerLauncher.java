package net.ncguy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.ui.VisUI;
import net.ncguy.asset.AssetHandler;
import net.ncguy.asset.Assets;

import java.util.ArrayList;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ModelViewerLauncher extends Game {

    public static List<String> supportedMeshExtensions = new ArrayList<>();
    public static List<String> supportedTextureExtensions = new ArrayList<>();

    @Override
    public void create() {

        List<String> loadersOfType = AssetHandler.instance().getLoadersOfType(Model.class);

        System.out.println("Model Loaders: ");
        for (String s : loadersOfType) {
            System.out.printf("\t%s%n", s);
            supportedMeshExtensions.add(s);
        }

        loadersOfType = AssetHandler.instance().getLoadersOfType(Texture.class);

        System.out.println("Texture Loaders: ");
        for (String s : loadersOfType) {
            System.out.printf("\t%s%n", s);
            supportedTextureExtensions.add(s);
        }

        ShaderProgram.pedantic = false;
        ShaderProgram.prependVertexCode = "#version 330 core\n";
        ShaderProgram.prependFragmentCode = "#version 330 core\n";
        Assets.load();
        VisUI.load();
        setScreen(new FirstScreen());
    }
}