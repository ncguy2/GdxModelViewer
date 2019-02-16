package net.ncguy.asset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class SpriteCache {

    static Texture black;
    public static Texture Black() {
        if (black == null) {
            Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            map.setColor(0, 0, 0, 0);
            map.drawPixel(0, 0);
            black = new Texture(map);
            map.dispose();
        }
        return black;
    }

    static Sprite pixel;
    public static Sprite Pixel() {
        if (pixel == null) {
            Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            map.setColor(Color.WHITE);
            map.drawPixel(0, 0);
            pixel = new Sprite(new Texture(map));
            map.dispose();
        }
        return pixel;
    }

    static Sprite defTex;
    public static Sprite Default() {
        if (defTex == null) {
            Pixmap map = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
            map.setColor(Color.BLACK);
            map.drawRectangle(0, 0, 2, 2);
            map.setColor(Color.MAGENTA);
            map.drawPixel(0,0);
            map.drawPixel(1,1);
            defTex = new Sprite(new Texture(map));
            map.dispose();
        }
        return defTex;
    }

    static ModelInstance box;
    static Model boxModel;
    public static ModelInstance Box() {
        return Box(null);
    }
    public static ModelInstance Box(Color colour, Vector3 halfExtents) {
        ModelInstance inst = Box(colour);
        inst.transform.setToScaling(halfExtents.cpy().scl(2f));
        return inst;
    }
    public static ModelInstance Box(Color colour) {
        if (box == null) {
            Material mtl = new Material();
            mtl.set(ColorAttribute.createDiffuse(Color.CYAN));
            mtl.set(new ColorAttribute(ColorAttribute.Emissive, Color.CYAN));
            box = new ModelInstance(boxModel = new ModelBuilder().createBox(1, 1, 1, mtl, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        }

        SetColour(box, colour);

        return box;
    }

    static ModelInstance sphere;
    static Model sphereModel;
    public static ModelInstance Sphere() {
        return Sphere((Color) null);
    }
    public static ModelInstance Sphere(Color colour, float radius) {
        ModelInstance inst = Sphere(colour);
        inst.transform.setToScaling(radius * 2f, radius * 2f, radius * 2f);
        return inst;
    }
    public static ModelInstance Sphere(Color colour) {
        if (sphere == null) {
            Material mtl = new Material();
            mtl.set(ColorAttribute.createDiffuse(Color.CYAN));
            mtl.set(new ColorAttribute(ColorAttribute.Emissive, Color.CYAN));
            sphere = new ModelInstance(sphereModel = new ModelBuilder().createSphere(1, 1, 1, 64, 64, mtl, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates));
        }

        SetColour(sphere, colour);


        return sphere;
    }

    static ModelInstance cylinder;
    static Model cylinderModel;
    public static ModelInstance Cylinder() {
        return Cylinder(null);
    }
    public static ModelInstance Cylinder(Color colour, Vector3 halfExtents) {
        ModelInstance cyl = Cylinder(colour);
        cyl.transform.setToScaling(halfExtents.cpy().scl(2));
        return cyl;
    }
    public static ModelInstance Cylinder(Color colour) {
        if (cylinder == null) {
            Material mtl = new Material();
            mtl.set(ColorAttribute.createDiffuse(Color.CYAN));
            mtl.set(new ColorAttribute(ColorAttribute.Emissive, Color.CYAN));
            cylinder = new ModelInstance(cylinderModel = new ModelBuilder().createCylinder(1, 1, 1, 64, mtl, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        }

        SetColour(cylinder, colour);


        return cylinder;
    }

    static ModelInstance capsule;
    static Model capsuleModel;
    public static ModelInstance Capsule() {
        return Capsule(null);
    }
    public static ModelInstance Capsule(Color colour) {
        if (capsule == null) {
            Material mtl = new Material();
            mtl.set(ColorAttribute.createDiffuse(Color.CYAN));
            mtl.set(new ColorAttribute(ColorAttribute.Emissive, Color.CYAN));
            capsule = new ModelInstance(capsuleModel = new ModelBuilder().createCapsule(1, 1, 64, mtl, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        }

        SetColour(capsule, colour);

        return capsule;
    }

    static ModelInstance cone;
    static Model coneModel;
    public static ModelInstance Cone() {
        return Cone(null);
    }
    public static ModelInstance Cone(Color colour, float radius, float height) {
        ModelInstance cone = Cone(colour);
        cone.transform.setToScaling(radius * 2f, height, radius * 2f);
        return cone;
    }
    public static ModelInstance Cone(Color colour) {
        if (cone == null) {
            Material mtl = new Material();
            mtl.set(ColorAttribute.createDiffuse(Color.CYAN));
            mtl.set(new ColorAttribute(ColorAttribute.Emissive, Color.CYAN));
            cone = new ModelInstance(coneModel = new ModelBuilder().createCone(1, 1, 1, 64, mtl, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal));
        }

        SetColour(cone, colour);

        return cone;
    }

    static void SetColour(ModelInstance inst, Color colour) {
        if(colour != null) {
            ColorAttribute dif = inst.materials.get(0).get(ColorAttribute.class, ColorAttribute.Diffuse);
            ColorAttribute emi = inst.materials.get(0).get(ColorAttribute.class, ColorAttribute.Emissive);
            dif.color.set(colour);
            emi.color.set(colour);
        }
    }

    public static ModelInstance Line(Vector3 a, Vector3 b, float thickness) {
        return Line(a, b, thickness, null);
    }
    public static ModelInstance Line(Vector3 a, Vector3 b, float thickness, Color colour) {
        ModelInstance inst = Box(colour);

        inst.transform.idt();
        Quaternion q = new Quaternion();

        Vector3 crs = a.cpy().crs(b);
        float dot = a.dot(b);
        float w = (float) (Math.sqrt((a.len2()) * (b.len2())) + dot);
        q.set(crs, w);

        float len = a.dst(b);
        inst.transform.translate(a.cpy().lerp(b, .5f));

        inst.transform.rotate(q);
        inst.transform.scale(thickness, thickness, thickness);

        return inst;
    }

    public static ModelInstance Line(Matrix4 a, Matrix4 b, float thickness) {
        return Line(a, b, thickness, null);
    }
    public static ModelInstance Line(Matrix4 a, Matrix4 b, float thickness, Color colour) {
        Vector3 vecA = new Vector3();
        Vector3 vecB = new Vector3();

        a.getTranslation(vecA);
        b.getTranslation(vecB);

        return Line(vecA, vecB, thickness, colour);
    }

    public static void dispose() {
        if(pixel != null) {
            pixel.getTexture()
                    .dispose();
            pixel = null;
        }

        if (boxModel != null) {
            box = null;
            boxModel.dispose();
            boxModel = null;
        }

        if (sphereModel != null) {
            sphere = null;
            sphereModel.dispose();
            sphereModel = null;
        }

        if (cylinderModel != null) {
            cylinder = null;
            cylinderModel.dispose();
            cylinderModel = null;
        }

        if (capsuleModel != null) {
            capsule = null;
            capsuleModel.dispose();
            capsuleModel = null;
        }

        if (coneModel != null) {
            cone = null;
            coneModel.dispose();
            coneModel = null;
        }
    }

}