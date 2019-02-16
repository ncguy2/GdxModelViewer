package net.ncguy.tga;

import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;

import static net.ncguy.tga.DDSLoader.*;

public class DDSHeader {

    public int width;
    public int height;
    public int depth;
    public int flags;
    public int pitchOrSize;
    public int mipMapCount;
    public int caps1;
    public int caps2;
    public boolean directx10;
    public boolean compressed;
    public boolean texture3D;
    public boolean grayscaleOrAlpha;
    public boolean normal;
    public int bpp;
    public Format pixelFormat;
    public int[] sizes;
    public int redMask, greenMask, blueMask, alphaMask;

    public DDSHeader(ByteBuffer buffer) {
        if(buffer.getInt() != 0x20534444 || buffer.getInt() != 124) {
            throw new IOError(new IOException("Not a DDS file"));
        }

        flags = buffer.getInt();

        if(!is(flags, DDSD_MANDATORY) && !is(flags, DDSD_MANDATORY_DX10)) {
            throw new IOError(new IOException("Mandatory flags missing"));
        }

        height = buffer.getInt();
        width = buffer.getInt();
        pitchOrSize = buffer.getInt();
        depth = buffer.getInt();
        mipMapCount = buffer.getInt();
        buffer.position(buffer.position() + 44);
        directx10 = false;
        readPixelFormat(buffer);
        caps1 = buffer.getInt();
        caps2 = buffer.getInt();
        buffer.position(buffer.position() + 12);
        texture3D = false;

        if(!directx10) {
            if(!is(caps1, DDSCAPS_TEXTURE)) {
                System.out.println("Texture is missing the DDSCAPS_TEXTURE flag");
            }

            if(depth <= 0) {
                depth = 1;
            }

            if(is(caps2, DDSCAPS2_CUBEMAP)) {
                depth = 6;
            }

            if(is(caps2, DDSCAPS2_VOLUME)) {
                texture3D = true;
            }
        }

        int expectedMipmaps = (int) (1 + Math.ceil(Math.log(Math.max(height, width)) / LOG2));

        if(is(caps1, DDSCAPS_MIPMAP)) {
            if(!is(flags, DDSD_MIPMAPCOUNT)) {
                mipMapCount = expectedMipmaps;
            }else if(mipMapCount != expectedMipmaps) {
                System.out.printf("Got %d mipmaps, expected %d%n", mipMapCount, expectedMipmaps);
            }
        }else{
            mipMapCount = 1;
        }

        if(directx10) {
            loadDX10Header(buffer);
        }

        loadSizes(buffer);
    }

    private void loadSizes(ByteBuffer buffer) {

    }

    private void loadDX10Header(ByteBuffer buffer) {

    }

    private void readPixelFormat(ByteBuffer buffer) {
        int pfSize = buffer.getInt();
        if(pfSize != 32) {
            throw new IOError(new IOException("Pixel format size is " + pfSize + ", not 32"));
        }

        int pfFlags = buffer.getInt();
        normal = is(pfFlags, DDPF_NORMAL);

        if(is(pfFlags, DDPF_FOURCC)) {
            compressed = true;
            int fourcc = buffer.getInt();
            int swizzle = buffer.getInt();
            buffer.position(buffer.position() + 16);
            switch (fourcc) {
                case PF_DXT1:
                    bpp = 4;
                    if(is(pfFlags, DDPF_ALPHAPIXELS)) {
                    }
            }
        }
    }

    private static boolean is(int flags, int mask) {
        return (flags & mask) == mask;
    }



}
