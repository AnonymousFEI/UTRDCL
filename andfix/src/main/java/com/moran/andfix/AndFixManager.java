package com.moran.andfix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.DuplicateFormatFlagsException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.moran.andfix.annotation.MethodReplace;
import com.moran.andfix.security.SecurityChecker;
import dalvik.system.DexFile;

/**
 * AndFix Manager
 *
 * @author sanping.li@alipay.com
 *
 */
public class AndFixManager {

    private static final String TAG = "AndFixManager";

    static {
        try {
            Runtime.getRuntime().loadLibrary("andfix");
        } catch (Throwable e) {
            Log.e(TAG, "loadLibrary", e);
        }
    }

    private static native Object memoryLoadDexFile(byte[] dexContent, int start_index, int end_index);

    private static final String DIR = "apatch_opt";


    /**
     *  save method
     */
    private static ArrayList<Method> mOriginalMethod;
    private static ArrayList<Method> mPatchMethod;

    /**
     * context
     */
    private final Context mContext;

    /**
     * classes will be fixed
     */
    private static Map<String, Class<?>> mFixedClass = new ConcurrentHashMap<String, Class<?>>();

    /**
     * whether support AndFix
     */
    private boolean mSupport = false;

    /**
     * security check
     */
    private SecurityChecker mSecurityChecker;

    /**
     * optimize directory
     */
    private File mOptDir;


    private static class DFEnum implements Enumeration<String> {
        private int mIndex;
        private String[] mNameList;

        DFEnum(Object cookie) {
            mIndex = 0;
            try{
                Log.d(TAG, "In DFEnum constructor");
                Class cls = Class.forName("dalvik.system.DexFile");
                Log.d(TAG, "get dexfile class");
                Method method = cls.getDeclaredMethod("getClassNameList", Object.class);
                Log.d(TAG, "get getClassNameList");
                method.setAccessible(true);
                Log.d(TAG, "set accessible");
                mNameList = (String[]) method.invoke(null, cookie);
                Log.d(TAG, "call getClassNameList method, mNameList is");
                for (String name: mNameList){
                    Log.d(TAG, name);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public boolean hasMoreElements(){
            return (mIndex < mNameList.length);
        }

        public String nextElement(){
            return mNameList[mIndex++];
        }
    }

    private static class CookieClassLoader{
        private String mClassName;
        private ClassLoader mLoader;

        CookieClassLoader(String className, ClassLoader loader){
            this.mClassName = className.replace('.', '/');
            this.mLoader = loader;
        }

        public Class loadClass(Object cookie){
            Class result = null;
            try{
                //Load here
                Class cls = Class.forName("dalvik.system.DexFile");
                @SuppressLint("SoonBlockedPrivateApi") Method method = cls.getDeclaredMethod("defineClassNative", String.class, ClassLoader.class, Object.class, DexFile.class);
                method.setAccessible(true);
                result = (Class) method.invoke(null, this.mClassName, this.mLoader, cookie, null);
            } catch (NoClassDefFoundError e){
                e.printStackTrace();
            } catch (ClassNotFoundException e){

            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public AndFixManager(Context context) {
        mContext = context;
        mSupport = Compat.isSupport();
        mOriginalMethod = new ArrayList<>();
        mPatchMethod = new ArrayList<>();
        if (mSupport) {
            mSecurityChecker = new SecurityChecker(mContext);
            mOptDir = new File(mContext.getFilesDir(), DIR);
            if (!mOptDir.exists() && !mOptDir.mkdirs()) {// make directory fail
                mSupport = false;
                Log.e(TAG, "opt dir create error.");
            } else if (!mOptDir.isDirectory()) {// not directory
                mOptDir.delete();
                mSupport = false;
            }
        }
    }

    /**
     * delete optimize file of patch file
     *
     * @param file
     *            patch file
     */
    public synchronized void removeOptFile(File file) {
        File optfile = new File(mOptDir, file.getName());
        if (optfile.exists() && !optfile.delete()) {
            Log.e(TAG, optfile.getName() + " delete error.");
        }
    }


    /**
     * fix class
     *
     * @param clazz
     *            class
     */
    private void fixClass(Class<?> clazz, ClassLoader classLoader) {
        // Get all of the methods declared in the patch dex file class
        Log.d(TAG, "fixClass begin");
        Method[] methods = clazz.getDeclaredMethods();
        MethodReplace methodReplace;
        String clz;
        String meth;
        Log.d(TAG, "got all declared methods");
        for (Method method : methods) {
            Log.d(TAG, "parse all method");
            // Get method replace information
            methodReplace = method.getAnnotation(MethodReplace.class);
            Log.d(TAG, "get annotation of method to be replace, methodReplace is null? " + (methodReplace == null));
            if (methodReplace == null)
                // no annotation in this method
                continue;
            // clz is the name of class which contains the method to be replaced
            clz = methodReplace.clazz();
            Log.d(TAG, "Name of the class to replace is " + clz);
            // meth is the name of method which is to be replaced
            meth = methodReplace.method();
            Log.d(TAG, "Name of the method to replace is " + meth);
            if (!isEmpty(clz) && !isEmpty(meth)) {
                // method is the memory object belonging to the method that will be replaced
                replaceMethod(classLoader, clz, meth, method);
            }
        }
    }

    /**
     * fix_from_memory, real work load.
     *
     * @param patchFile
     *              download binary patch file
     * @param classLoader
     *              classloader of class that will be fixed
     * @param classes
     *              classes in white list, which will not be fixed
     */

    public synchronized void fix_from_memory(byte[] patchFile, ClassLoader classLoader,
                                             List<String> classes){
        try{
            Log.d("Moran", "Patch content length is: " + patchFile.length);
            Object mCookie = memoryLoadDexFile(patchFile, 0, patchFile.length);

            ClassLoader patchClassLoader = new ClassLoader(classLoader) {
                @Override
                protected Class<?> findClass(String className)
                        throws ClassNotFoundException {
                   CookieClassLoader cookieClassLoader = new CookieClassLoader(className, this);
                    Class<?> clazz = cookieClassLoader.loadClass(mCookie);

                    if (clazz == null && className.startsWith("com.moran.andfix")) {
                        return Class.forName(className);// annotation’s class
                        // not found
                    }
                    if (clazz == null) {
                        throw new ClassNotFoundException(className);
                    }
                    return clazz;
                }
            };

            // load class one by one
            Log.d(TAG, "begin to parse mCookie");
            Enumeration<String> entrys = new DFEnum(mCookie);
            Class<?> clazz = null;
            while (entrys.hasMoreElements()) {
                String entry = entrys.nextElement();
                if (classes != null && !classes.contains(entry)) {
                    continue;// skip, not need fix
                }

                CookieClassLoader cookieClassLoader = new CookieClassLoader(entry, patchClassLoader);
                clazz = cookieClassLoader.loadClass(mCookie);

                if (clazz != null) {
                    fixClass(clazz, classLoader);
                }
            }

        } catch (Exception e){
            Log.e(TAG, "patch", e);
        }
    }

    /**
     * replace method
     *
     * @param classLoader classloader
     * @param clz string name of target class
     * @param meth name of target method
     * @param method source method
     */
    private void replaceMethod(ClassLoader classLoader, String clz,
                               String meth, Method method) {
        try {
            String key = clz + "@" + classLoader.toString();
            Log.d(TAG, "[replaceMethod]: key is " + key);
            Class<?> clazz = mFixedClass.get(key);
            if (clazz == null) {// class not load
                Log.d(TAG, "[replaceMethod]: clazz is not null, begin to load " + clz);
                Class<?> clzz = classLoader.loadClass(clz);
                // initialize target class
                Log.d(TAG, "[replaceMethod]: clzz load finished, begin to init clzz");
                clazz = AndFix.initTargetClass(clzz);
            }
            if (clazz != null) {// initialize class OK
                mFixedClass.put(key, clazz);
                // src is the original class that loaded in memory
                Log.d(TAG, "[replaceMethod]: plan to load method: " + meth);
                Method src = clazz.getDeclaredMethod(meth, method.getParameterTypes());
                // src: original class; method: patch method;
                Log.d(TAG, "[replaceMethod]: save the patch and target method");
                mPatchMethod.add(method);
                mOriginalMethod.add(src);
                Log.d(TAG, "[replaceMethod]: begin to replace method");
                AndFix.addReplaceMethod(src, method);
            }
        } catch (Exception e) {
            Log.e(TAG, "replaceMethod", e);
        }
    }

    public void uninstallAll(){
        Log.d(TAG, "begin to uninstall");
        for(int i = 0; i < mOriginalMethod.size(); i++){
            try{
                AndFix.uninstallMethod(mPatchMethod.get(i), mOriginalMethod.get(i));
            } catch (Exception e){
                Log.e(TAG, "[uninstallAll]", e);
            }
        }
    }


    private static boolean isEmpty(String string) {
        return string == null || string.length() <= 0;
    }

}
