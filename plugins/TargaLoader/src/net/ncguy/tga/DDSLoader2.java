package net.ncguy.tga;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import model.DDSFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.IntBuffer;

public class DDSLoader2 extends AsynchronousAssetLoader<Texture, TextureLoader.TextureParameter> {

    public DDSLoader2(FileHandleResolver resolver) {
        super(resolver);
    }

    int width;
    int height;
    IntBuffer pixelBuffer;

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        File f = file.file();
        try {
            if(!DDSFile.isValidDDSImage(f)) {
                throw new IOException(fileName + " is not a valid DDS file");
            }
        } catch (IOException e) {
            throw new IOError(e);
        }

        DDSFile ddsFile = new DDSFile(f);
        BufferedImage data = ddsFile.getData();
        int size = width * height;
        pixelBuffer = IntBuffer.allocate(size);
        pixelBuffer.position(0);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixelBuffer.put(data.getRGB(x, y));
            }
        }
        pixelBuffer.position(0);
    }

    @Override
    public Texture loadSync(AssetManager manager, String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        return TgaLoaderPlugin.buildTexture(width, height, pixelBuffer);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        return null;
    }
}
