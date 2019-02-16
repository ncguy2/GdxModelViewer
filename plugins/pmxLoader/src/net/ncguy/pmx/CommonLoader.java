package net.ncguy.pmx;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.*;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import net.ncguy.asset.AssetNote;
import net.ncguy.exceptions.AssetLoadingException;
import net.ncguy.pmx.mmd.Pmx;
import net.ncguy.pmx.mmd.data.Face;
import net.ncguy.pmx.mmd.data.Material;
import net.ncguy.pmx.mmd.data.Vertex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class CommonLoader extends ModelLoader<CommonLoader.PmxParameters> {

    public CommonLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public List<String> splitTextures(String path) {
        List<String> items = new ArrayList<>();

        int endIndex = path.lastIndexOf('/');
        String baseName = path.substring(0, endIndex);
        String substring = path.substring(endIndex + 1);

        if(substring.contains("*")) {
            String[] split = substring.split("\\*");
            for (String s : split) {
                items.add(baseName + "/" + s);
            }
        }else{
            items.add(path);
        }
        return items;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, PmxParameters parameters) {
        Array<AssetDescriptor> dependencies = super.getDependencies(fileName, file, parameters);

        TextureLoader.TextureParameter textureParameter = (parameters != null)
                ? parameters.textureParameter
                : defaultParameters.textureParameter;

        for (int i = 0; i < dependencies.size; i++) {
            AssetDescriptor item = dependencies.get(i);
            if(item == null) {
                continue;
            }

            List<String> strings = splitTextures(item.fileName);
            if(strings.size() != 1) {
                dependencies.removeIndex(i);
                for (String s : strings) {
                    dependencies.add(new AssetDescriptor<>(s, Texture.class, textureParameter));
                }
            }
        }

        for (int i = 0; i < dependencies.size; i++) {
            AssetDescriptor item = dependencies.get(i);
            if(item == null) {
                continue;
            }


            FileHandle resolve = resolve(item.fileName);
            if(resolve == null) {
                dependencies.removeIndex(i);
                continue;
            }
            if (!resolve.exists()) {
                dependencies.removeIndex(i);
            }
            String s = resolve.extension().toLowerCase();
        }

        return dependencies;
    }

    @Override
    public ModelData loadModelData(FileHandle fileHandle, PmxParameters parameters) {
        ModelData modelData = new ModelData();

        modelData.id = fileHandle.name();
        try {
            Pmx read = readFile(fileHandle.file());
            ModelMesh mesh = new ModelMesh();

            modelData.id = read.getHeader().getName();
            String[] textures = read.getTextures();
            Vertex[] vertices = read.getVertices();
            ModelNode modelNode = new ModelNode();

            int stride = 8;

            mesh.attributes = new VertexAttribute[3];
            mesh.attributes[0] = VertexAttribute.Position();
            mesh.attributes[1] = VertexAttribute.Normal();
            mesh.attributes[2] = VertexAttribute.TexCoords(0);

            mesh.vertices = new float[vertices.length * stride];
            int ptr = 0;
            for (Vertex vertex : vertices) {
                mesh.vertices[ptr++] = vertex.getX();
                mesh.vertices[ptr++] = vertex.getY();
                mesh.vertices[ptr++] = vertex.getZ();

                mesh.vertices[ptr++] = vertex.getNx();
                mesh.vertices[ptr++] = vertex.getNy();
                mesh.vertices[ptr++] = vertex.getNz();

                mesh.vertices[ptr++] = vertex.getU();
                mesh.vertices[ptr++] = vertex.getV();
            }

            if(mesh.vertices.length / stride > 65535) {
                throw new AssetLoadingException("Too many vertices (" + (mesh.vertices.length / stride) + " > 65535)", AssetNote.NoteType.Error);
            }

            mesh.id = read.getHeader().getName();


            Face[] faces = read.getFaces();
            Material[] materials = read.getMaterials();
            mesh.parts = new ModelMeshPart[materials.length];

            ptr = 0;

            modelNode.parts = new ModelNodePart[mesh.parts.length];
            for (int i = 0, v = 0; i < materials.length; i++) {
                Material material = materials[i];

                ModelMaterial modelMaterial = new ModelMaterial();

                int texture = material.getTexture();
                modelMaterial.textures = new Array<>();
                modelMaterial.id = material.getName();

                if(texture >= 0) {
                    ModelTexture modelTexture = new ModelTexture();

                    List<String> strings = splitTextures(fileHandle.parent().path() + "/" + textures[texture].replaceAll("\\\\", "/"));

                    for (String string : strings) {
                        modelTexture.fileName = string;

                        String fileName = modelTexture.fileName;
                        if(fileName.endsWith(".spa")) {
                            modelTexture.usage = ModelTexture.USAGE_SPECULAR;
                        }else{
                            modelTexture.usage = ModelTexture.USAGE_DIFFUSE;
                        }

                        modelTexture.id = modelMaterial.id + "_tex" + modelTexture.usage;

                        modelMaterial.textures.add(modelTexture);
                    }
                }

                modelNode.parts[i] = new ModelNodePart();
                modelNode.parts[i].materialId = modelMaterial.id;
                modelMaterial.type = ModelMaterial.MaterialType.Lambert;
                modelMaterial.ambient = new Color(material.getAmbientR(), material.getAmbientG(), material.getAmbientB(), 1);
                modelMaterial.diffuse = new Color(material.getDiffuseR(), material.getDiffuseG(), material.getDiffuseB(), material.getDiffuseA());
                modelMaterial.specular = new Color(material.getSpecularR(), material.getSpecularG(), material.getSpecularB(), 1);

                modelData.materials.add(modelMaterial);
                
                ModelMeshPart part = new ModelMeshPart();
                int idx = 0;

                part.id = material.getName()+"_part";
                part.primitiveType = GL20.GL_TRIANGLES;
                part.indices = new short[material.getBoundFace()];

                for(int j = 0; j < material.getBoundFace() / 3; j++, v++) {
                    Face face = faces[v];
                    part.indices[idx++] = (short) face.getV1();
                    part.indices[idx++] = (short) face.getV2();
                    part.indices[idx++] = (short) face.getV3();
                }

                mesh.parts[i] = part;
            }

            for (int i = 0; i < mesh.parts.length; i++) {
                modelNode.parts[i].meshPartId = mesh.parts[i].id;
            }

            modelNode.id = read.getHeader().getName() + "_root";
            modelNode.meshId = mesh.id;
            modelNode.translation = new Vector3();
            modelNode.rotation = new Quaternion();
            modelNode.scale = new Vector3(1, 1, 1);

            modelData.nodes.add(modelNode);

            modelData.meshes.add(mesh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modelData;
    }



    protected abstract Pmx readFile(File file) throws IOException;

    private int addVertex(ModelMesh mesh, int ptr, Vertex vertex) {
        mesh.vertices[ptr++] = vertex.getX();
        mesh.vertices[ptr++] = vertex.getY();
        mesh.vertices[ptr++] = vertex.getZ();

        mesh.vertices[ptr++] = vertex.getNx();
        mesh.vertices[ptr++] = vertex.getNy();
        mesh.vertices[ptr++] = vertex.getNz();

        mesh.vertices[ptr++] = vertex.getU();
        mesh.vertices[ptr++] = vertex.getV();
        return ptr;
    }

    public static class PmxParameters extends ModelLoader.ModelParameters {
    }



}
