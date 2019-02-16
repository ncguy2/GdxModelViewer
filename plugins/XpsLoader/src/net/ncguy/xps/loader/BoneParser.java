package net.ncguy.xps.loader;

import com.badlogic.gdx.math.Vector3;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class BoneParser {

    public static Map<String, BonePose> parse(String data) {
        Map<String, BonePose> bones = new HashMap<>();
        String[] poseList = data.split("[\r\n]");
        for (String bonePose : poseList) {
            if(bonePose.trim().isEmpty()) {
               continue;
            }

            BonePose bone = new BonePose();
            String[] pose = bonePose.split(":");

            bone.name = pose[0];

            String[] dataList = fillArray(String.class, pose[1].split(" "), 9, "1");
            bone.rotDelta = new Vector3(
                    asFloat(dataList[0]),
                    asFloat(dataList[1]),
                    asFloat(dataList[2])
            );
            bone.traDelta = new Vector3(
                    asFloat(dataList[3]),
                    asFloat(dataList[4]),
                    asFloat(dataList[5])
            );
            bone.sclDelta = new Vector3(
                    asFloat(dataList[6]),
                    asFloat(dataList[7]),
                    asFloat(dataList[8])
            );

            bones.put(bone.name, bone);
        }
        return bones;
    }

    static float asFloat(String str) {
        return Float.parseFloat(str);
    }

    static <T> T[] fillArray(Class<T> type, T[] array, int minLen, T value) {
        if(array.length >= minLen) {
            return array;
        }

        @SuppressWarnings("unchecked")
        T[] arr = (T[]) Array.newInstance(type, minLen);

        System.arraycopy(array, 0, arr, 0, array.length);
        for(int i = array.length; i < minLen; i++) {
            arr[i] = value;
        }

        return arr;
    }

    public static class BonePose {
        public String name;
        public Vector3 traDelta;
        public Vector3 rotDelta;
        public Vector3 sclDelta;
    }

    public static class Bone {
        public int boneId;
        public String boneName;
        public Vector3 coords;
        public int parentId;
    }
}
