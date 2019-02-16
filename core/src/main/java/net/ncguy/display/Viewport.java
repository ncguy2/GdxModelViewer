package net.ncguy.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisImage;
import net.ncguy.input.ViewportInputListener;
import net.ncguy.render.FBO;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class Viewport extends Group implements AutoCloseable, Disposable {

    protected FBO fbo;
    public WeakReference<Viewport> ref;
    public InputProcessor processor;
    public VisImage image;
    protected Camera mainCamera;

    public Viewport(FBO.Builder fboBuilder, boolean autoAttachListeners) {
        this(fboBuilder.Build(), autoAttachListeners);
    }
    public Viewport(FBO fbo, boolean autoAttachListeners) {
        this.fbo = fbo;

        Init();

        if(autoAttachListeners)
            AttachListeners();
    }

    public void render() {
        if (!isVisible() || fbo == null) return;

        PreRender();

        fbo.begin();
        fbo.clear(0, 0 ,0, 1, true);
        DoRender();
        fbo.end();
    }

    @Override
    protected void sizeChanged() {
        Resize(getWidth(), getHeight());
    }

    protected void setDrawable(Texture texture) {
        setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }
    protected void setDrawable(Drawable drawable) {
        image.setDrawable(drawable);
        image.setBounds(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void setParent(Group parent) {
        super.setParent(parent);
        viewports.add(ref = new WeakReference<>(this));
    }

    public void Init() {
        image = new VisImage();
        addActor(image);

        mainCamera = MakeCamera();

        DoInit();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float width = getWidth();
        float height = getHeight();
        image.setBounds(0, 0, width, height);
    }

    @Override
    public void draw(Batch batch, float a) {
        if (fbo != null) {
            TextureRegion reg = new TextureRegion(fbo.getColorBufferTexture());
            reg.flip(false, true);
            setDrawable(new TextureRegionDrawable(reg));
        }
        super.draw(batch, a);
    }

    public void Resize(float width, float height) {

        float w = Math.max(width, 1);
        float h = Math.max(height, 1);

        fbo.Resize(Math.round(w), Math.round(h));
        DoResize(w, h);
        ResizeCamera(w, h);
    }

    public void PreRender() {}

    public abstract Camera MakeCamera();
    public abstract void ResizeCamera(float width, float height);

    public abstract void DoInit();
    public abstract void DoResize(float width, float height);
    public abstract void DoRender();

    protected abstract InputProcessor BuildController(Camera camera);

    public void AttachListeners() {
        setTouchable(Touchable.enabled);
        processor = BuildController(mainCamera);
        addListener(new ViewportInputListener(this));
    }

    @Override
    public void close() throws Exception {
        Gdx.app.postRunnable(this::dispose);
    }

    @Override
    public boolean remove() {
        try {
            close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return super.remove();
    }

    @Override
    public void dispose() {
        if(fbo != null) {
            fbo.dispose();
            fbo = null;
        }
        viewports.remove(ref);
        ref.clear();
        ref = null;
    }

    public void setMainCamera(Camera mainCamera) {
        this.mainCamera = mainCamera;
    }

    public Camera getMainCamera() {
        return mainCamera;
    }

    // Static registry

    public static List<WeakReference<Viewport>> viewports = new ArrayList<>();

    public static void Invoke(Consumer<Viewport> task) {
        if(viewports.isEmpty())
            return;

        viewports.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .forEach(task);
    }

    public static void RenderViewports() {
        Invoke(Viewport::render);

        List<WeakReference<Viewport>> toRemove = viewports.stream()
                .filter(ref -> ref.get() == null)
                .collect(Collectors.toList());

        if(!toRemove.isEmpty())
            viewports.removeAll(toRemove);
    }

}