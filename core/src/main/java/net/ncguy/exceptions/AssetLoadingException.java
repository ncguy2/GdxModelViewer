package net.ncguy.exceptions;

import net.ncguy.asset.AssetNote;

public class AssetLoadingException extends RuntimeException {

    public final AssetNote.NoteType type;

    public AssetLoadingException(AssetNote.NoteType type) {
        this.type = type;
    }

    public AssetLoadingException(String message, AssetNote.NoteType type) {
        super(message);
        this.type = type;
    }

    public AssetLoadingException(String message, Throwable cause, AssetNote.NoteType type) {
        super(message, cause);
        this.type = type;
    }

    public AssetLoadingException(Throwable cause, AssetNote.NoteType type) {
        super(cause);
        this.type = type;
    }

    public AssetLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, AssetNote.NoteType type) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.type = type;
    }
}
