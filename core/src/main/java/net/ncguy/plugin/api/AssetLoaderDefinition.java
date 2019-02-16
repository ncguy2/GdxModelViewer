package net.ncguy.plugin.api;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssetLoaderDefinition<T> {

    public AssetLoaderDefinition(String displayName, Class<T> loadedType, AssetLoader<T, AssetLoaderParameters<T>> loader) {
        this.displayName = displayName;
        this.loadedType = loadedType;
        this.loader = loader;
    }

    public String displayName;
    public Class<T> loadedType;
    public AssetLoader<T, AssetLoaderParameters<T>> loader;
    public List<String> supportedExtensions = new ArrayList<>();

    public AssetLoaderDefinition<T> addExtensions(String... exts) {
        Collections.addAll(supportedExtensions, exts);
        return this;
    }

}
