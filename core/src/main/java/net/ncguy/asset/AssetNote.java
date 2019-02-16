package net.ncguy.asset;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.io.File;

public class AssetNote {

    public final String assetRef;
    public NoteType type = NoteType.Information;

    public AssetNote(String assetRef) {
        this.assetRef = assetRef;
    }

    public Color noticeColour;
    public String noticeText;

    public Drawable noticeIcon;
    public float noticeIconRotationSpeed;


    public boolean refEquals(String otherRef) {
        return new File(assetRef).equals(new File(otherRef));
    }

    public enum NoteType {
        Progress,
        Information,
        Warning,
        Error
    }

}
