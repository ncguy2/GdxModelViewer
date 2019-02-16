package net.ncguy.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.toast.Toast;
import com.kotcrab.vis.ui.widget.toast.ToastTable;

public class Toaster {

    private static ToastManager toastManager;

    public static void subscribe(ToastManager toastManager) {
        Toaster.toastManager = toastManager;
    }

    public static void show(String text) {
        Gdx.app.postRunnable(() -> {
            if(toastManager != null) {
                toastManager.show(text);
            }
        });
    }

    public static void show(String text, float timeSec) {
        Gdx.app.postRunnable(() -> {
            if(toastManager != null) {
                toastManager.show(text, timeSec);
            }
        });
    }

    public static void show(Table table) {
        Gdx.app.postRunnable(() -> {
            if(toastManager != null) {
                toastManager.show(table);
            }
        });
    }

    public static void show(Table table, float timeSec) {
        Gdx.app.postRunnable(() -> {
            if(toastManager != null) {
                toastManager.show(table, timeSec);
            }
        });
    }

    public static void show(ToastTable toastTable) {
        Gdx.app.postRunnable(() -> {
            if(toastManager != null) {
                toastManager.show(toastTable);
            }
        });
    }

    public static void show(ToastTable toastTable, float timeSec) {
        Gdx.app.postRunnable(() -> {
            if(toastManager != null) {
                toastManager.show(toastTable, timeSec);
            }
        });
    }

    public static void show(Toast toast) {
        Gdx.app.postRunnable(() -> {
            if(toastManager != null) {
                toastManager.show(toast);
            }
        });
    }

    public static void show(Toast toast, float timeSec) {
        Gdx.app.postRunnable(() -> {
            if(toastManager != null) {
                toastManager.show(toast, timeSec);
            }
        });
    }
}
