package net.ncguy.xps.utils;

import java.nio.ByteBuffer;

public class BufferUtils {

    public static final int LIMIT = 128;

    public static String getString(ByteBuffer buffer) {
        int lengthByte2 = 0;
        int lengthByte1 = buffer.get();
        if(lengthByte1 >= LIMIT) {
            lengthByte2 = buffer.get();
        }

        int length = (lengthByte1 % LIMIT) + (lengthByte2 * LIMIT);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append((char) buffer.get());
        }

        return sb.toString();
    }

}
