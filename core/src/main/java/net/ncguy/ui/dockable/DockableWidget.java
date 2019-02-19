package net.ncguy.ui.dockable;

import java.util.Optional;

public abstract class DockableWidget implements IDockable {

    protected IDockableContainer parent;

    protected String title;

    public DockableWidget() {
        this(null);
    }

    public DockableWidget(String title) {
        this.title = title;
    }

    @Override
    public void setParent(IDockableContainer parent) {
        this.parent = parent;
    }

    @Override
    public Optional<IDockableContainer> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public String getTitle() {
        if (title == null || title.isEmpty()) {
            return getClass().getSimpleName();
        }
        return title;
    }

}
