package net.ncguy.ui.dockable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisWindow;
import javafx.beans.property.SimpleObjectProperty;
import net.ncguy.asset.SpriteCache;
import net.ncguy.utils.StageUtils;

import java.util.Optional;

import static net.ncguy.ui.dockable.IDockableContainer.Globals.TAB_CONTAINERS;

public class DockableWindow extends VisWindow implements IDockableContainer {

    protected IDockable content;
    protected transient SimpleObjectProperty<DockableTabContainer> dropCandidate = new SimpleObjectProperty<>(null);

    protected VisImage highlightActor;
    protected Vector2 oldPos = new Vector2();
    protected Vector2 oldDim = new Vector2();
    protected float lifeState;
    protected float lifeScale;

    public DockableWindow() {
        this("Window");
    }

    public DockableWindow(String title) {
        super(title);
        init();
        setResizable(true);
    }

    private void init() {
        float animTime = .15f;

        highlightActor = new VisImage(SpriteCache.Pixel());

        dropCandidate.addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                oldPos.set(newValue.getSceneLocation());
                oldDim.set(newValue.getSize());

                lifeScale = -animTime;
                this.addAction(Actions.alpha(.7f, animTime));
            }else{
                lifeScale = animTime;
                this.addAction(Actions.alpha(1f, animTime));
            }

            if(lifeScale != 0.f) {
                lifeScale = 1 / lifeScale;
            }
        }));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(highlightActor != null) {
            highlightActor.draw(batch, parentAlpha);
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        lifeState += delta * lifeScale;
        if(lifeState < 0) {
            lifeState = 0;
        }else if(lifeState > 1) {
            lifeState = 1;
        }

        highlightActor.getColor().a = (1 - lifeState) * .2f;
        Vector2 tgt = localToStageCoordinates(new Vector2()).lerp(oldPos, 1 - lifeState);
        Vector2 size = new Vector2(getWidth(), getHeight()).lerp(oldDim, 1 - lifeState);

        int inset = 4;
        int dInset = inset * 2;

        highlightActor.setPosition(tgt.x + inset, tgt.y + inset);
        highlightActor.setSize(size.x - dInset, size.y - dInset);

        highlightActor.act(delta);
        super.act(delta);

        if(isDragging()) {
            Optional<DockableTabContainer> opt = GetBestContainer(new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));
            dropCandidate.set(opt.orElse(null));
        }else if(dropCandidate.get() != null) {
            drop(dropCandidate.get());
            dropCandidate.set(null);
        }
    }

    private void drop(DockableTabContainer container) {
        container.addTab(this.content);
        setContents(null);
    }

    private Optional<DockableTabContainer> GetBestContainer(Vector2 point) {
        return TAB_CONTAINERS.stream()
                .filter(c -> StageUtils.ActorContainsPoint(c.getTabsPane(), point))
                .findFirst();
    }

    @Override
    public void onRemove(IDockable removed) {

    }

    @Override
    public Optional<IDockable> getContents() {
        return Optional.ofNullable(content);
    }

    @Override
    public void setContents(IDockable contents) {
        this.content = contents;
        clearChildren();
        if(this.content == null) {
            fadeOut(0.15f);
        }else{
            content.setParent(this);
            content.getRootTable().ifPresent(t -> this.add(t).space(4));
            getTitleLabel().setText(content.getTitle());
        }
    }

    @Override
    public float getMetric() {
        return getZIndex();
    }
}
