package net.ncguy.pmx;

import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.g3d.Model;
import net.ncguy.plugin.api.AssetLoaderDefinition;
import net.ncguy.plugin.api.LoaderPlugin;

import java.util.Arrays;
import java.util.List;

public class PmxLoaderPlugin implements LoaderPlugin {

    @Override
    public String name() {
        return "MMD Loader";
    }

    @Override
    public int numDefinitions() {
        return 2;
    }

    @Override
    public List<AssetLoaderDefinition<?>> definitions(FileHandleResolver resolver) {
        //noinspection unchecked
        return Arrays.asList(
                new AssetLoaderDefinition<>("Pmx Loader", Model.class, (AssetLoader) new PmxLoader(resolver)).addExtensions(".pmx"),
                new AssetLoaderDefinition<>("Pmd Loader", Model.class, (AssetLoader) new PmdLoader(resolver)).addExtensions(".pmd")
        );
    }
}
