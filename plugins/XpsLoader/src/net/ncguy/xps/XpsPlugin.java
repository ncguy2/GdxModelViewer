package net.ncguy.xps;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.g3d.Model;
import net.ncguy.plugin.api.AssetLoaderDefinition;
import net.ncguy.plugin.api.LoaderPlugin;
import net.ncguy.xps.loader.XpsDataLoader;

import java.util.Collections;
import java.util.List;

public class XpsPlugin implements LoaderPlugin {
    @Override
    public String name() {
        return "XPS loader";
    }

    @Override
    public int numDefinitions() {
        return 1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AssetLoaderDefinition<?>> definitions(FileHandleResolver resolver) {
        return Collections.singletonList(
                new AssetLoaderDefinition("Xps binary", Model.class, new XpsDataLoader(resolver)).addExtensions(".mesh", ".xps")
        );
    }
}
