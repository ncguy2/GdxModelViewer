package net.ncguy.xps.loader;

import java.util.List;

public class XpsData {

    public final XpsHeader header;
    public final List<BoneParser.Bone> bones;
    public final List<BinLoader.Mesh> meshes;

    public XpsData(XpsHeader header, List<BoneParser.Bone> bones, List<BinLoader.Mesh> meshes) {
        this.header = header;
        this.bones = bones;
        this.meshes = meshes;
    }
}
