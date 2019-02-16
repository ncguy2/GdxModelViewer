package net.ncguy.ui.dockable;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

import java.util.Optional;

public class DockableTab extends Tab implements IDockableContainer {

    protected IDockable content;
    protected Table table;

    public DockableTab() {
        this(false, false);
    }

    public DockableTab(boolean savable) {
        super(savable, false);
    }

    public DockableTab(boolean savable, boolean closeableByUser) {
        super(savable, closeableByUser);
    }

    public DockableTab(IDockable content) {
        super(false, false);
        this.content = content;
        content.setParent(this);
    }

    public DockableTab(boolean savable, IDockable content) {
        super(savable, false);
        this.content = content;
    }

    public DockableTab(boolean savable, boolean closeableByUser, IDockable content) {
        super(savable, closeableByUser);
        this.content = content;
    }

    @Override
    public void onRemove(IDockable removed) {
        if(removed.equals(content)) {
            getPane().remove(this);
        }
    }

    @Override
    public Optional<IDockable> getContents() {
        return Optional.ofNullable(content);
    }

    @Override
    public void setContents(IDockable contents) {
        this.content = contents;
        if(this.content != null) {
            this.content.setParent(this);
        }
    }

    @Override
    public void addAction(Action action) {
        table.addAction(action);
    }

    @Override
    public float getMetric() {
        return getContentTable().getZIndex();
    }

    @Override
    public String getTabTitle() {
        return getContents()
                .map(IDockable::getTitle)
                .orElse(null);
    }

    @Override
    public final Table getContentTable() {
        if(table == null) {
            table = getContents()
                    .flatMap(IDockable::getRootTable)
                    .orElse(null);
        }
        return table;
    }
}
