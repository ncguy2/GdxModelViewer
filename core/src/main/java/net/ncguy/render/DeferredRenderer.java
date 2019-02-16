package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.shader.StandardSkeletalMeshShader;
import net.ncguy.shader.StandardStaticMeshShader;
import org.lwjgl.opengl.GL14;

import static com.badlogic.gdx.graphics.GL20.GL_FLOAT;
import static com.badlogic.gdx.graphics.GL20.GL_RGB;
import static com.badlogic.gdx.graphics.GL30.GL_RGB32F;

public class DeferredRenderer extends BasicRenderer {

    public FBO fbo;
    public int texId = 0;
    public ModelBatch batch;
    public SpriteBatch spriteBatch;
    OrthographicCamera orthographicCamera;
    public PerspectiveCamera camera;
    public Environment environment;

    @Override
    public void init() {
        FBO.Builder builder = new FBO.Builder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        builder.addColorTextureAttachment(GL_RGB32F, GL_RGB, GL_FLOAT); // Position[32f32f32f]
        builder.addBasicColorTextureAttachment(Pixmap.Format.RGB888);   // Normal[888]
        builder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888); // Albedo[888] + Alpha[8]
        builder.addBasicColorTextureAttachment(Pixmap.Format.RGB888);   // Specular[8] + empty channels[88]

        builder.addDepthRenderBuffer(GL14.GL_DEPTH_COMPONENT32);
        fbo = builder.Build();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(.8f, .8f, .8f, -1f, -.8f, -.2f));

        camera = new PerspectiveCamera(67, 1, 1);
        camera.position.set(10, 10, 10);
        camera.lookAt(Vector3.Zero);
        camera.near = 0.1f;
        camera.far = 1024.f;

        batch = new ModelBatch(new DefaultShaderProvider() {
            @Override
            protected Shader createShader(Renderable renderable) {
                if(renderable.bones != null && renderable.bones.length > 0) {
                    return new StandardSkeletalMeshShader(renderable);
                }
                return new StandardStaticMeshShader(renderable);
            }
        });

        spriteBatch = new SpriteBatch();
        orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(RenderableProvider provider) {
        fbo.begin();
        fbo.clear(0, 0, 0, 1, true);
        batch.begin(camera);
        batch.render(provider, environment);
        batch.end();
        fbo.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            texId--;
            if(texId < 0)
                texId += fbo.getAttachmentCount();
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            texId++;
        }

        TextureRegion reg = new TextureRegion(fbo.getTextureWrapped(texId));
        reg.flip(false, false);

        spriteBatch.setProjectionMatrix(orthographicCamera.combined);
        spriteBatch.begin();
        spriteBatch.draw(reg, 0, 0, width, height);
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        if(fbo != null) {
            fbo.dispose();
            fbo = null;
        }
        if(spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
        if(batch != null) {
            batch.dispose();
            batch = null;
        }
    }

    @Override
    public void doResize(int width, int height) {
        fbo.Resize(width, height);

        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        orthographicCamera.setToOrtho(true, width, height);
    }

    @Override
    public Camera camera() {
        return camera;
    }

}
