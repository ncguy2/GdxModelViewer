package net.ncguy.ui.dockable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import net.ncguy.ui.IPanel;
import net.ncguy.utils.StageUtils;

public class TabContainer extends VisTable implements IPanel {

    DockableTabContainer dock;
    VisTable contentTable;

    public TabContainer() {
        super();
        init();
    }

    public boolean removeTab(IDockable tab) {
        return dock.removeTab(tab);
    }
    public boolean removeTab(DockableTab tab) {
        return dock.remove(tab);
    }

    public void addTab(IDockable tab) {
        dock.addTab(tab);
    }
    public void addTab(DockableTab tab) {
        dock.add(tab);
    }

    public void addTabs(IDockable... tabs) {
        for (IDockable tab : tabs)
            addTab(tab);
    }

    public void addTabs(DockableTab... tabs) {
        for (DockableTab tab : tabs)
            addTab(tab);
    }

    @Override
    public void initUI() {
        dock = new DockableTabContainer(this);
        contentTable = new VisTable();
    }

    @Override
    protected void sizeChanged() {
        invalidate();
    }

    @Override
    public void attachListeners() {
        StageUtils.PreventClickthrough(this);
        dock.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                super.switchedTab(tab);
                contentTable.clearChildren();
                if(tab != null)
                    contentTable.add(tab.getContentTable()).grow();
            }
        });
    }

    @Override
    public void assemble() {

        Value widthVal = new Value() {
            @Override
            public float get(Actor context) {
                if(context.hasParent()) {
                    context = context.getParent();
                }
                return context.getWidth();
            }
        };

        Value contentHeightVal = new Value() {
            @Override
            public float get(Actor context) {
                if(context.hasParent()) {
                    context = context.getParent();
                }
                return context.getHeight() - 24;
            }
        };

        add(dock.getTabsPane()).fillX().width(widthVal).height(24).row();
        add(contentTable).fill().width(widthVal).height(contentHeightVal).row();

        contentTable.toBack();
    }

    @Override
    public void style() {
        setBackground("window-border-bg");
    }

    @Override
    public String getTitle() {
        return "Tab";
    }

    @Override
    public Table getRootTable() {
        return this;
    }
}
