package net.ncguy.ui;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import net.ncguy.asset.AssetHandler;
import net.ncguy.asset.AssetNote;

import java.util.HashMap;
import java.util.Map;

public class AssetNoteContainer extends VisTable implements AssetHandler.IAssetNoteListener {

    private final String ref;
    VisTable noteTray;
    VisLabel label;

    private Map<AssetNote, AssetNoteWidget> widgetMap;

    private static float noteSize = 16;

    public AssetNoteContainer(String text, String ref) {
        this.ref = ref;
        widgetMap = new HashMap<>();
        AssetHandler.addNoteListener(this);
        noteTray = new VisTable();

        label = new VisLabel(text);
        add(label).growX().left();
        add().growX();
        add(noteTray).fillY().right();

        rebuild();
    }

    @Override
    public void setColor(Color color) {
        label.setColor(color);
    }

    public void rebuild() {
        noteTray.clearChildren();

        noteTray.add().growX();

        widgetMap.values().forEach(noteTray::add);
    }

    @Override
    public void noteAdded(AssetNote note) {
        if(!note.refEquals(ref)) {
            return;
        }

        widgetMap.put(note, new AssetNoteWidget(note));
        rebuild();
    }

    @Override
    public void noteRemoved(AssetNote note) {
        if(!note.refEquals(ref)) {
            return;
        }

        widgetMap.remove(note);
        rebuild();
    }

    public static class AssetNoteWidget extends VisImage {

        private final AssetNote note;
        public AssetNoteWidget(AssetNote note) {
            this.note = note;
            build();
        }

        private void build() {
            setColor(note.noticeColour);
            setDrawable(note.noticeIcon);
            if(note.noticeText == null || note.noticeText.isEmpty()) {
                return;
            }

            Tooltip t = new Tooltip();
            t.setText(note.noticeText);
            t.setTarget(this);
        }

        @Override
        protected void sizeChanged() {
            setOrigin(getWidth() * .5f, getHeight() * .5f);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            rotateBy(note.noticeIconRotationSpeed * delta);
        }
    }

}
