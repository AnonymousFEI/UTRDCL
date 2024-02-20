package com.moran.andfix.patch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.moran.andfix.AndFixManager;
import com.moran.andfix.util.FileUtil;

/**
 * patch manager
 *
 * @author sanping.li@alipay.com
 *
 */
public class PatchManager {
    private static final String TAG = "PatchManager";
    // patch extension
    private static final String SUFFIX = ".apatch";
    private static final String DIR = "apatch";
    private static final String SP_NAME = "_andfix_";
    private static final String SP_VERSION = "version";

    /**
     * context
     */
    private final Context mContext;
    /**
     * AndFix manager
     */
    private final AndFixManager mAndFixManager;
    /**
     * patch directory
     */
    private final File mPatchDir;
    /**
     * patchs
     */
    private final SortedSet<Patch> mPatchs;
    /**
     * classloaders
     */
    private final Map<String, ClassLoader> mLoaders;

    /**
     * @param context
     *            context
     */
    public PatchManager(Context context) {
        mContext = context;
        mAndFixManager = new AndFixManager(mContext);
        mPatchDir = new File(mContext.getFilesDir(), DIR);
        mPatchs = new ConcurrentSkipListSet<Patch>();
        mLoaders = new ConcurrentHashMap<String, ClassLoader>();
    }

    /**
     * initialize
     *
     * @param appVersion
     *            App version
     */
    public void init(String appVersion) {
        if (!mPatchDir.exists() && !mPatchDir.mkdirs()) {// make directory fail
            Log.e(TAG, "patch dir create error.");
            return;
        } else if (!mPatchDir.isDirectory()) {// not directory
            mPatchDir.delete();
            return;
        }
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
        String ver = sp.getString(SP_VERSION, null);
        if (ver == null || !ver.equalsIgnoreCase(appVersion)) {
            cleanPatch();
            sp.edit().putString(SP_VERSION, appVersion).commit();
        } else {
            initPatchs();
        }
    }

    private void initPatchs() {
        File[] files = mPatchDir.listFiles();
        for (File file : files) {
            addPatch(file);
        }
    }

    /**
     * add patch file
     *
     * @param file
     * @return patch
     */
    private Patch addPatch(File file) {
        Patch patch = null;
        if (file.getName().endsWith(SUFFIX)) {
            try {
                patch = new Patch(file);
                mPatchs.add(patch);
            } catch (IOException e) {
                Log.e(TAG, "addPatch", e);
            }
        }
        return patch;
    }

    private void cleanPatch() {
        File[] files = mPatchDir.listFiles();
        for (File file : files) {
            mAndFixManager.removeOptFile(file);
            if (!FileUtil.deleteFile(file)) {
                Log.e(TAG, file.getName() + " delete error.");
            }
        }
    }

    /**
     * add patch at runtime
     *
     * @param path
     *            patch path
     * @throws IOException
     */
    public void addPatch(String path) throws IOException {
        File src = new File(path);
        File dest = new File(mPatchDir, src.getName());
        if(!src.exists()){
            throw new FileNotFoundException(path);
        }
        if (dest.exists()) {
            Log.d(TAG, "dest path is " + dest.toString());
            Log.d(TAG, "patch [" + path + "] has be loaded.");
            return;
        }
        FileUtil.copyFile(src, dest);// copy to patch's directory
        Patch patch = addPatch(dest);
        if (patch != null) {
            loadPatch(patch);
        }
    }

    /**
     * remove all patchs
     */
    public void removeAllPatch() {
        cleanPatch();
        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }

//    /**
//     * load patch,call when plugin be loaded. used for plugin architecture.</br>
//     *
//     * need name and classloader of the plugin
//     *
//     * @param patchName
//     *            patch name
//     * @param classLoader
//     *            classloader
//     */
//    public void loadPatch(String patchName, ClassLoader classLoader) {
//        mLoaders.put(patchName, classLoader);
//        Set<String> patchNames;
//        List<String> classes;
//        for (Patch patch : mPatchs) {
//            patchNames = patch.getPatchNames();
//            if (patchNames.contains(patchName)) {
//                classes = patch.getClasses(patchName);
//                mAndFixManager.fix(patch.getFile(), classLoader, classes);
//            }
//        }
//    }

//    /**
//     * load patch,call when application start
//     *
//     */
//    public void loadPatch() {
//        mLoaders.put("*", mContext.getClassLoader());// wildcard
//        Set<String> patchNames;
//        List<String> classes;
//        for (Patch patch : mPatchs) {
//            patchNames = patch.getPatchNames();
//            for (String patchName : patchNames) {
//                classes = patch.getClasses(patchName);
//                mAndFixManager.fix(patch.getFile(), mContext.getClassLoader(),
//                        classes);
//            }
//        }
//    }

    /**
     * uninstall all of the patch
     *
     */

    public void uninstallPatch(){
        Log.d(TAG, "uninstalling patch");
        mAndFixManager.uninstallAll();
    }

    /**
     * load specific patch
     *
     * @param patch
     *            patch
     */
    private void loadPatch(Patch patch) {
        Set<String> patchNames = patch.getPatchNames();
        ClassLoader cl;
        List<String> classes;
        for (String patchName : patchNames) {
            if (mLoaders.containsKey("*")) {
                cl = mContext.getClassLoader();
            } else {
                cl = mLoaders.get(patchName);
            }
            if (cl != null) {
                classes = patch.getClasses(patchName);
                // Edit by Xin, we need to test cookie here
//                mAndFixManager.fix(patch.getFile(), cl, classes);
                Log.d(TAG, "Begin to call test_dex_cookie");
                mAndFixManager.fix_from_memory(patch.getFile(), cl, null);
//                mAndFixManager.test_dex_cookies(patch.getFile(), cl, classes);
            }
        }
    }

    /**
     * We will load **ONLY ONE** patch from memory in time, so mLoaders and mContext are one-shot.
     * So after uninstalling the patch, the whole class will be destroyed.
     *
     * @param patch
     */

     public void loadMemoryPatch(byte[] patch){
        ClassLoader classLoader = mContext.getClassLoader();
        Log.d(TAG, "begin to fix the application");
        mAndFixManager.fix_from_memory(patch, classLoader, null);
    }

}
