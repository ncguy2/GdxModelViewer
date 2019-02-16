package net.ncguy.ui.dockable;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Optional;

public interface IDockable {

    void setParent(IDockableContainer parent);
    Optional<IDockableContainer> getParent();
    Optional<Table> getRootTable();

    default String getTitle() {
        return getClass().getSimpleName();
    }

}
