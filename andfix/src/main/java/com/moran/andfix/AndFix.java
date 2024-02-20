package com.moran.andfix;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AndFix {
    private static final String TAG = "AndFix";

    static {
        try {
            Runtime.getRuntime().loadLibrary("andfix");
        } catch (Throwable e) {
            Log.e(TAG, "loadLibrary", e);
        }
    }

    private static native boolean setup(boolean isArt, int apilevel);

    private static native void replaceMethod(Method dest, Method src);

    private static native void setFieldFlag(Field field);

    private static native void testNativeFunction();

    private static native void uninstall(Method dest, Method src);

    public static void testNative(){
        Log.d(TAG, "Step into testNative function");
        testNativeFunction();
    }

    public static void uninstallMethod(Method patch, Method target){
        try{
            uninstall(patch, target);
            initFields(target.getDeclaringClass());
        } catch (Throwable e){
            Log.e(TAG, "uninstall method", e);
        }
    }

    /**
     * replace method's body
     *
     * @param src
     *            source method
     * @param dest
     *            target method
     *
     */
    public static void addReplaceMethod(Method src, Method dest) {
        try {
            replaceMethod(src, dest);
            initFields(dest.getDeclaringClass());
        } catch (Throwable e) {
            Log.e(TAG, "addReplaceMethod", e);
        }
    }

    /**
     * initialize the target class, and modify access flag of class’ fields to
     * public
     *
     * @param clazz
     *            target class
     * @return initialized class
     */
    public static Class<?> initTargetClass(Class<?> clazz) {
        try {
            Class<?> targetClazz = Class.forName(clazz.getName(), true,
                    clazz.getClassLoader());

            initFields(targetClazz);
            return targetClazz;
        } catch (Exception e) {
            Log.e(TAG, "initTargetClass", e);
        }
        return null;
    }

    /**
     * modify access flag of class’ fields to public
     *
     * @param clazz
     *            class
     */
    private static void initFields(Class<?> clazz) {
        Field[] srcFields = clazz.getDeclaredFields();
        for (Field srcField : srcFields) {
            Log.d(TAG, "modify " + clazz.getName() + "." + srcField.getName()
                    + " flag:");
            setFieldFlag(srcField);
        }
    }

    /**
     * initialize
     *
     * @return true if initialize success
     */
    public static boolean setup() {
        try {
            final String vmVersion = System.getProperty("java.vm.version");
            boolean isArt = vmVersion != null && vmVersion.startsWith("2");
            int apilevel = Build.VERSION.SDK_INT;
            return setup(isArt, apilevel);
        } catch (Exception e) {
            Log.e(TAG, "setup", e);
            return false;
        }
    }



}

