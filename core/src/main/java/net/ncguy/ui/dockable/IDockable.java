package net.ncguy.ui.dockable;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Optional;

public interface IDockable {

    @SuppressWarnings("PointlessBitwiseExpression")
    public static final int SAVEABLE = 1 << 0;
    public static final int CLOSABLE = 1 << 1;

    void setParent(IDockableContainer parent);
    Optional<IDockableContainer> getParent();
    Optional<Table> getRootTable();

    default boolean is(int flag) {
        return (getFlags() & flag) == flag;
    }
    default int getFlags() {
        return 0;
    }

    default String getTitle() {
        return getClass().getSimpleName();
    }

}
