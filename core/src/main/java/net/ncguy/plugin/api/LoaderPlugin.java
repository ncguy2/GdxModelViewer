package net.ncguy.plugin.api;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;

import java.util.List;

public interface LoaderPlugin {

    String name();
    int numDefinitions();
    List<AssetLoaderDefinition<?>> definitions(FileHandleResolver resolver);

}
