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

    public abstract void setActiveAttachment(int index);
    public abstract Attachment[] getAttachments();

    public static class Attachment {
        public int index;
        public String displayName;

        public Attachment(int index, String displayName) {
            this.index = index;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

}