package net.ncguy.input;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import net.ncguy.display.Viewport;

public class ViewportInputListener extends InputListener {

    private final Viewport viewport;

    public ViewportInputListener(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (viewport.processor != null) {
            event.getStage().setKeyboardFocus(viewport);
            return viewport.processor.touchDown((int) x, (int) y, pointer, button);
        }
        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        if (viewport.processor != null)
            viewport.processor.touchUp((int) x, (int) y, pointer, button);
        super.touchUp(event, x, y, pointer, button);
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer) {
        if (viewport.processor != null)
            viewport.processor.touchDragged((int) x, (int) y, pointer);
        super.touchDragged(event, x, y, pointer);
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        if (viewport.processor != null)
            return viewport.processor.mouseMoved((int) x, (int) y);
        return super.mouseMoved(event, x, y);
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        super.enter(event, x, y, pointer, fromActor);
        viewport.getStage().setScrollFocus(viewport);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
        super.exit(event, x, y, pointer, toActor);
        viewport.getStage().setScrollFocus(viewport);
    }

    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
        if (viewport.processor != null)
            return viewport.processor.scrolled(amount);
        return super.scrolled(event, x, y, amount);
    }

    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if (viewport.processor != null)
            return viewport.processor.keyDown(keycode);
        return super.keyDown(event, keycode);
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        if (viewport.processor != null)
            return viewport.processor.keyUp(keycode);
        return super.keyUp(event, keycode);
    }

    @Override
    public boolean keyTyped(InputEvent event, char character) {
        if (viewport.processor != null)
            return viewport.processor.keyTyped(character);
        return super.keyTyped(event, character);
    }

}