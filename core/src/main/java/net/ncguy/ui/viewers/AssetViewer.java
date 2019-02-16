package net.ncguy.ui.viewers;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import net.ncguy.ui.dockable.DockableWidget;

import java.util.Optional;

public class AssetViewer extends DockableWidget {

    @Override
    public Optional<Table> getRootTable() {
        return Optional.empty();
    }

}
