package net.ncguy.ui.dockable;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.MultiSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import net.ncguy.ui.IPanel;
import net.ncguy.ui.MenuBuilder;
import net.ncguy.utils.StageUtils;

import java.util.ArrayList;
import java.util.List;

public class MultiTabContainer extends MultiSplitPane implements IPanel {
    
    protected List<DockWidget> docks;

    public MultiTabContainer(boolean vertical) {
        super(vertical);
        init();
    }

    public void split(DockableTabContainer dock) {
        IDockable currentTab = dock.getCurrentTab();
        int i = addDock();
        DockableTabContainer tgtDock = getDock(i);
//        dock.getWidget(currentTab).ifPresent(Actor::remove);
        tgtDock.addTab(currentTab);
    }

    public void openContextMenu(DockableTabContainer dock, Vector2 screenCoords) {
        MenuBuilder.MenuNode begin = MenuBuilder.Begin();
        MenuBuilder.MenuNode split_current_view = begin.AddAndReturn("Split current view", () -> split(dock));
        split_current_view.isEnabled = dock.count() > 1;

        begin.Build().showMenu(getStage(), screenCoords.x, screenCoords.y);
    }

    public void addTabs(int dockIdx, IDockable... tabs) {
        for (IDockable tab : tabs) {
            addTab(dockIdx, tab);
        }
    }

    public void addTabs(int dockIdx, DockableTab... tabs) {
        for (DockableTab tab : tabs) {
            addTab(dockIdx, tab);
        }
    }

    public DockableTabContainer getDock(int dockIdx) {
        DockWidget dockWidget = docks.get(dockIdx);
        if(dockWidget == null) {
            dockWidget = docks.get(addDock());
        }
        return dockWidget.getContainer();
    }

    public void addTab(int dockIdx, IDockable tab) {
        getDock(dockIdx).addTab(tab);
    }
    public void addTab(int dockIdx, DockableTab tab) {
        getDock(dockIdx).add(tab);
    }
    
    public boolean removeTab(int dockIdx, IDockable tab) {
        return getDock(dockIdx).removeTab(tab);
    }
    public boolean removeTab(int dockIdx, DockableTab tab) {
        return getDock(dockIdx).remove(tab);
    }

    @Override
    public void initUI() {
        docks = new ArrayList<>();
        addDock();
    }

    public int addDock() {
        DockableTabContainer dock = new DockableTabContainer(this);
        DockWidget dockWidget = new DockWidget(dock);
        docks.add(dockWidget);
        updateWidgets();
        return docks.indexOf(dockWidget);
    }

    private void updateWidgets() {
        setWidgets(docks.toArray(new DockWidget[0]));
    }

    @Override
    public void attachListeners() {
        StageUtils.PreventClickthrough(this);

        addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                DockWidget w = getDockAtPoint(x, y);
                if(w == null) {
                    return;
                }
                DockableTabContainer dock = w.getContainer();
                openContextMenu(dock, new Vector2(event.getStageX(), event.getStageY()));
            }
        });
    }

    private DockWidget getDockAtPoint(float x, float y) {
        Vector2 hitLocation = new Vector2(x, y);
        localToStageCoordinates(hitLocation);

        return docks.stream()
                .filter(dock -> {
                    Vector2 tgt = localToStageCoordinates(new Vector2());
                    Rectangle rect = new Rectangle(tgt.x, tgt.y, dock.getWidth(), dock.getHeight());
                    return rect.contains(hitLocation);
                }).findFirst()
                .orElse(null);
    }

    @Override
    public void assemble() {
    }

    @Override
    public void style() {
    }

    @Override
    public String getTitle() {
        return "Tab";
    }

    @Override
    public Table getRootTable() {
        VisTable visTable = new VisTable();
        visTable.add(this).grow().row();
        return visTable;
    }

    public static class DockWidget extends VisTable {

        private final DockableTabContainer container;
        public DockWidget(DockableTabContainer container) {
            this.container = container;
            add(container.getTabsPane()).growX().row();
            VisTable content = new VisTable();
            container.addListener(new TabbedPaneAdapter() {
                @Override
                public void switchedTab(Tab tab) {
                    super.switchedTab(tab);
                    content.clearChildren();
                    if(tab != null) {
                        Table contentTable = tab.getContentTable();
                        if(contentTable != null) {
                            content.add(contentTable).grow().row();
                        }
                    }
                }
            });
        }

        public DockableTabContainer getContainer() {
            return container;
        }
    }
}
