package net.ncguy.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.toast.ToastTable;
import net.ncguy.exceptions.AssetLoadingException;
import net.ncguy.plugin.PluginLoader;
import net.ncguy.plugin.api.AssetLoaderDefinition;
import net.ncguy.ui.Toaster;
import net.ncguy.utils.ReflectionUtils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.ncguy.asset.AssetNote.NoteType.Progress;

public class AssetHandler implements Disposable {

    public static final Map<String, Class> extMapping = new HashMap<>();
    private static AssetHandler instance;

    static {
        // Texture
        extMapping.put("png", Texture.class);
        extMapping.put("jpg", Texture.class);
        extMapping.put("jpeg", Texture.class);
        extMapping.put("bmp", Texture.class);

        // Model
        extMapping.put("g3dj", Model.class);
        extMapping.put("g3db", Model.class);
//        extMapping.put("fbx", Model.class);
        extMapping.put("obj", Model.class);
    }

    public final List<AssetNote> assetNotes = new CopyOnWriteArrayList<>();
    public final List<WeakReference<IAssetNoteListener>> noteListeners = new CopyOnWriteArrayList<>();
    public boolean generateMipmaps = true;
    protected Map<String, Consumer<?>> asyncRequests;
    protected AssetManager manager;
    boolean isLoading = false;
    Array<AssetDescriptor> tasks;
    ObjectMap<Class, ObjectMap<String, AssetLoader>> loaders;

    private AssetHandler() {
        manager = new AssetManager();
        asyncRequests = new HashMap<>();

        manager.setErrorListener((asset, throwable) -> {
            throwable.printStackTrace();
            asyncRequests.remove(asset.fileName);
            List<AssetNote> assetNotes = getAssetNotes(asset.fileName);
            assetNotes.stream().filter(n -> n.type == Progress).forEach(AssetHandler::removeAssetNote);

            AssetNote note = addAssetNote(asset.fileName);
            note.type = AssetNote.NoteType.Warning;

            boolean typeDiscerned = false;

            Throwable tgt = throwable;
            while(tgt.getCause() != null && !typeDiscerned) {
                tgt = tgt.getCause();

                    if (tgt instanceof AssetLoadingException) {
                        typeDiscerned = true;
                        note.type = ((AssetLoadingException) tgt).type;
                    }
            }

            note.noticeText = tgt.getMessage();

            if(note.noticeText == null || note.noticeText.trim().isEmpty()) {
                note.noticeText = tgt.getClass().getSimpleName();
            }

            switch (note.type) {
                case Progress:
                    note.noticeColour = Color.WHITE;
                    note.noticeIcon = Assets.getIcon("progress");
                    break;
                case Information:
                    note.noticeColour = Color.CYAN;
                    note.noticeIcon = Assets.getIcon("question-big");
                    break;
                case Warning:
                    note.noticeColour = Color.YELLOW;
                    note.noticeIcon = Assets.getIcon("warning");
                    break;
                case Error:
                    note.noticeColour = Color.RED;
                    note.noticeIcon = Assets.getIcon("warning");
                    break;
            }

            ToastTable toast = new ToastTable();
            Cell<VisImage> iconCell;
            if(note.noticeIcon != null) {
                VisImage img = new VisImage(note.noticeIcon);
                iconCell = toast.add(img);
                img.setColor(note.noticeColour);
            }else{
                iconCell = toast.add();
            }
            iconCell.size(48);

            String name = asset.fileName.replace("\\\\", "/");
            VisTable table = new VisTable();
            VisLabel title = new VisLabel(name.substring(name.lastIndexOf("/") + 1));
            VisLabel text = new VisLabel(note.noticeText, "small");
            table.add(title).growX().row();
            table.add(text).growX().row();

            toast.add(table).growX().row();
            Toaster.show(toast);

        });

        try {
            PluginLoader.getLoadedPlugins().forEach(plugin -> {
                System.out.println("Registering loader definitions from " + plugin.name());
                List<AssetLoaderDefinition<?>> definitions = plugin.definitions(manager.getFileHandleResolver());
                System.out.println(" >> Definitions found: " + definitions.size());
                for (AssetLoaderDefinition def : definitions) {
                    System.out.println(" >>>> Registering " + def.displayName);
                    if(def.supportedExtensions.isEmpty()) {
                        manager.setLoader(def.loadedType, def.loader);
                    } else {
                        for (Object ext : def.supportedExtensions) {
                            manager.setLoader(def.loadedType, ext.toString(), def.loader);
                        }
                    }
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        manager.setLoader(Model.class, new AssetLoader<Model, AssetLoaderParameters<Model>>() {
//            @Override
//            public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AssetLoaderParameters<Model> parameter) {
//                return null;
//            }
//        });

//        manager.setLoader(Texture.class, new FileTextureLoader(manager.getFileHandleResolver()));

        //noinspection unchecked
        tasks = (Array<AssetDescriptor>) ReflectionUtils.GetPrivateField(manager, "loadQueue")
                .orElse(null);

        loaders = (ObjectMap<Class, ObjectMap<String, AssetLoader>>) ReflectionUtils.GetPrivateField(manager, "loaders").orElse(null);
    }

    public static void Start() {
        instance();
    }

    public static void Dispose() {
        WithInstanceIfExists(AssetHandler::dispose);
    }

    public static List<AssetNote> getAssetNotes(String ref) {
        return instance().assetNotes.stream().filter(n -> n.refEquals(ref)).collect(Collectors.toList());
    }

    public static AssetNote addAssetNote(String ref) {
        AssetNote note = new AssetNote(ref);
        instance().assetNotes.add(note);
        notifyNoteListeners(note, true);
        return note;
    }

    public static AssetNote addProgressNote(String ref) {
        AssetNote note = addAssetNote(ref);
        note.noticeColour = Color.WHITE;
        note.noticeIcon = Assets.getIcon("progress");
        note.type = Progress;
        note.noticeIconRotationSpeed = 90;
        return note;
    }

    public static boolean hasErrorNote(String ref) {
        return getAssetNotes(ref).stream().anyMatch(n -> n.type == AssetNote.NoteType.Error);
    }

    public static void removeAssetNote(AssetNote note) {
        instance().assetNotes.remove(note);
        notifyNoteListeners(note, false);
    }

    public static void clearAssetNotes(String ref) {
        List<AssetNote> assetNotes = getAssetNotes(ref);
        instance().assetNotes.removeAll(assetNotes);
        assetNotes.forEach(n -> notifyNoteListeners(n, false));
    }

    public static void clearLoadingAssetNotes() {
        List<AssetNote> assetNotes = instance().assetNotes.stream().filter(n -> n.type == Progress).collect(Collectors.toList());
        instance().assetNotes.removeAll(assetNotes);
        assetNotes.forEach(n -> notifyNoteListeners(n, false));
    }

    public static void addNoteListener(IAssetNoteListener listener) {
        instance().noteListeners.add(new WeakReference<>(listener));
    }

    public static void removeNoteListener(IAssetNoteListener listener) {
        List<WeakReference<IAssetNoteListener>> collect = instance().noteListeners
                .stream()
                .filter(r -> r.get() == null || r.get() == listener)
                .collect(Collectors.toList());
        instance().noteListeners.removeAll(collect);
    }

    public static void notifyNoteListeners(AssetNote note, boolean added) {
        Gdx.app.postRunnable(() -> notifyNoteListenersImmediate(note, added));
    }
    public static void notifyNoteListenersImmediate(AssetNote note, boolean added) {
        instance().noteListeners
                .stream()
                .filter(Objects::nonNull)
                .map(Reference::get)
                .filter(Objects::nonNull)
                .forEach(l -> {
                    if(added) {
                        l.noteAdded(note);
                    }else{
                        l.noteRemoved(note);
                    }
                });
    }

    public static AssetHandler instance() {
        if (instance == null)
            instance = new AssetHandler();
        return instance;
    }

    public static void WithInstanceIfExists(Consumer<AssetHandler> task) {
        if (instance != null)
            task.accept(instance);
    }

    public static Class<?> GetType(String ext) {
        if (ext.startsWith("."))
            ext = ext.substring(1);
        return extMapping.getOrDefault(ext, Object.class);
    }

    public Optional<AssetDescriptor> GetNextTask() {
        if (tasks.size <= 0) return Optional.empty();
        return Optional.ofNullable(tasks.peek());
    }

    public boolean IsLoading() {
        return this.isLoading;
    }

    public float GetProgress() {
        return manager.getProgress();
    }

    public void Update() {
        isLoading = !manager.update();
        asyncRequests.entrySet()
                .stream()
                .filter(e -> manager.isLoaded(e.getKey()))
                .peek(e -> e.getValue()
                        .accept(manager.get(e.getKey())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(asyncRequests::remove);

    }


    public <T> T Get(String pth, Class<T> type) {

        if (pth == null || pth.isEmpty())
            return null;

        final String path = pth;

        if (!manager.isLoaded(path, type)) {

            FileHandle handle = Gdx.files.internal(path);

            if (!handle.exists())
                handle = Gdx.files.external(path);

            if (!handle.exists())
                return null;

            AtomicReference<T> item = new AtomicReference<>();

            FileHandle finalHandle = handle;
            AssetDescriptor tAssetDescriptor;
            if (type.equals(Texture.class)) {
                TextureLoader.TextureParameter p = new TextureLoader.TextureParameter();
                p.genMipMaps = generateMipmaps;
                p.minFilter = Texture.TextureFilter.MipMapLinearLinear;
                p.magFilter = Texture.TextureFilter.Linear;
                p.wrapU = Texture.TextureWrap.Repeat;
                p.wrapV = Texture.TextureWrap.Repeat;
                tAssetDescriptor = new AssetDescriptor<>(finalHandle, Texture.class, p);
            } else tAssetDescriptor = new AssetDescriptor<>(finalHandle, type);
            manager.load(tAssetDescriptor);
            manager.finishLoadingAsset(path);
            item.set(manager.get(path, type));

            return item.get();
        }

        return manager.get(path, type);
    }

    public <T> void GetAsync(String path, Class<T> type, Consumer<T> func) {
        if (path == null || path.isEmpty())
            return;

        path = path.toLowerCase();

        if (manager.isLoaded(path, type)) {
            func.accept(manager.get(path, type));
            return;
        }

        FileHandle handle = Gdx.files.internal(path);

        if (handle.exists() && handle.isDirectory()) {
            return;
        }


        if (!handle.exists())
            handle = Gdx.files.external(path);

        if (!handle.exists() || handle.isDirectory() || handle.extension().isEmpty())
            return;

        path = handle.path();

        if (manager.isLoaded(path, type)) {
            func.accept(manager.get(path, type));
            return;
        }

        AssetNote note = addProgressNote(path);

        if (asyncRequests.containsKey(path)) return;
        if (IsAbsolutePath(path)) {
            if (handle.exists() && !handle.isDirectory())
                manager.load(new AssetDescriptor<>(handle, type));
        } else {
            if (generateMipmaps && type.equals(Texture.class)) {
                TextureLoader.TextureParameter p = new TextureLoader.TextureParameter();
                p.genMipMaps = true;
                p.minFilter = Texture.TextureFilter.MipMapLinearLinear;
                p.magFilter = Texture.TextureFilter.Linear;
                p.wrapU = Texture.TextureWrap.Repeat;
                p.wrapV = Texture.TextureWrap.Repeat;
                manager.load(path, Texture.class, p);
            } else manager.load(path, type);
        }

        asyncRequests.put(path, func.andThen(o -> removeAssetNote(note)));
    }

    public <T> List<T> GetOfType(Class<T> type) {
        Array<T> objects = new Array<>();
        manager.getAll(type, objects);
        List<T> list = new ArrayList<>();
        objects.forEach(list::add);
        return list;
    }

    public boolean IsAbsolutePath(String path) {
        if (path.length() >= 2)
            return path.charAt(1) == ':';
        return false;
    }

    public boolean IsLoaded(String path) {
        return manager.isLoaded(path);
    }

    public boolean IsLoaded(String path, Class<?> cls) {
        return manager.isLoaded(path, cls);
    }

    public void UsingManager(Consumer<AssetManager> func) {
        func.accept(manager);
    }

    public <T> String GetAssetFileName(T asset) {
        return manager.getAssetFileName(asset);
    }

    public <T> List<T> AllAssetsOfTypeInDirectory(FileHandle root, Class<T> type) {
        List<T> list = new ArrayList<>();
        AllAssetsOfTypeInDirectory(root, type, list);
        return list;
    }

    public <T> void AllAssetsOfTypeInDirectory(FileHandle root, Class<T> type, List<T> list) {
        String ext = root.extension();
        if (extMapping.containsKey(ext)) {
            Class extCls = extMapping.get(ext);
            if (extCls.equals(type)) {
                T t = Get(root.path(), type);
                if (t != null)
                    list.add(t);
            }
        }

        for (FileHandle child : root.list())
            AllAssetsOfTypeInDirectory(child, type, list);
    }

    public <T> List<T> AllAssetsOfTypeInRegistry(Class<T> type) {
        List<T> list = new ArrayList<>();
        AllAssetsOfTypeInRegistry(type, list);
        return list;
    }

    public <T> void AllAssetsOfTypeInRegistry(Class<T> type, List<T> list) {
        Array<T> out = new Array<>();
        manager.getAll(type, out);
        out.forEach(list::add);
    }

    @Override
    public void dispose() {
        manager.dispose();
        instance = null;
    }

    public Texture DefaultTexture() {
        return SpriteCache.Default()
                .getTexture();
    }

    public List<String> getLoadersOfType(Class<?> assetType) {
        if(loaders == null) {
            return Arrays.asList(
                    "g3dj",
                    "g3db"
            );
        }

        List<String> exts = new ArrayList<>();

        ObjectMap<String, AssetLoader> entries = loaders.get(assetType);
        for (String key : entries.keys()) {
            if(key.isEmpty()) {
                continue;
            }
            exts.add(key);
        }

        return exts;
    }

    public interface IAssetNoteListener {
        void noteAdded(AssetNote note);

        void noteRemoved(AssetNote note);
    }

}