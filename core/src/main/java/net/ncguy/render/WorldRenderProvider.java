package net.ncguy.render;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import java.util.function.Supplier;

public class WorldRenderProvider implements RenderableProvider {

    private Supplier<ModelInstance> instanceSupplier;

    public WorldRenderProvider(Supplier<ModelInstance> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if (instanceSupplier == null) {
            return;
        }

        ModelInstance modelInstance = instanceSupplier.get();
        if (modelInstance == null) {
            return;
        }

        modelInstance.getRenderables(renderables, pool);
    }
}