package net.ncguy.display;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import net.ncguy.asset.AssetHandler;
import net.ncguy.render.BasicRenderer;
import net.ncguy.render.DeferredRenderer;
import net.ncguy.render.FBO;
import net.ncguy.render.WorldRenderProvider;
import net.ncguy.ui.dockable.DockableWidget;

import java.util.Arrays;
import java.util.Optional;

public class ModelViewportWrapper extends DockableWidget {

    public WorldViewport viewport;
    public ModelInstance gridInstance;
    public ModelInstance modelInstance;
    public float currentHeightOffset;
    float targetHeight = 175;
    float scaleFactor = 1;

    public ModelViewportWrapper(Model grid, FBO.Builder fboBuilder) {
        gridInstance = new ModelInstance(grid);

        BasicRenderer renderer = new DeferredRenderer();
        WorldRenderProvider worldRenderProvider = new WorldRenderProvider(() -> Arrays.asList(
                modelInstance,
                gridInstance
        ));

        ViewportControlGroup ctrlGroup = new ViewportControlGroup();

        viewport = new WorldViewport(fboBuilder, false, renderer, worldRenderProvider) {
            @Override
            protected void sizeChanged() {
                super.sizeChanged();
                ctrlGroup.setAlignment(0, 1);
                ctrlGroup.updateBounds();
            }
        };
        viewport.AttachListeners();

        VisSelectBox<BasicRenderer.Attachment> attachmentSelection = new VisSelectBox<>();
        attachmentSelection.setItems(renderer.getAttachments());

        attachmentSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                renderer.setActiveAttachment(attachmentSelection.getSelected().index);
            }
        });

        ctrlGroup.add(attachmentSelection);

        viewport.addActor(ctrlGroup);
    }

    public WorldViewport getViewport() {
        return viewport;
    }

    public void loadModelInstance(String path) {
        AssetHandler.instance().GetAsync(path, Model.class, this::setModelInstance);
    }

    private void setModelInstance(Model model) {
        modelInstance = new ModelInstance(model);

        BoundingBox bounds = new BoundingBox();
        modelInstance.calculateBoundingBox(bounds);
        scaleFactor = calculateScaleFactor(bounds.getHeight());

        modelInstance.transform.setToScaling(scaleFactor, scaleFactor, scaleFactor);
        modelInstance.calculateBoundingBox(bounds);

        float yOffset = bounds.getCenterY();
        modelInstance.transform.translate(0, -yOffset, 0);
        currentHeightOffset = yOffset * scaleFactor;

        viewport.getMainCamera().far = 1024 * scaleFactor;
        gridInstance.transform.setToTranslationAndScaling(0, -(currentHeightOffset + 1), 0, viewport.getMainCamera().far, 1, viewport.getMainCamera().far);
    }

    private float calculateScaleFactor(float currentHeight) {
        if(currentHeight == 0.f) {
            return 1;
        }
        return targetHeight / currentHeight;
    }

    @Override
    public Optional<Table> getRootTable() {
        return Optional.ofNullable(viewport);
    }

    @Override
    public int getFlags() {
        return CLOSABLE;
    }
}