package net.ncguy.fbx;

import net.ncguy.fbx.data.FbxModel;
import net.ncguy.fbx.data.FbxNode;

import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class FbxLoader {

    public static final String MAGIC_HEADER = "Kaydara FBX Binary" + (char)0x20 + (char)0x20 + (char)0x00 + (char)0x1a + (char)0x00;
    public static final byte[] MAGIC_HEADER_BYTES = MAGIC_HEADER.getBytes();

    public static final int BLOCK_SENTINEL_LENGTH = 13;
    public static final byte[] BLOCK_SENTINEL_DATA = makeArr('\0', BLOCK_SENTINEL_LENGTH);

    private static byte[] makeArr(char contents, int amt) {
        return makeArr((byte) contents, amt);
    }
    private static byte[] makeArr(byte contents, int amt) {
        byte[] arr = new byte[amt];
        for (int i = 0; i < amt; i++) {
            arr[i] = contents;
        }
        return arr;
    }

    public static FbxModel read(ByteBuffer buffer) {
        FbxModel model = new FbxModel();
        model.nodes = new ArrayList<>();

        if(!Arrays.equals(read(buffer, MAGIC_HEADER_BYTES.length), MAGIC_HEADER_BYTES)) {
            throw new IOError(new IOException("Invalid header"));
        }

        model.version = buffer.getInt();

        while(true) {
            FbxNode elem = readElem(buffer);
            if(elem == null) {
                break;
            }
            model.nodes.add(elem);
        }

        return model;
    }

    static Function<ByteBuffer, Object> readDataDict(byte dataType) {
        switch(dataType) {
            case 'Y': return ByteBuffer::getShort;
            case 'C': return b -> b.get() != 0;
            case 'I': return ByteBuffer::getInt;
            case 'F': return ByteBuffer::getFloat;
            case 'D': return ByteBuffer::getDouble;
            case 'L': return ByteBuffer::getLong;
            case 'R': return FbxLoader::readUInt; // read_uint, binary data
            case 'S': return FbxLoader::readUInt; // read_uint, string data

            // Array
            case 'f': return b -> unpackArray(b, 'f', 4, false);
            case 'i': return b -> unpackArray(b, 'i', 4, true);
            case 'd': return b -> unpackArray(b, 'd', 8, false);
            case 'l': return b -> unpackArray(b, 'q', 8, true);
            case 'b': return b -> unpackArray(b, 'b', 1, false);
            case 'c': return b -> unpackArray(b, 'B', 1, false);
        }
        throw new IOError(new IOException("Unknown data type: " + (char) dataType));
    }

    private static FbxNode readElem(ByteBuffer buffer) {
        int endOffset = buffer.getInt();
        if(endOffset == 0) {
            return null;
        }

        FbxNode node = new FbxNode();

        int propCount = buffer.getInt();
        int propLength = buffer.getInt();
        node.elemId = readString(buffer);
        node.elemPropsType = new byte[propCount];
        node.elemPropsData = new Object[propCount];
        node.elemSubTree = new ArrayList<>();

        for (int i = 0; i < propCount; i++) {
            byte dataType = buffer.get();
            node.elemPropsData[i] = readDataDict(dataType).apply(buffer);
            node.elemPropsType[i] = dataType;
        }

        if(buffer.position() < endOffset) {
            while(buffer.position() < endOffset - BLOCK_SENTINEL_LENGTH) {
                node.elemSubTree.add(readElem(buffer));
            }

            if(!Arrays.equals(read(buffer, BLOCK_SENTINEL_LENGTH), BLOCK_SENTINEL_DATA)) {
                throw new IOError(new IOException("Failed to read nested block sentinel, expected " + BLOCK_SENTINEL_LENGTH + " null bytes"));
            }
        }

        if(buffer.position() != endOffset) {
            throw new IOError(new IOException("Scope length not reached"));
        }

        return node;
    }

    static Object[] unpackArray(ByteBuffer buffer, char arrayType, int stride, boolean byteSwap) {

        switch(arrayType) {
            case 'f':
                return unpackArray(buffer, Float.class, stride, byteSwap);
            case 'i':
                return unpackArray(buffer, Integer.class, stride, byteSwap);
            case 'd':
                return unpackArray(buffer, Double.class, stride, byteSwap);
            case 'l':
                return unpackArray(buffer, Long.class, stride, byteSwap);
            case 'b':
                return unpackArray(buffer, Boolean.class, stride, byteSwap);
            case 'c':
                return unpackArray(buffer, Byte.class, stride, byteSwap);
        }

        throw new RuntimeException("Unrecognised array type: " + arrayType);
    }
    static <T> T[] unpackArray(ByteBuffer buffer, Class<T> type, int stride, boolean byteSwap) {
        int len = buffer.getInt();
        int encoding = buffer.getInt();
        int compLen = buffer.getInt();

        byte[] data = read(buffer, compLen);

        if(encoding == 0) {
            // pass
        }else if(encoding == 1) {
            Inflater inflater = new Inflater();
            inflater.setInput(data, 0, len);
            byte[] newData = new byte[len];
            try {
                inflater.inflate(newData);
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            data = newData;
            inflater.end();
//            data = zlib.decompress(data);
        }

        assert len * stride == data.length;

        T[] dataArr = (T[]) Array.newInstance(type, len);

        byte[] temp = new byte[stride];
        for (int i = 0, j = 0; i < data.length; i += stride, j++) {
            System.arraycopy(data, i, temp, 0, stride);
            dataArr[j] = fromBytes(type, temp, byteSwap ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        }

        return dataArr;
    }

    static <T> T fromBytes(Class<T> type, byte[] bytes, ByteOrder byteOrder) {
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        wrap.order(byteOrder);
        if (Float.class.equals(type)) {
            return (T) (Float) wrap.getFloat();
        } else if (Integer.class.equals(type)) {
            return (T) (Integer) wrap.getInt();
        } else if (Double.class.equals(type)) {
            return (T) (Double) wrap.getDouble();
        } else if (Long.class.equals(type)) {
            return (T) (Long) wrap.getLong();
        } else if (Boolean.class.equals(type)) {
            return (T) (Boolean) (wrap.getInt() != 0);
        } else if (Byte.class.equals(type)) {
            return (T) (Byte) wrap.get();
        }
        throw new RuntimeException("Unrecognised array type: " + type.getSimpleName());
    }

    static String readString(ByteBuffer buffer) {
        byte b = buffer.get();
        int i = (int) b & 0xFF;
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < i; j++) {
            sb.append((char) buffer.get());
        }

        return sb.toString();
    }

    static byte[] readUByte(ByteBuffer buffer) {
        int i = buffer.get() & 0xFF;
        byte[] b = new byte[i];
        buffer.get(b);
        return b;
    }

    static byte[] readUInt(ByteBuffer buffer) {
        int i = buffer.getInt();
        byte[] b = new byte[i];
        buffer.get(b);
        return b;
    }

    public static byte[] read(ByteBuffer buffer, int amt) {
        byte[] tgt = new byte[amt];
        buffer.get(tgt);
        return tgt;
    }

}
