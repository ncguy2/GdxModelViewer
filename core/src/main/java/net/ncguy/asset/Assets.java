package net.ncguy.asset;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.function.Supplier;

public class Assets {

    public static final int ICON_SIZE = 22;
    public static final int FOLDER_ROOT_ICON_SIZE = 44;
    public static final int BIG_ICON_SIZE = 76;

    private static TextureAtlas icons;

    private static Skin compositeSkin;

    public static void load() {
        icons = new TextureAtlas("ui/icons.atlas");
        compositeSkin = new Skin();
        compositeSkin.addRegions(icons);
    }

    public static void dispose () {
        if (icons != null) icons.dispose();
        icons = null;
    }

    public static NinePatch GetPatch(String name) {
        return compositeSkin.getPatch(name);
    }

    public static NinePatchDrawable GetPatchDrawable(String name) {
        return new NinePatchDrawable(GetPatch(name));
    }

    public static Drawable get(String name, Atlases atlas) {
        return new TextureRegionDrawable(getRegion(name, atlas));
    }

    public static TextureRegion getRegion(String name, Atlases atlas) {
        return atlas.a().findRegion(name);
    }

    public static Drawable getIcon (String name) {
        return new TextureRegionDrawable(getIconRegion(name));
    }

    public static TextureRegion getIconRegion (String name) {
        return icons.findRegion(name);
    }

    public static enum Atlases {
        Icons(() -> Assets.icons),
        ;

        private final Supplier<TextureAtlas> atlasPath;

        Atlases(Supplier<TextureAtlas> atlasPath) {
            this.atlasPath = atlasPath;
        }

        public TextureAtlas a() {
            return atlasPath.get();
        }

        public TextureRegion get(String name) {
            return a().findRegion(name);
        }

    }

}
