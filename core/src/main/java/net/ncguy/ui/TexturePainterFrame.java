package net.ncguy.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.render.TexturePainter;

public class TexturePainterFrame extends VisTable {

    protected VisTable controls;
    protected VisImage canvas;
    protected Actor canvasPlaceholder;
    protected Texture texture;
    protected Brush activeBrush;
    protected TexturePainter painter;

    private float width;
    private float height;
    private float widthHeightRatio;
    private float heightWidthRatio;

    public TexturePainterFrame(Texture texture) {
        canvas = new VisImage(texture);
        controls = new VisTable();

        width = texture.getWidth();
        height = texture.getHeight();

        widthHeightRatio = width / height;
        heightWidthRatio = height / width;

        canvasPlaceholder = new Actor() {
            @Override
            protected void sizeChanged() {
                super.sizeChanged();
                float tableWidth = canvasPlaceholder.getWidth();
                float tableHeight = canvasPlaceholder.getHeight();

                if(tableWidth > tableHeight) {
                    canvas.setWidth(tableHeight * heightWidthRatio);
                    canvas.setHeight(tableHeight);
                }else{
                    canvas.setWidth(tableWidth);
                    canvas.setHeight(tableWidth * widthHeightRatio);
                }

                canvas.setX((tableWidth - canvas.getWidth()) * 0.5f);
                canvas.setY((tableHeight - canvas.getHeight()) * 0.5f);

            }
        };

        painter = new TexturePainter(texture);

        add(controls).growX().row();
        add(canvasPlaceholder).grow().row();

        addActor(canvas);

        activeBrush = new Brush();
        activeBrush.colour = new Color(1, 0, 0, 1);
        activeBrush.size = 32;
        activeBrush.hardness = 0.5f;

        canvas.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                canvas.setDebug(true);
                Vector2 pos = new Vector2(x, Gdx.graphics.getHeight() - y);
                pos.x /= canvas.getWidth();
                pos.y /= canvas.getHeight();

                pos.x *= texture.getWidth();
                pos.y *= texture.getHeight();

                painter.requestDraw(activeBrush, pos);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Vector2 pos = new Vector2(x, Gdx.graphics.getHeight() - y);
                pos.x /= canvas.getWidth();
                pos.y /= canvas.getHeight();

                pos.x *= texture.getWidth();
                pos.y *= texture.getHeight();
                painter.requestDraw(activeBrush, pos);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                Vector2 pos = new Vector2(x, Gdx.graphics.getHeight() - y);
                pos.x /= canvas.getWidth();
                pos.y /= canvas.getHeight();

                pos.x *= texture.getWidth();
                pos.y *= texture.getHeight();
                painter.requestDraw(activeBrush, pos);
            }
        });
    }

    public static class Brush {
        public Color colour;
        public float size;
        public float hardness;

    }

}
