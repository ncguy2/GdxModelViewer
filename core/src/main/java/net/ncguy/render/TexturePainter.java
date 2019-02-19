package net.ncguy.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.asset.SpriteCache;
import net.ncguy.ui.TexturePainterFrame;
import net.ncguy.utils.ShaderPreprocessor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static org.lwjgl.opengl.GL11.*;

public class TexturePainter {

    private final Texture texture;
    protected FBO fbo;
    protected SpriteBatch batch;
    protected OrthographicCamera camera;
    protected ShaderProgram drawShader;

    public TexturePainter(Texture texture) {
        this.texture = texture;
        camera = new OrthographicCamera(texture.getWidth(), texture.getHeight());
        batch = new SpriteBatch(64, getDrawShader());
    }

    public FBO getFbo() {
        if (fbo == null) {
            fbo = new FBO(RGBA8888, texture.getWidth(), texture.getHeight(), false, false).Name("Texture painter");
//            fbo.begin();
//            Gdx.gl20.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture.getTextureObjectHandle(), 0);
//            fbo.end();
        }
        return fbo;
    }

    public ShaderProgram getDrawShader() {
        if (drawShader == null) {
            String vert = ShaderPreprocessor.ReadShader(Gdx.files.internal("shaders/brush/brush.vert"));
            String frag = ShaderPreprocessor.ReadShader(Gdx.files.internal("shaders/brush/brush.frag"));
            ShaderProgram shader = new ShaderProgram(vert, frag);
            if(!shader.isCompiled()) {
                System.out.println(shader.getLog());
                return null;
            }
            this.drawShader = shader;

        }
        return drawShader;
    }

    public void requestDraw(TexturePainterFrame.Brush brush, Vector2 point) {
        Gdx.app.postRunnable(() -> draw(brush, point));
    }

    protected void draw(TexturePainterFrame.Brush brush, Vector2 p) {
        getFbo().begin();


//        p.x -= camera.viewportWidth * 0.5f;
//        p.y -= camera.viewportHeight * 0.5f;
//
//        p.y -= brush.size;

//        getFbo().clear(0, 0, 0, 0, false);

        camera.position.set(texture.getWidth() * .5f, texture.getHeight() * .5f, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.getShader().setUniformf("brush.colour", brush.colour);
        batch.getShader().setUniformf("brush.size", brush.size);
        batch.getShader().setUniformf("brush.hardness", brush.hardness);
        batch.getShader().setUniformf("brush.point", p.x, p.y);
        batch.getShader().setUniformf("texSize", texture.getWidth(), texture.getHeight());

        Sprite pixel = SpriteCache.Pixel();

        System.out.println(p);

//        batch.setColor(brush.colour);
//        pixel.setPosition(p.x, p.y);
//        pixel.setSize(brush.size, brush.size);
//        pixel.draw(batch);

        batch.draw(texture, 0, texture.getHeight(), texture.getWidth(), -texture.getHeight());
        batch.end();
        getFbo().end();

        System.out.println(GL20.glGetError());

        ByteBuffer b = getFbo().getPixels(GL_RGBA, GL_UNSIGNED_BYTE);
        blit(b, texture);
    }

    private void blit(ByteBuffer b, Texture target) {
        while(GL20.glGetError() != 0);


        target.bind(0);
        GL20.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBA8, target.getWidth(), target.getHeight(), 0, GL_RGBA, GL11.GL_UNSIGNED_BYTE, b);

        System.out.println(GL20.glGetError());
        Gdx.gl.glActiveTexture(com.badlogic.gdx.graphics.GL20.GL_TEXTURE0);
    }


}
