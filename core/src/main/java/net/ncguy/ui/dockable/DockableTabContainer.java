package net.ncguy.ui.dockable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.SnapshotArray;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import net.ncguy.utils.ReflectionUtils;
import net.ncguy.utils.StageUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.ncguy.ui.dockable.IDockableContainer.Globals.*;

public class DockableTabContainer extends TabbedPane {

    protected boolean isHidden;
    protected boolean isPermanentContainer = false;
    protected float defaultHeight;
    protected float animDuration = .15f;

    protected Map<IDockable, DockableTab> tabMapping;

    protected final Actor anchor;
    protected final List<Consumer<DockableTab>> onTabAddedListeners = new ArrayList<>();

    public DockableTabContainer(Actor anchor) {
        super();
        tabMapping = new HashMap<>();
        this.anchor = anchor;
        init();
    }

    public boolean isPermanentContainer() {
        return isPermanentContainer;
    }

    public void setPermanentContainer(boolean permanentContainer) {
        isPermanentContainer = permanentContainer;
    }

    public boolean add(Consumer<DockableTab> dockableTabConsumer) {
        return onTabAddedListeners.add(dockableTabConsumer);
    }

    public boolean remove(Consumer<DockableTab> o) {
        return onTabAddedListeners.remove(o);
    }

    public void addAction(Action action) {
        anchor.addAction(action);
    }

    public void removeAction(Action action) {
        anchor.removeAction(action);
    }

    public void addTab(IDockable contents) {
        DockableTab tab = new DockableTab(contents);
        add(tab);
        notifyTabAddedListeners(tab);
        tabMapping.put(contents, tab);
        getActorFromTab(tab).ifPresent(a -> {
            a.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    IS_DRAGGING = false;
                    DRAGGING_TARGET.set(event.getStageX(), event.getStageY());
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    super.touchDragged(event, x, y, pointer);
                    IS_DRAGGING = true;
                    DRAGGING_TARGET.set(event.getStageX(), event.getStageY());
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                    if(DROP_HANDLER != null && IS_DRAGGING) {
                        if(!StageUtils.ActorContainsPoint(anchor, DRAGGING_TARGET)) {
                            DROP_HANDLER.drop(tab, DRAGGING_TARGET, event.getStage());
                        }
                        IS_DRAGGING = false;
                    }

                    super.touchUp(event, x, y, pointer, button);
                }
            });
        });
    }

    private Optional<Actor> getActorFromTab(Tab tab) {
        SnapshotArray<Actor> children = getTabsPane().getChildren();
        for (Actor child : children) {
            Optional<Object> opt = ReflectionUtils.GetPrivateField(child, "tab");
            if(opt.isPresent() && opt.get() == tab) {
                Optional<Actor> childOpt = Optional.of(child);
                return childOpt;
            }
        }
        return Optional.empty();
    }

    public boolean removeTab(IDockable contents) {
        if(!tabMapping.containsKey(contents)) {
            return false;
        }
        DockableTab tab = tabMapping.remove(contents);
        return remove(tab);
    }

    protected void notifyTabAddedListeners(DockableTab tab) {
        onTabAddedListeners.forEach(l -> l.accept(tab));
    }

    protected void init() {
        isHidden = getTabs().size > 1;
        defaultHeight = getTabsPane().getHeight();
        TAB_CONTAINERS.add(this);

        addListener(new TabbedPaneAdapter() {
            @Override
            public void removedTab(Tab tab) {
                super.removedTab(tab);
                invalidateHiddenState();
            }

            @Override
            public void removedAllTabs() {
                super.removedAllTabs();
                invalidateHiddenState();
            }
        });
    }

    private void invalidateHiddenState() {
        boolean shouldHide = getTabs().size <= 1;

        if(getTabs().size <= 0 && !isPermanentContainer) {
            getActiveTab().getContentTable().remove();
            getTabsPane().remove();
        }

        if(shouldHide == isHidden) {
            return;
        }

        isHidden = shouldHide;

        getTabsPane().addAction(Actions.alpha(shouldHide ? 0.1f : 1.0f, animDuration));
    }

    @Override
    protected void addTab(Tab tab, int index) {
        super.addTab(tab, index);
        invalidateHiddenState();
    }

    public void setWidth(float width) {
        getTabsPane().setWidth(width);
    }

    public void sizeChanged() {
        getTabsPane().pack();
    }

    public int count() {
        return getTabs().size;
    }

    public Set<IDockable> getInactiveTabs() {
        IDockable currentTab = getCurrentTab();
        return tabMapping.keySet()
                .stream()
                .filter(e -> e != currentTab)
                .collect(Collectors.toSet());
    }

    public IDockable getCurrentTab() {
        Tab activeTab = getActiveTab();
        for (Map.Entry<IDockable, DockableTab> entry : tabMapping.entrySet()) {
            if(entry.getValue() == activeTab) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Actor getSceneFocus() {
        return anchor;
    }

    public Vector2 getSceneLocation() {
        return getSceneFocus().localToStageCoordinates(new Vector2(0, 0));
    }

    public Vector2 getLocation() {
        return new Vector2(getSceneFocus().getX(), getSceneFocus().getY());
    }

    public Vector2 getSize() {
        return new Vector2(getSceneFocus().getWidth(), getSceneFocus().getHeight());
    }

    public Optional<Actor> getCurrentTabWidget() {
        return getActorFromTab(tabMapping.get(getCurrentTab()));
    }
}
