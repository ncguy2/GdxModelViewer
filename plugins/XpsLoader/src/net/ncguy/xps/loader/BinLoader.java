package net.ncguy.xps.loader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.xps.utils.BufferUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.ncguy.xps.utils.BufferUtils.getString;

public class BinLoader {

    public static final int MAGIC_NUMBER = 323232;
    public static final int ROUND_MULTIPLE = 4;
    private final InputStream stream;

    public ByteBuffer buffer;

    public BinLoader(InputStream stream) {
        this.stream = stream;
    }

    public XpsData load() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String contents = reader.lines().collect(Collectors.joining("\n"));
        buffer = ByteBuffer.wrap(contents.getBytes());
        XpsHeader header = findHeader();
        List<BoneParser.Bone> bones = readBones();
        boolean hasBones = !bones.isEmpty();
        List<Mesh> meshes = readMeshes(header, hasBones);

        return new XpsData(header, bones, meshes);
    }

    public List<Mesh> readMeshes(XpsHeader header, boolean hasBones) {
        List<Mesh> meshes = new ArrayList<>();

        int meshCount = buffer.getInt();
        boolean hasHeader = header != null;
        boolean hasTangent = false;

        if(hasHeader) {
            hasTangent = hasTangentHeader(header);
        }

        for (int meshId = 0; meshId < meshCount; meshId++) {
            Mesh mesh = new Mesh();
            mesh.name = BufferUtils.getString(buffer);
            if(mesh.name.isEmpty()) {
                mesh.name = "unnamed";
            }
            int uvLayerCount = buffer.getInt();
            // Textures
            mesh.textures = new ArrayList<>();
            int textureCount = buffer.getInt();
            for (int texId = 0; texId < textureCount; texId++) {
                Texture tex = new Texture();
                tex.id = texId;
                tex.file = getString(buffer);
                tex.layerId = buffer.getInt();
                mesh.textures.add(tex);
            }

            // Vertices
            mesh.vertices = new ArrayList<>();
            int vertCount = buffer.getInt();
            for (int vertId = 0; vertId < vertCount; vertId++) {
                Vertex vert = new Vertex();
                vert.coord = getVec3();
                vert.normal = getVec3();
                vert.vertexColour = getColour();

                vert.uvs = new ArrayList<>();
                for (int uvLayerId = 0; uvLayerId < uvLayerCount; uvLayerId++) {
                    vert.uvs.add(getVec2());
                    if(!hasHeader || hasTangent) {
                        get4Float();
                    }
                }

                vert.boneWeights = new ArrayList<>();

                if(hasBones) {
                    int[] boneIdx = new int[] {
                            buffer.getShort(),
                            buffer.getShort(),
                            buffer.getShort(),
                            buffer.getShort()
                    };
                    float[] boneWeight = new float[] {
                            buffer.getFloat(),
                            buffer.getFloat(),
                            buffer.getFloat(),
                            buffer.getFloat()
                    };

                    for (int i = 0; i < boneIdx.length; i++) {
                        BoneWeight w = new BoneWeight();
                        w.boneIdx = boneIdx[i];
                        w.boneWeight = boneWeight[i];
                        vert.boneWeights.add(w);
                    }
                }

                mesh.vertices.add(vert);
            }

            // Faces
            mesh.faces = new ArrayList<>();
            int triCount = buffer.getInt();
            for (int i = 0; i < triCount; i++) {
                Face face = new Face();
                face.v1 = buffer.getInt();
                face.v2 = buffer.getInt();
                face.v3 = buffer.getInt();
                mesh.faces.add(face);
            }

            meshes.add(mesh);
        }

        return meshes;
    }

    private boolean hasTangentHeader(XpsHeader header) {
        return header.versionMajor <= 1 && header.versionMinor <= 12;
    }

    public XpsHeader findHeader() {
        int num = buffer.getInt(0);
        buffer.position(0);

        if(num == MAGIC_NUMBER) {
            System.out.println("Header found");
            return readHeader();
        }

        return null;
    }

    private List<BoneParser.Bone> readBones() {
        List<BoneParser.Bone> bones = new ArrayList<>();

        int boneCount = buffer.getInt();
        for (int i = 0; i < boneCount; i++) {
            BoneParser.Bone bone = new BoneParser.Bone();
            bone.boneId = i;
            bone.boneName = getString(buffer);
            bone.parentId = buffer.getShort();
            bone.coords = getVec3();
            bones.add(bone);
        }

        return bones;
    }

    private XpsHeader readHeader() {
        XpsHeader header = new XpsHeader();

        // Magic number
        header.magicNumber = buffer.getInt();
        // Xps version
        header.versionMajor = buffer.getShort();
        header.versionMinor = buffer.getShort();
        // XnaAral name
        header.xnaAral = getString(buffer);
        // Settings lenght
        header.settingsLen = buffer.getInt();
        // Machine name
        header.machine = getString(buffer);
        // Username
        header.user = getString(buffer);
        // Files
        header.files = getString(buffer);

        if(header.versionMajor <= 1 && header.versionMinor <= 12) {
            buffer.position(buffer.position() + (header.settingsLen * 4));
        }else{
            int valuesRead = 0;
            int hash = buffer.getInt();
            valuesRead += 4;
            int items = buffer.getInt();
            valuesRead += 4;

            for (int i = 0; i < items; i++) {
                int optType = buffer.getInt();
                valuesRead += 4;
                int optCount = buffer.getInt();
                valuesRead += 4;
                int optInfo = buffer.getInt();
                valuesRead += 4;

                if(optType == 255) {
                    readNone(optCount);
                    valuesRead += optCount * 2;
                }else if(optType == 2) {
                    readFlags(optCount);
                    valuesRead += optCount * 2 * 4;
                }else if(optType == 1) {
                    header.pose = readDefaultPose(optCount, optInfo);
                    valuesRead += roundToMultiple(optCount, ROUND_MULTIPLE);
                }else{
                    int loopStart = valuesRead / 4;
                    int loopFinish = header.settingsLen;
                    for(int j = loopStart; j < loopFinish; j++) {
                        buffer.getInt();
                    }
                }
            }

        }


        return header;
    }

    private Vector2 getVec2() {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        return new Vector2(x, y);
    }

    private Vector3 getVec3() {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        float z = buffer.getFloat();
        return new Vector3(x, y, z);
    }

    private float[] get4Float() {
        return new float[] {
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat(),
                buffer.getFloat()
        };
    }

    private Color getColour() {
        float r = ((int) buffer.get() / 255.f);
        float g = ((int) buffer.get() / 255.f);
        float b = ((int) buffer.get() / 255.f);
        float a = ((int) buffer.get() / 255.f);
        return new Color(r, g, b, a);
    }

    private void readNone(int amt) {
        for (int i = 0; i < amt; i++) {
            buffer.getInt();
        }
    }

    private void readFlags(int amt) {
        for (int i = 0; i < amt * 2; i++) {
            buffer.getInt();
        }
    }

    private Map<String, BoneParser.BonePose> readDefaultPose(int len, int bones) {
        List<Byte> poseBytes = new ArrayList<>();
        if (len != 0) {
            for (int i = 0; i < bones; i++) {
                readLine(poseBytes);
            }
        }

        int poseLength = roundToMultiple(len, ROUND_MULTIPLE);
        int emptyBytes = poseLength - len;
        for (int i = 0; i < emptyBytes; i++) {
            buffer.get();
        }

        byte[] bytes = new byte[poseBytes.size()];
        for (int i = 0; i < poseBytes.size(); i++) {
            bytes[i] = poseBytes.get(i);
        }
        String poseString = new String(bytes);
        return BoneParser.parse(poseString);
    }

    private void readLine(List<Byte> target) {
        while(buffer.hasRemaining()) {
            byte b = buffer.get();
            char c = (char) b;
            if(c == '\r' || c == '\n' || c == '\u0000') {
                return;
            }
            target.add(b);
        }
    }

    private int roundToMultiple(int amt, int multiple) {
        int remainder = amt % multiple;
        if (remainder == 0) {
            return amt;
        }
        return amt + multiple - remainder;
    }

    public static class Vertex {
        public int id;
        public Vector3 coord;
        public Vector3 normal;
        public Color vertexColour;
        public List<Vector2> uvs;
        public List<BoneWeight> boneWeights;
    }

    public static class BoneWeight {
        public int boneIdx;
        public float boneWeight;
    }

    public static class Texture {
        public int id;
        public String file;
        public int layerId;
    }

    public static class Face {
        public int v1;
        public int v2;
        public int v3;
    }

    public static class Mesh {
        public String name;
        public List<Texture> textures;
        public List<Vertex> vertices;
        public List<Face> faces;
        public int uvLayerCount;
    }

}
