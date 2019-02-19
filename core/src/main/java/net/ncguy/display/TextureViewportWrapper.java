package net.ncguy.display;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.asset.AssetHandler;
import net.ncguy.ui.TexturePainterFrame;
import net.ncguy.ui.dockable.DockableWidget;

import java.util.Optional;

public class TextureViewportWrapper extends DockableWidget {

    Table container;
    String imgRef;

    TexturePainterFrame painterFrame;

    float baseWidth;
    float baseHeight;

    float widthHeightRatio;
    float heightWidthRatio;

    public TextureViewportWrapper() {
        this("Texture");
    }

    public TextureViewportWrapper(String title) {
        super(title);

        container = new VisTable() {
            @Override
            protected void sizeChanged() {
                super.sizeChanged();
                updateImage();
            }
        };
//        container.addActor(image);
    }

    private void updateImage() {

        if(painterFrame == null) {
            return;
        }

        float tableWidth = container.getWidth();
        float tableHeight = container.getHeight();

        if(tableWidth > tableHeight) {
            painterFrame.setWidth(tableHeight * heightWidthRatio);
            painterFrame.setHeight(tableHeight);
        }else{
            painterFrame.setWidth(tableWidth);
            painterFrame.setHeight(tableWidth * widthHeightRatio);
        }

        painterFrame.setX((tableWidth - painterFrame.getWidth()) * 0.5f);
        painterFrame.setY((tableHeight - painterFrame.getHeight()) * 0.5f);
    }

    public void setImgRef(String imgRef) {
        this.imgRef = imgRef;
        AssetHandler.instance().GetAsync(imgRef, Texture.class, this::setTexture);
    }

    public void setTexture(Texture texture) {
//        this.texture = texture;
//        image.setDrawable(texture);

        if(painterFrame != null) {
            painterFrame.remove();
            painterFrame = null;
        }

        painterFrame = new TexturePainterFrame(texture);

        container.addActor(painterFrame);

        baseWidth = texture.getWidth();
        baseHeight = texture.getHeight();

        widthHeightRatio = baseWidth / baseHeight;
        heightWidthRatio = baseHeight / baseWidth;

        updateImage();
    }

    @Override
    public Optional<Table> getRootTable() {
        return Optional.ofNullable(container);
    }

    @Override
    public int getFlags() {
        return CLOSABLE;
    }

}
