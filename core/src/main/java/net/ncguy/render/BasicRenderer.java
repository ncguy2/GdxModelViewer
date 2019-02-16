package net.ncguy.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;

public abstract class BasicRenderer {

    protected int width;
    protected int height;

    public abstract void init();
    public abstract void render(RenderableProvider provider);
    public abstract void dispose();
    public abstract void doResize(int width, int height);

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        this.doResize(width, height);
    }

    public abstract Camera camera();

}