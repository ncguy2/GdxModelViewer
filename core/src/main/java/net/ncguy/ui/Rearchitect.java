package net.ncguy.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class Rearchitect {

    private static Rearchitect instance;
    public static Rearchitect get() {
        if (instance == null) {
            instance = new Rearchitect();
        }
        return instance;
    }

    protected Map<Class<? extends Actor>, RebuilderObject<? extends Actor>> rebuilderMap;

    public Rearchitect() {
        rebuilderMap = new HashMap<>();
    }

    public <T extends Actor> void rebuild(T actor) {
        Optional<? extends RebuilderObject<? extends Actor>> objOpt = getRebuilderObject(actor.getClass());

        if(!objOpt.isPresent()) {
            return;
        }

        @SuppressWarnings("unchecked")
        RebuilderObject<T> obj = (RebuilderObject<T>) objOpt.get();
        if(obj.filter.test(actor)) {
            obj.rebuilder.rebuild(actor.getStage(), actor.getParent(), actor);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Actor> Optional<RebuilderObject<T>> getRebuilderObject(Class<T> type) {
        RebuilderObject<? extends Actor> rebuilderObject;
        Class cls = type;
        do {
            rebuilderObject = rebuilderMap.get(cls);
            cls = cls.getSuperclass();
        }while(rebuilderObject == null && cls != Object.class);
        return Optional.ofNullable((RebuilderObject<T>) rebuilderObject);
    }

    public <T extends Actor> void addRebuilder(Class<T> type, Rebuilder<T> rebuilder) {
        rebuilderMap.put(type, new RebuilderObject<>(rebuilder));
    }

    public <T extends Actor> void addRebuilder(Class<T> type, Rebuilder<T> rebuilder, Predicate<T> filter) {
        rebuilderMap.put(type, new RebuilderObject<>(rebuilder, filter));
    }

    public static class RebuilderObject<T extends Actor> {
        public Rebuilder<T> rebuilder;
        public Predicate<T> filter;

        public RebuilderObject(Rebuilder<T> rebuilder) {
            this(rebuilder, a -> true);
        }

        public RebuilderObject(Rebuilder<T> rebuilder, Predicate<T> filter) {
            this.rebuilder = rebuilder;
            this.filter = filter;
        }
    }

    @FunctionalInterface
    public static interface Rebuilder<T extends Actor> {
        void rebuild(Stage stage, Group parent, T Actor);
    }

}
