package com.moran.andfix.patch;

import android.content.Context;

/**
 * @FunctionName PatchManagerControler
 * @Author name
 * @Date 3/28/23
 * @Description
 */
public class PatchManagerControler {
    private static PatchManager mPatchManager = null;

    public PatchManagerControler(Context context, byte[] patch){
        // We only use this Class in a single thread, so do not use synchronized.
        if (mPatchManager == null){
            mPatchManager = new PatchManager(context);
        }

        if (patch != null){
            installPatch(patch);
        }
    }

    public void installPatch(byte[] patch){
        mPatchManager.loadMemoryPatch(patch);
    }

    public void uninstallPatch(){
        mPatchManager.uninstallPatch();
        mPatchManager = null;
    }
}
