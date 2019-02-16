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

public class DDSLoader extends AsynchronousAssetLoader<Texture, TextureLoader.TextureParameter> {

    public static final boolean forceRGBA = false;
    public static final int DDSD_MANDATORY = 0x1007;
    public static final int DDSD_MANDATORY_DX10 = 0x6;
    public static final int DDSD_MIPMAPCOUNT = 0x20000;
    public static final int DDSD_LINEARSIZE = 0x80000;
    public static final int DDSD_DEPTH = 0x800000;
    public static final int DDPF_ALPHAPIXELS = 0x1;
    public static final int DDPF_FOURCC = 0x4;
    public static final int DDPF_RGB = 0x40;
    // used by compressonator to mark grayscale images, red channel mask is used for data and bitcount is 8
    public static final int DDPF_GRAYSCALE = 0x20000;
    // used by compressonator to mark alpha images, alpha channel mask is used for data and bitcount is 8
    public static final int DDPF_ALPHA = 0x2;
    // used by NVTextureTools to mark normal images.
    public static final int DDPF_NORMAL = 0x80000000;
    public static final int SWIZZLE_xGxR = 0x78477852;
    public static final int DDSCAPS_COMPLEX = 0x8;
    public static final int DDSCAPS_TEXTURE = 0x1000;
    public static final int DDSCAPS_MIPMAP = 0x400000;
    public static final int DDSCAPS2_CUBEMAP = 0x200;
    public static final int DDSCAPS2_VOLUME = 0x200000;
    public static final int PF_DXT1 = 0x31545844;
    public static final int PF_DXT3 = 0x33545844;
    public static final int PF_DXT5 = 0x35545844;
    public static final int PF_ATI1 = 0x31495441;
    public static final int PF_ATI2 = 0x32495441; // 0x41544932;
    public static final int PF_DX10 = 0x30315844; // a DX10 format
    public static final int DX10DIM_BUFFER = 0x1,
            DX10DIM_TEXTURE1D = 0x2,
            DX10DIM_TEXTURE2D = 0x3,
            DX10DIM_TEXTURE3D = 0x4;
    public static final int DX10MISC_GENERATE_MIPS = 0x1,
            DX10MISC_TEXTURECUBE = 0x4;
    public static final double LOG2 = Math.log(2);
    DDSHeader header;

    public DDSLoader(FileHandleResolver resolver) {
        super(resolver);
    }


    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        ByteBuffer buffer = ByteBuffer.wrap(file.readBytes());
        buffer.position(0);
        header = new DDSHeader(buffer);
    }

    @Override
    public Texture loadSync(AssetManager manager, String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        return null;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TextureLoader.TextureParameter parameter) {
        return null;
    }
}
