package net.ncguy.ui.dockable;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import net.ncguy.ui.IPanel;

import java.util.Optional;

public class DockablePanel implements IDockable {

    protected IDockableContainer parent;
    protected IPanel panel;

    public DockablePanel(IPanel panel) {
        this.panel = panel;
    }

    public IPanel getPanel() {
        return panel;
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
    public Optional<Table> getRootTable() {
        return Optional.ofNullable(panel.getRootTable());
    }

    @Override
    public String getTitle() {
        return panel.getTitle();
    }
}
