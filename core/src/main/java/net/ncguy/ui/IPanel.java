package net.ncguy.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.MenuItem;

public interface IPanel {

    default void init() {
        initUI();
        attachListeners();
        assemble();
        style();
    }

    void initUI();
    void attachListeners();
    void assemble();
    void style();

    default String getTooltip() {
        return "";
    }

    String getTitle();
    Table getRootTable();

    default public void addManagedMenuName(Class<?> owner, String name) {}
    default public void addManagedMenuItem(Class<?> owner, MenuItem item) {}
    default public void removeManagedItems(Class<?> owner) {}

}
