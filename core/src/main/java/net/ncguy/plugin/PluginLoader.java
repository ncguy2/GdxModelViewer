package net.ncguy.plugin;

import net.ncguy.plugin.api.LoaderPlugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

public class PluginLoader {

    private static List<LoaderPlugin> loadedPlugins;

    static ServiceLoader<LoaderPlugin> serviceLoader;
    static URLClassLoader classLoader;

    public static List<LoaderPlugin> getLoadedPlugins() throws MalformedURLException {
        if(loadedPlugins == null) {
            loadPlugins(new File("./plugins"));
        }
        return new ArrayList<>(loadedPlugins);
    }

    private static void loadPlugins(File directory) throws MalformedURLException {
        loadedPlugins = new ArrayList<>();

        if(!directory.exists()) {
            directory.mkdirs();
            return;
        }

        File[] files = directory.listFiles(pathname -> pathname.getPath().toLowerCase().endsWith(".jar"));
        URL[] urls = new URL[Objects.requireNonNull(files).length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }

        classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        serviceLoader = ServiceLoader.load(LoaderPlugin.class, classLoader);
        System.out.println("Loading plugins...");
        serviceLoader.forEach(p -> {
            System.out.printf("\tLoaded %s, providing %d definition%s%n", p.name(), p.numDefinitions(), p.numDefinitions() == 1 ? "" : "s");
            loadedPlugins.add(p);
        });
    }

}
