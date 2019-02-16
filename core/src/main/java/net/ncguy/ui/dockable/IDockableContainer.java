package net.ncguy.ui.dockable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public interface IDockableContainer {

    void onRemove(IDockable removed);
    Optional<IDockable> getContents();
    void setContents(IDockable contents);
    void addAction(Action action);

    default float getMetric() {
        return 0.0f;
    }

    default <T extends IDockableContainer> Optional<T> to(Class<T> type) {
        try {

            final Constructor<T> ctor = type.getConstructor();
            final T t = ctor.newInstance();

            this.getContents().ifPresent(c -> {
                c.getRootTable().ifPresent(Actor::remove);
                c.getParent().ifPresent(p -> p.onRemove(c));
                t.setContents(c);
            });

            return Optional.of(t);

        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public static class Globals {
        public static boolean IS_DRAGGING = false;
        public static DropHandler DROP_HANDLER;

        public static final Vector2 DRAGGING_TARGET = new Vector2();
        public static final Set<DockableTabContainer> TAB_CONTAINERS = new LinkedHashSet<>();

        static {
            DROP_HANDLER = (container, vector2, stage) -> {
                Gdx.app.log("INFO", "Tab dropped at "+vector2.toString()+", spawning window");
                final Optional<DockableWindow> window = container.to(DockableWindow.class);
                window.ifPresent(w -> {
                    stage.addActor(w.fadeIn());
                    w.setPosition(vector2.x, vector2.y, Align.top);
                });
            };
        }
    }

    @FunctionalInterface
    public static interface DropHandler  {
        void drop(IDockableContainer container, Vector2 target, Stage stage);
    }

}
