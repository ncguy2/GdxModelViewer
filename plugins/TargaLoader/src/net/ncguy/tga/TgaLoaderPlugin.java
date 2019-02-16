package net.ncguy.tga;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import net.ncguy.plugin.api.AssetLoaderDefinition;
import net.ncguy.plugin.api.LoaderPlugin;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

public class TgaLoaderPlugin implements LoaderPlugin {

    public static Texture buildTexture(int width, int height, IntBuffer pixelBuffer) {
        Pixmap map = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        int idx = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map.drawPixel(x, y, pixelBuffer.get(idx++));
            }
        }

        Texture tex = new Texture(map);
        map.dispose();
        return tex;
    }

    @Override
    public String name() {
        return "TGA Loader";
    }

    @Override
    public int numDefinitions() {
        return 1;
    }

    @Override
    public List<AssetLoaderDefinition<?>> definitions(FileHandleResolver resolver) {
        return Arrays.asList(
                new AssetLoaderDefinition("Targa Loader", Texture.class, new TgaLoader(resolver)).addExtensions(".tga"),
                new AssetLoaderDefinition("DDS Loader", Texture.class, new DDSLoader2(resolver)).addExtensions(".dds")
        );
    }
}
