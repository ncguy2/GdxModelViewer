package net.ncguy.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import net.ncguy.display.WorldViewport;

/** Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * @author badlogic */
public class OrbitCameraController extends InputAdapter implements WorldViewport.Updatable {
    private final Camera camera;
    private float velocity = 5;
    private float degreesPerPixel = 0.5f;
    private final Vector3 direction;
    private final Vector3 tmp = new Vector3();
    float distance = 500;
    float scrollStrength = 1f;
    private int lastButton;

    public boolean modifyCameraDirection = true;

    public Vector3 directionObject;

    public OrbitCameraController(Camera camera) {
        this.camera = camera;
        direction = new Vector3(this.camera.position).nor();
    }

    public boolean ShouldModifyOtherDirection() {
        return !modifyCameraDirection && directionObject != null;
    }

    /**
     * Sets how many degrees to rotate per pixel the mouse moved.
     *
     * @param degreesPerPixel
     */
    public void setDegreesPerPixel(float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    @Override
    public boolean scrolled(int amount) {
        Scrolled(amount * 1000);
        return super.scrolled(amount);
    }

    public void Scrolled(float amt) {
        scrollStrength = 1f;
        distance += (amt * scrollStrength) * Gdx.graphics.getDeltaTime();
        distance = Math.max(150f, Math.min(distance, 1000));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        lastButton = button;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = Gdx.input.getDeltaY() * degreesPerPixel;
        if(lastButton == Input.Buttons.LEFT) {

            Vector3 target;
            boolean constrainPitch;

            if(ShouldModifyOtherDirection()) {
                target = directionObject;
                constrainPitch = false;
                deltaX = -deltaX;
            }else {
                target = direction;
                constrainPitch = true;
            }

            target.rotate(camera.up, deltaX);
            tmp.set(target).crs(camera.up).nor();
            target.rotate(tmp, deltaY);
            if(constrainPitch)
                target.y = MathUtils.clamp(target.y, -0.9f, 0.9f);
            target.nor();

        }else if(lastButton == Input.Buttons.RIGHT) {
            Scrolled(deltaY);
        }

        return true;
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float deltaTime) {
        direction.y = MathUtils.clamp(direction.y, -0.9f, 0.9f);
        direction.nor();
        camera.position.set(direction.nor()).scl(distance);
        camera.lookAt(0, 0, 0);
        camera.up.set(0, 1, 0);
        camera.update(true);
    }
}