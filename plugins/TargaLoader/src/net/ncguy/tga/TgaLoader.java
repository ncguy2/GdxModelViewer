package net.ncguy.tga;


import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static net.ncguy.tga.TgaLoaderPlugin.buildTexture;

public class TgaLoader extends AsynchronousAssetLoader<Texture, TextureLoader.TextureParameter> {

    public TgaLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    IntBuffer pixelBuffer;
    int width;
    int height;

    public static final int BGR = 0x02;
    public static final int _32 = 0x20;
    public static final int _24 = 0x18;

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        return null;
    }

    public int get(ByteBuffer buffer) {
        int i = buffer.get();
        return (i < 0 ? 256 + i : i);
    }

    public int get(ByteBuffer buffer, int idx) {
        int i = buffer.get(idx);
        return (i < 0 ? 256 + i : i);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        ByteBuffer buffer = ByteBuffer.wrap(file.readBytes());

        int width = get(buffer, 12)+(get(buffer, 13)<<8);
        int height = get(buffer, 14)+(get(buffer, 15)<<8);

        int size = width * height;

        int typeCode = get(buffer, 2);
        int pixelSize = get(buffer, 16);
        int descriptor = get(buffer, 17);

        buffer.position(17);

        int n = size;

        pixelBuffer = IntBuffer.allocate(n);

        if(typeCode == BGR) {
            while(n > 0) {
                pixelBuffer.put(getColourValue(buffer, pixelSize));
                n--;
            }
        }else{
            while(n > 0) {
                int nb = get(buffer);
                if((nb & 0x80) == 0) {
                    for (int i = 0; i <= nb; i++) {
                        pixelBuffer.put(getColourValue(buffer, pixelSize));
                    }
                }else{
                    nb &= 0x7F;
                    int v = getColourValue(buffer, pixelSize);
                    for (int i = 0; i <= nb; i++) {
                        pixelBuffer.put(v);
                    }
                }
                n--;
            }
        }
    }

    int getColourValue(ByteBuffer buffer, int pixelSize) {
        int b = get(buffer);
        int g = get(buffer);
        int r = get(buffer);
        int a;
        if(pixelSize == _32) {
            a = get(buffer);
        }else{
            a = 255;
        }
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public Texture loadSync(AssetManager manager, String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        return buildTexture(width, height, pixelBuffer);
    }

}
