package net.ncguy.ui.dockable;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import net.ncguy.ui.FileTree;

import java.io.File;
import java.util.Optional;

public class DockableFileTree extends DockableWidget {

    protected FileTree fileTree;

    public DockableFileTree() {
        this("Window");
    }

    public DockableFileTree(String title) {
        this(title, null);
    }

    public DockableFileTree(FileTree fileTree) {
        this.fileTree = fileTree;
    }

    public DockableFileTree(String title, FileTree fileTree) {
        super(title);

        if(fileTree == null) {
            fileTree = new FileTree(new File("."));
        }

        this.fileTree = fileTree;
    }

    public FileTree getFileTree() {
        return fileTree;
    }

    @Override
    public Optional<Table> getRootTable() {
        return Optional.of(fileTree);
    }
}
