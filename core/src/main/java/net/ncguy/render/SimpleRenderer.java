package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.shader.GridShader;

public class SimpleRenderer extends BasicRenderer {

    private ModelBatch mBatch;
    private Environment environment;
    private PerspectiveCamera pCamera;
    private Model floorModel;
    private ModelInstance floorInstance;
    private GridShader gridShader;

    @Override
    public void init() {
        mBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(.8f, .8f, .8f, -1f, -.8f, -.2f));

        pCamera = new PerspectiveCamera(67, 1, 1);
        pCamera.position.set(10, 10, 10).nor().scl(10);
        pCamera.lookAt(Vector3.Zero);
        pCamera.near = 0.1f;
        pCamera.far = 1.07374182E9f;

        Material mtl = new Material();

        mtl.set(ColorAttribute.createDiffuse(Color.GREEN));

        floorModel = new ModelBuilder().createRect(
                -1f, 0f, -1f,
                1f, 0f, -1f,
                1f, 0f, 1f,
                -1f, 0f, 1f,
                0f, 1f, 0f,
                mtl, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        floorInstance = new ModelInstance(floorModel);

        reGridShader();
    }

    public void reGridShader() {
        if(gridShader != null) {
            gridShader.dispose();
            gridShader = null;
        }
        gridShader = new GridShader();
        gridShader.init();
    }

    @Override
    public void render(RenderableProvider provider) {
        floorInstance.transform.setToRotation(Vector3.Z, 180);
        floorInstance.transform.scale(1000000, 1, 1000000);
        floorInstance.transform.translate(0, 1, 0);

        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            reGridShader();
        }


        mBatch.begin(pCamera);
        mBatch.render(provider, environment);

        mBatch.render(floorInstance, gridShader);

        mBatch.end();
    }

    @Override
    public void dispose() {
        mBatch.dispose();
    }

    @Override
    public void doResize(int width, int height) {
        pCamera.viewportWidth = width;
        pCamera.viewportHeight = height;
        pCamera.update();
    }

    @Override
    public Camera camera() {
        return pCamera;
    }

    @Override
    public void setActiveAttachment(int index) {

    }

    @Override
    public Attachment[] getAttachments() {
        return new Attachment[] {
                new Attachment(0, "Output")
        };
    }

}
