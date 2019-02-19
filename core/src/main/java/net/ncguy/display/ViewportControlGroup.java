package net.ncguy.display;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.kotcrab.vis.ui.widget.VisTable;

public class ViewportControlGroup extends VisTable {

    protected final Vector2 alignment = new Vector2();

    public ViewportControlGroup() {
        super(true);
    }
    
    public void setAlignment(float x, float y) {
        alignment.x = x;
        alignment.y = y;
    }

    public void updateBounds() {
        pack();

        float width = getWidth();
        float height = getHeight();

        Group parent = getParent();
        float parentWidth = parent.getWidth();
        float parentHeight = parent.getHeight();

        setX((parentWidth * alignment.x) - (width * alignment.x));
        setY((parentHeight * alignment.y) - (height * alignment.y));
    }
    
}
