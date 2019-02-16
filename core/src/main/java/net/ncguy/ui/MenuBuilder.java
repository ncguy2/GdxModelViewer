package net.ncguy.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MenuBuilder {

    public static MenuNode Begin() {
        return new MenuNode(null, "Root");
    }

    public static PopupMenu Build(MenuNode root) {
        PopupMenu menu = new PopupMenu();
        root.children.forEach(n -> BuildStep(menu, n));
        return menu;
    }

    public static void BuildStep(PopupMenu menu, MenuNode node) {
        if(node.name == null || node.name.isEmpty()) {
            menu.addSeparator();
            return;
        }

        if(node.isSeparator) {
            VisTable table = new VisTable(true);
            table.addSeparator().growX();
            table.add(menu).padLeft(4).padRight(4);
            table.addSeparator().growX().row();
            menu.add(table).padTop(2).padBottom(2).grow().row();
        }

        if(node.IsParent()) {
            MenuItem item = new MenuItem(node.name);
            PopupMenu subMenu = new PopupMenu();
            node.children.forEach(n -> BuildStep(subMenu, n));
            item.setSubMenu(subMenu);
            menu.addItem(item);
        }else menu.addItem(node.BuildRunnable());
    }

    public static class MenuNode {

        public final MenuNode parent;
        public final String name;
        public final List<MenuNode> children;
        public boolean isSeparator = false;
        public boolean isEnabled = true;
        public Runnable task;

        public MenuNode AddSeparator(String name) {
            MenuNode add = Add(name, null);
            add.isSeparator = true;
            return add;
        }

        public MenuNode AddSeparator() {
            return AddSeparator(null);
        }
        public MenuNode Add(String name) {
            return Add(name, null);
        }
        public MenuNode Add(String name, Runnable task) {
            Push(name).task = task;
            return this;
        }

        public MenuNode Push(String name) {
            MenuNode menuNode = new MenuNode(this, name);
            children.add(menuNode);
            return menuNode;
        }
        public MenuNode Pop() {
            return this.parent;
        }

        public MenuNode Do(Function<MenuNode, MenuNode> task) {
            return task.apply(this);
        }

        public MenuNode AddAndReturn(String name) {
            return AddAndReturn(name, null);
        }
        public MenuNode AddAndReturn(String name, Runnable task) {
            MenuNode node = new MenuNode(this, name);
            this.children.add(node);
            node.task = task;
            return node;
        }

        public MenuNode(MenuNode parent, String name) {
            this.parent = parent;
            this.name = name;
            this.children = new ArrayList<>();
            this.task = null;
        }

        public boolean IsParent() {
            return !this.children.isEmpty();
        }

        public void Invoke() {
            if(this.task != null)
                this.task.run();
        }

        public MenuItem BuildRunnable() {
            MenuItem item = new MenuItem(name) {
                @Override
                public void act(float delta) {
                    super.act(delta);
                    Object obj = getUserObject();
                    if(obj instanceof MenuNode)
                        setDisabled(((MenuNode) obj).task == null);
                    else setDisabled(true);
                }
            };
            item.setUserObject(this);
            item.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Object obj = item.getUserObject();
                    if(obj instanceof MenuNode)
                        ((MenuNode) obj).Invoke();
                }
            });
            item.setDisabled(!isEnabled);
            return item;
        }

        public PopupMenu Build() {
            return MenuBuilder.Build(this);
        }

    }

}
