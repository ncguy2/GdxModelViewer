package net.ncguy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.MultiSplitPane;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.asset.AssetHandler;
import net.ncguy.display.Viewport3D;
import net.ncguy.display.WorldViewport;
import net.ncguy.render.BasicRenderer;
import net.ncguy.render.DeferredRenderer;
import net.ncguy.render.FBO;
import net.ncguy.render.WorldRenderProvider;
import net.ncguy.ui.FileTree;
import net.ncguy.ui.Toaster;
import net.ncguy.ui.dockable.*;

import java.io.File;
import java.util.Optional;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {

    Stage stage;
    Viewport stageViewport;
    OrthographicCamera stageCamera;

    WorldViewport viewport;
    ModelInstance instance;

    Table root;
    float targetHeight = 175;

    ToastManager toastManager;

    public static float currentHeightOffset;

    public float calculateScaleFactor(float currentHeight) {
        if(currentHeight == 0.f) {
            return 1;
        }
        return targetHeight / currentHeight;
    }

    public void openModel(String ref) {
        AssetHandler.instance().GetAsync(ref, Model.class, this::setModelInstance);
    }

    private void openTexture(String absolutePath) {

    }

    private void setModelInstance(Model model) {
        instance = new ModelInstance(model);
        BoundingBox bounds = new BoundingBox();
        instance.calculateBoundingBox(bounds);
        float scaleFactor = calculateScaleFactor(bounds.getHeight());

        instance.transform.setToScaling(scaleFactor, scaleFactor, scaleFactor);
        instance.calculateBoundingBox(bounds);

        float yOffset = bounds.getCenterY();
        instance.transform.translate(0, -yOffset, 0);
        currentHeightOffset = yOffset * scaleFactor;
    }

    @Override
    public void show() {
        // Prepare your screen here.
        AssetHandler.Start();

        stageCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageViewport = new ScreenViewport(stageCamera);
        stage = new Stage(stageViewport);

        FBO.Builder fboBuilder = new FBO.Builder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fboBuilder.addColorTextureAttachment(GL30.GL_RGB32F, GL20.GL_RGB, GL20.GL_FLOAT);
        fboBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT32F);

        BasicRenderer renderer = new DeferredRenderer();
        WorldRenderProvider worldRenderProvider = new WorldRenderProvider(() -> instance);

        viewport = new WorldViewport(fboBuilder, false, renderer, worldRenderProvider);
        viewport.AttachListeners();

        openModel("J:\\Character Meshes\\Mythra\\Mythra.g3dj");

        root = new VisTable();

        VisTable sidebar = new VisTable();
        sidebar.setBackground("window-border-bg");

        FileTree fileTree = new FileTree(new File("J:\\Character Meshes\\Mythra"));
        fileTree.addSelectionListener(this::openFile);
        TabContainer sidebarContainer = new TabContainer();
        sidebarContainer.addTab(new DockableFileTree("File browser", fileTree));
        sidebar.add(sidebarContainer).grow().row();

        MultiSplitPane splitPane = new MultiSplitPane(false);

        TabContainer actor = new TabContainer();
        TabContainer actor2 = new TabContainer();

        VisSplitPane pane = new VisSplitPane(actor, actor2, true);
        pane.addListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                pane.layout();
                actor.setDebug(true);
                actor2.setDebug(true);
                return super.mouseMoved(event, x, y);
            }
        });
        splitPane.setWidgets(sidebar, pane, viewport);
        splitPane.setSplit(0, .3f);
        root.add(splitPane).grow().row();

        stage.addActor(root);

        Gdx.input.setInputProcessor(stage);

        toastManager = new ToastManager(stage);
        Toaster.subscribe(toastManager);

        actor.addTabs(new DockableWidget("Tab 1") {
            @Override
            public Optional<Table> getRootTable() {
                VisTable visTable = new VisTable();
                visTable.add("Test 1");
                return Optional.of(visTable);
            }
        }, new DockableWidget("Tab 2") {
            @Override
            public Optional<Table> getRootTable() {
                VisTable visTable = new VisTable();
                visTable.add("Test 2");
                return Optional.of(visTable);
            }
        });
        actor2.addTabs(new DockableWidget("Tab 3") {
            @Override
            public Optional<Table> getRootTable() {
                VisTable visTable = new VisTable();
                visTable.add("Test 3");
                return Optional.of(visTable);
            }
        });

    }

    private void openFile(FileTree.FileWrapper f) {
        if(f.type == FileTree.FileType.MeshFile) {
            openModel(f.file.getAbsolutePath());
        }else if(f.type == FileTree.FileType.Texture) {
            openTexture(f.file.getAbsolutePath());
        }
    }

    @Override
    public void render(float delta) {
        AssetHandler.instance().Update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Viewport3D.RenderViewports();

        stage.act(delta);
        stage.draw();

        stage.setDebugAll(false);
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        stageViewport.update(width, height, true);
        root.setBounds(0, 0, width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        AssetHandler.Dispose();
    }
}