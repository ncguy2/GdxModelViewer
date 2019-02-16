package net.ncguy.fbx;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.g3d.Model;
import net.ncguy.plugin.api.AssetLoaderDefinition;
import net.ncguy.plugin.api.LoaderPlugin;

import java.util.Collections;
import java.util.List;

public class FbxPlugin implements LoaderPlugin {

    @Override
    public String name() {
        return "Fbx Loader";
    }

    @Override
    public int numDefinitions() {
        return 1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AssetLoaderDefinition<?>> definitions(FileHandleResolver resolver) {
        return Collections.singletonList(
                new AssetLoaderDefinition("Fbx loader", Model.class, new FbxAssetLoader(resolver)).addExtensions(".fbx")
        );
    }
}
