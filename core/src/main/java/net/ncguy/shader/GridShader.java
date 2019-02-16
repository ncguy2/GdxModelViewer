package net.ncguy.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import static com.badlogic.gdx.graphics.GL20.GL_BACK;

public class GridShader extends BaseShader {

    private final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewTrans"));
    private final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_worldTrans"));
    private final int UNIFORM_COLOUR_ADDITIVE = register(new Uniform("u_additiveColour"));

    public GridShader() {
        program = new ShaderProgram(Gdx.files.internal("shaders/grid.vert"), Gdx.files.internal("shaders/grid.frag"));
    }

    @Override
    public void init() {
        super.init(program, null);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.begin();

        context.setCullFace(GL_BACK);
        context.setDepthTest(GL20.GL_LEQUAL, 0, 1);
        context.setDepthMask(true);

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
    }

    @Override
    public void render(Renderable renderable, Attributes combinedAttributes) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);

        VertexAttribute attr = renderable.meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.ColorUnpacked);
        if(attr != null) {
            set(UNIFORM_COLOUR_ADDITIVE, Color.BLACK);
        }else set(UNIFORM_COLOUR_ADDITIVE, Color.WHITE);

        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }
}
