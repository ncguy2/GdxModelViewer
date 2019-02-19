package net.ncguy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.MultiSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.asset.AssetHandler;
import net.ncguy.display.ModelViewportWrapper;
import net.ncguy.display.TextureViewportWrapper;
import net.ncguy.display.Viewport3D;
import net.ncguy.render.FBO;
import net.ncguy.ui.FileTree;
import net.ncguy.ui.Toaster;
import net.ncguy.ui.dockable.DockableFileTree;
import net.ncguy.ui.dockable.TabContainer;

import java.io.File;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {

    Stage stage;
    Viewport stageViewport;
    OrthographicCamera stageCamera;
    TabContainer viewportContainer;

    Model gridModel;
    ModelInstance gridInstance;

    Table root;
    float targetHeight = 175;

    ToastManager toastManager;
    FBO.Builder fboBuilder;

   public float calculateScaleFactor(float currentHeight) {
        if(currentHeight == 0.f) {
            return 1;
        }
        return targetHeight / currentHeight;
    }

    public void openModel(String ref) {
        ModelViewportWrapper tab = new ModelViewportWrapper(gridModel, fboBuilder);
        viewportContainer.addTab(tab);
        tab.loadModelInstance(ref);
    }

    private void openTexture(String absolutePath) {
        TextureViewportWrapper tab = new TextureViewportWrapper();
        viewportContainer.addTab(tab);
        tab.setImgRef(absolutePath);
    }

    @Override
    public void show() {
        // Prepare your screen here.
        AssetHandler.Start();

        stageCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stageViewport = new ScreenViewport(stageCamera);
        stage = new Stage(stageViewport);

        fboBuilder = new FBO.Builder(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fboBuilder.addColorTextureAttachment(GL30.GL_RGB32F, GL20.GL_RGB, GL20.GL_FLOAT);
        fboBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT32F);


        long gridAttrs = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
        Material gridMtl = new Material();
        gridMtl.id = "grid";
        gridMtl.set(IntAttribute.createCullFace(0));
        gridMtl.set(FloatAttribute.createAlphaTest(0.1f));
        gridModel = new ModelBuilder().createRect(
                -1, 0, -1,
                 1, 0, -1,
                 1, 0,  1,
                -1, 0,  1,
                 0, 1, 0,
                gridMtl, gridAttrs
        );
        gridInstance = new ModelInstance(gridModel);

        root = new VisTable();

        FileTree fileTree = new FileTree(new File("."));
        fileTree.addSelectionListener(this::openFile);
        TabContainer sidebarContainer = new TabContainer();
        sidebarContainer.addTab(new DockableFileTree("File browser", fileTree));

        MultiSplitPane splitPane = new MultiSplitPane(false);

        viewportContainer = new TabContainer();

        splitPane.setWidgets(sidebarContainer, viewportContainer);
        splitPane.setSplit(0, .3f);
        root.add(splitPane).grow().row();

        stage.addActor(root);

        Gdx.input.setInputProcessor(stage);

        toastManager = new ToastManager(stage);
        Toaster.subscribe(toastManager);
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
        gridModel.dispose();
        AssetHandler.Dispose();
    }
}