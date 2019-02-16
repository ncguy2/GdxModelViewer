package net.ncguy.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.widget.VisImage;
import net.ncguy.render.FBO;
import net.ncguy.shader.SimpleShader;

public abstract class Viewport3D extends Viewport {

    protected ModelBatch ssBatch;
    protected Model coordAxisModel;
    protected ModelInstance coordAxis;
    protected Camera ssCamera;
    protected Shader ssShader;
    protected FBO ssFbo;
    protected VisImage ssImage;
    protected boolean drawAxis;
    protected Environment env;
    protected final Vector2 ssOffset = new Vector2(0, 128);

    public Viewport3D(FBO.Builder fboBuilder, boolean autoAttachListeners) {
        super(fboBuilder, autoAttachListeners);
    }

    public Viewport3D(FBO fbo, boolean autoAttachListeners) {
        super(fbo, autoAttachListeners);
    }

    @Override
    public void DoInit() {
        env = new Environment();

        ssFbo = new FBO(Pixmap.Format.RGBA8888, 128, 128, true).Name("SS FBO");
        ssCamera = new PerspectiveCamera(67, 128, 128);
        ssImage = new VisImage();
        ssBatch = new ModelBatch();

        long attrs = 0;
        attrs |= VertexAttributes.Usage.Position;
        attrs |= VertexAttributes.Usage.Normal;
        attrs |= VertexAttributes.Usage.TextureCoordinates;
        attrs |= VertexAttributes.Usage.ColorUnpacked;

        coordAxisModel = new ModelBuilder().createXYZCoordinates(0.5f, 0.25f, .3f, 5, GL20.GL_TRIANGLES, new Material(), attrs);
        coordAxis = new ModelInstance(coordAxisModel);

        ssShader = new SimpleShader();
        ssShader.init();
    }

    public void setDrawAxis(boolean drawAxis) {
        this.drawAxis = drawAxis;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(!drawAxis || ssFbo == null) return;
        ssImage.setDrawable(ssFbo.getColorBufferTexture());
        ssImage.setBounds(getX() + ssOffset.x, getY() + ssOffset.y, 128, -128);
    }

    @Override
    public void draw(Batch batch, float a) {
        super.draw(batch, a);
        if(drawAxis && ssImage != null)
            ssImage.draw(batch, a * getColor().a);
    }

    @Override
    public void PreRender() {
        if(drawAxis) {
            ssCamera.update();

            ssFbo.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            coordAxis.transform.idt();
            ssCamera.position.set(mainCamera.direction.cpy()
                    .nor()
                    .scl(-1.5f));
            ssCamera.lookAt(0, 0, 0);
            ssCamera.up.set(0, 1, 0);
            ssCamera.update();

            ssBatch.begin(ssCamera);
            ssBatch.render(coordAxis, env, ssShader);
            ssBatch.end();

            ssFbo.end();
        }
    }

    @Override
    public Camera MakeCamera() {
        return new PerspectiveCamera(67, getWidth(), getHeight());
    }

    @Override
    public void ResizeCamera(float width, float height) {
        mainCamera.viewportWidth = width;
        mainCamera.viewportHeight = height;
    }

    @Override
    public void dispose() {
        if(ssFbo != null) {
            ssFbo.dispose();
            ssFbo = null;
        }
        if(ssBatch != null) {
            ssBatch.dispose();
            ssBatch = null;
        }
        if(coordAxisModel != null) {
            coordAxisModel.dispose();
            coordAxisModel = null;
        }
        if(ssShader != null) {
            ssShader.dispose();
            ssShader = null;
        }
        super.dispose();
    }
}