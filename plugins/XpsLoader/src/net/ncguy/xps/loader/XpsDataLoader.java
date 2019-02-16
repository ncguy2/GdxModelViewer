package net.ncguy.xps.loader;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class XpsDataLoader extends ModelLoader<ModelLoader.ModelParameters> {

    public XpsDataLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public ModelData loadModelData(FileHandle fileHandle, ModelParameters parameters) {
        try(InputStream stream = Files.newInputStream(fileHandle.file().toPath())) {
            BinLoader binLoader = new BinLoader(stream);
            XpsData data = binLoader.load();

            ModelData modelData = new ModelData();

            for (BinLoader.Mesh mesh : data.meshes) {
                modelData.materials.add(convertToMaterial(mesh));
            }

            for (BinLoader.Mesh mesh : data.meshes) {
                ModelMesh value = convertMesh(mesh);
                modelData.meshes.add(value);

                ModelNode node = new ModelNode();
                node.meshId = value.id;
                node.id = value.id + "_node";
                modelData.nodes.add(node);
            }
//
//            Map<BoneParser.Bone, ModelNode> nodeMap = new HashMap<>();
//            for (BoneParser.Bone bone : data.bones) {
//                nodeMap.put(bone, convertBone(bone));
//            }
//
//            nodeMap.keySet().forEach(b -> link(nodeMap, b));

            modelData.id = UUID.randomUUID().toString();

            return modelData;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void link(Map<BoneParser.Bone, ModelNode> map, BoneParser.Bone bone) {
        map.values()
                .stream()
                .filter(n -> n.id.equals(String.valueOf(bone.parentId)))
                .findFirst()
                .ifPresent(n -> {
                    ModelNode[] children = n.children;
                    ModelNode[] tgt = new ModelNode[children.length + 1];
                    System.arraycopy(children, 0, tgt, 0, children.length);
                    tgt[children.length] = map.get(bone);
                    n.children = tgt;
                });
    }

    private ModelNode convertBone(BoneParser.Bone bone) {
        ModelNode node = new ModelNode();

        node.id = String.valueOf(bone.boneId);
        node.meshId = bone.boneName;
        node.translation = bone.coords;

        return node;
    }

    private ModelMaterial convertToMaterial(BinLoader.Mesh mesh) {
        ModelMaterial mtl = new ModelMaterial();
        mtl.type = ModelMaterial.MaterialType.Lambert;

        mtl.textures = new Array<>();
        for (BinLoader.Texture texture : mesh.textures) {
            ModelTexture tex = new ModelTexture();
            tex.usage = ModelTexture.USAGE_DIFFUSE;
            tex.fileName = texture.file;
            tex.id = String.valueOf(texture.id);

            mtl.textures.add(tex);
        }

        return mtl;
    }

    private ModelMesh convertMesh(BinLoader.Mesh mesh) {
        ModelMesh modelMesh = new ModelMesh();

        // Attributes
        modelMesh.attributes = new VertexAttribute[]{
                VertexAttribute.Position(),
                VertexAttribute.Normal(),
                VertexAttribute.ColorUnpacked(),
                VertexAttribute.TexCoords(0),
                VertexAttribute.BoneWeight(0),
                VertexAttribute.BoneWeight(1),
                VertexAttribute.BoneWeight(2),
                VertexAttribute.BoneWeight(3)
        };

        int stride = 0;
        for (VertexAttribute attr : modelMesh.attributes) {
            stride += attr.numComponents;
        }

        // Vertices
        modelMesh.vertices = new float[mesh.vertices.size() * stride];
        int ptr = 0;
        for (BinLoader.Vertex vertex : mesh.vertices) {
            modelMesh.vertices[ptr++] = vertex.coord.x;
            modelMesh.vertices[ptr++] = vertex.coord.y;
            modelMesh.vertices[ptr++] = vertex.coord.z;

            modelMesh.vertices[ptr++] = vertex.normal.x;
            modelMesh.vertices[ptr++] = vertex.normal.y;
            modelMesh.vertices[ptr++] = vertex.normal.z;

            modelMesh.vertices[ptr++] = vertex.vertexColour.r;
            modelMesh.vertices[ptr++] = vertex.vertexColour.g;
            modelMesh.vertices[ptr++] = vertex.vertexColour.b;
            modelMesh.vertices[ptr++] = vertex.vertexColour.a;

            Vector2 uv = vertex.uvs.get(0);
            modelMesh.vertices[ptr++] = uv.x;
            modelMesh.vertices[ptr++] = uv.y;

            List<BinLoader.BoneWeight> boneWeights = vertex.boneWeights;
            int i = 0;
            for (i = 0; i < boneWeights.size(); i++) {
                BinLoader.BoneWeight boneWeight = boneWeights.get(i);
                modelMesh.vertices[ptr++] = boneWeight.boneIdx;
                modelMesh.vertices[ptr++] = boneWeight.boneWeight;
            }
            for(i = i; i < 4; i++) {
                modelMesh.vertices[ptr++] = 0f;
                modelMesh.vertices[ptr++] = 0f;
            }
        }

        // Parts
        modelMesh.parts = new ModelMeshPart[1];
        ModelMeshPart part = new ModelMeshPart();
        part.primitiveType = GL20.GL_TRIANGLES;
        part.id = mesh.name;

        part.indices = new short[mesh.faces.size() * 3];
        ptr = 0;
        for (BinLoader.Face face : mesh.faces) {
            part.indices[ptr++] = (short) face.v1;
            part.indices[ptr++] = (short) face.v2;
            part.indices[ptr++] = (short) face.v3;
        }

        modelMesh.parts[0] = part;


        return modelMesh;
    }

}
