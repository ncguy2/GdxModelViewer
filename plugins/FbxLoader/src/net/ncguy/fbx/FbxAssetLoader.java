package net.ncguy.fbx;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import net.ncguy.fbx.data.FbxModel;

import java.nio.ByteBuffer;

public class FbxAssetLoader extends ModelLoader<ModelLoader.ModelParameters> {

    public FbxAssetLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public ModelData loadModelData(FileHandle fileHandle, ModelParameters parameters) {
        ByteBuffer buffer = ByteBuffer.wrap(fileHandle.readBytes());
        FbxModel model = FbxLoader.read(buffer);

        return null;
    }
}
