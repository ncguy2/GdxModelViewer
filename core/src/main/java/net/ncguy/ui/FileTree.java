package net.ncguy.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.kotcrab.vis.ui.widget.*;
import net.ncguy.ModelViewerLauncher;
import net.ncguy.asset.AssetHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public class FileTree extends VisTable {

    private VisTree tree;
    private File root;

    private List<Consumer<FileWrapper>> selectionListeners;

    public void setRoot(File root) {
        this.root = root;
        tree.clearChildren();
        buildNode(root);
    }

    public FileTree(File root) {

        selectionListeners = new ArrayList<>();

        VisTextField rootField = new VisTextField(root.getAbsolutePath());

        rootField.setTextFieldListener((textField, c) -> {
            File file = new File(rootField.getText());
            if(file.exists() && file.isDirectory()) {
                setRoot(file);
            }
        });

        tree = new VisTree();

        tree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Selection<Tree.Node> selection = tree.getSelection();
                if(selection == null) {
                    return;
                }
                Tree.Node lastSelected = selection.getLastSelected();
                if(lastSelected == null) {
                    return;
                }
                if(lastSelected instanceof FileTree.FileNode) {
                    File file = ((FileNode) lastSelected).file;
                    String ref = file.getAbsolutePath();

                    if(AssetHandler.hasErrorNote(ref)) {
                        return;
                    }
                    notifySelectionListeners(file);
                }
            }
        });

        VisScrollPane scroller = new VisScrollPane(tree);

        add(rootField).growX().row();
        add(scroller).grow().row();

        setRoot(root);
    }

    public void addSelectionListener(Consumer<FileWrapper> task) {
        selectionListeners.add(task);
    }

    public void notifySelectionListeners(File file) {
        FileWrapper fw = new FileWrapper(file);
        selectionListeners.forEach(l -> l.accept(fw));
    }

    private void buildNode(File parent) {
        dispatchConstruction(parent, f -> {
            FileNode fileNode = new FileNode(f);
            fileNode.getActor().setWidth(tree.getWidth() - (tree.getIndentSpacing() * fileNode.getLevel()));
            tree.add(fileNode);
        });
    }

    public static void dispatchConstruction(File root, Consumer<File> task) {
        dispatchConstruction(root, task, null);
    }
    public static void dispatchConstruction(File root, Consumer<File> task, Runnable completeTask) {
        ForkJoinPool.commonPool().execute(() -> {
            File[] files = root.listFiles();
            if (files != null && files.length != 0) {
                List<File> fList = Arrays.asList(files);
                fList.sort(FileTree::compareFileSuitability);
                for (File f : fList) {
                    Gdx.app.postRunnable(() -> task.accept(f));
                }
            }

            if(completeTask != null) {
                Gdx.app.postRunnable(completeTask);
            }
        });
    }

    public static class FileNode extends VisTree.Node {
        private final File file;
        private final boolean leaf;

        private boolean childrenLoaded;

        public FileNode(File file) {
            super(new AssetNoteContainer(file.getName(), file.getAbsolutePath()));
            Actor actor = getActor();

            actor.setColor(evaluateFileType(file).getColour());

            this.file = file;
            File[] files = file.listFiles();
            this.leaf = files == null || files.length == 0;

            childrenLoaded = false;

            if(!leaf) {
                add(new VisTree.Node(new VisLabel("Placeholder")));
            }

            getActor().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if(getTapCount() == 2) {
                        setExpanded(!isExpanded());
                    }
                }
            });

        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (!expanded || leaf || childrenLoaded) {
                return;
            }

            for (Object item : getChildren()) {
                if(item instanceof Tree.Node) {
                    ((Tree.Node) item).remove();
                }
            }

            getChildren().clear();

            childrenLoaded = true;

            dispatchConstruction(file, f -> {
                FileNode fileNode = new FileNode(f);
                add(fileNode);
            }, () -> {
                updateChildren();
                setExpanded(true);
            });

        }
    }

    public static FileType evaluateFileType(File file) {
        if(file.isDirectory()) {
            return FileType.Directory;
        }

        for (String supportedExtension : ModelViewerLauncher.supportedMeshExtensions) {
            if(file.getName().toLowerCase().endsWith(supportedExtension.toLowerCase())) {
                return FileType.MeshFile;
            }
        }

        for (String supportedExtension : ModelViewerLauncher.supportedTextureExtensions) {
            if(file.getName().toLowerCase().endsWith(supportedExtension.toLowerCase())) {
                return FileType.Texture;
            }
        }

        return FileType.Unknown;
    }

    public static int compareFileSuitability(File a, File b) {
        FileType typeA = evaluateFileType(a);
        FileType typeB = evaluateFileType(b);

        if(typeA == typeB) {
            return a.compareTo(b);
        }

        if(typeA == FileType.Directory) {
            return -1;
        }else if(typeB == FileType.Directory) {
            return 1;
        }

        // TODO add more comparison rules
        return a.compareTo(b);
    }

    public enum FileType {
        Directory(Color.WHITE),
        MeshFile(Color.GREEN),
        Texture(Color.CYAN),
        Unknown(Color.RED);

        private final Color colour;
        FileType(Color colour) {
            this.colour = colour;
        }

        public Color getColour() {
            return colour;
        }
    }

    public static class FileWrapper {
        public final File file;
        public final FileType type;

        public FileWrapper(File file) {
            this.file = file;
            this.type = evaluateFileType(file);
        }
    }

}
