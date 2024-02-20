package com.moran.hotfixdemo.util;

import android.content.Context;

import com.moran.andfix.patch.PatchManager;

/**
 * @FunctionName PatchManager
 * @Author name
 * @Date 12/10/22
 * @Description
 */

public class PatchManagerUtil {
    private static PatchManagerUtil mInstance = null;
    private static PatchManager mPatchManager = null;

    public static PatchManagerUtil getInstance(){
        if (mInstance == null){
            synchronized (PatchManagerUtil.class){
                if (mInstance == null){
                    mInstance = new PatchManagerUtil();
                }
            }
        }
        return mInstance;
    }

    public void initPatch(Context context){
        mPatchManager = new PatchManager(context);
        mPatchManager.init(Utils.getVersionName(context));
//        mPatchManager.loadPatch();
    }

    public void uninstallPatch(){
        try{
            if(mPatchManager != null){
                mPatchManager.uninstallPatch();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addPatch(String path){
        try{
            if (mPatchManager != null){
                mPatchManager.addPatch(path);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
