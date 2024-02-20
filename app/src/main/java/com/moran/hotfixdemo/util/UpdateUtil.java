package com.moran.hotfixdemo.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.style.BulletSpan;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import com.moran.andfix.AndFixManager;
import com.moran.andfix.patch.PatchManagerControler;

/**
 * @FunctionName UpdateUtil
 * @Author name
 * @Date 3/21/23
 * @Description
 */

public class UpdateUtil {
    private String baseUrl = "http://210.28.134.73:8080/";
    private String responseContent = "";
    private final String TAG = "UpdateUtil";
    private File fileBaseDir;
    private File patchPath = new File("/data/user/0/com.moran.testaar/files/fixbug.apatch");
    private boolean enableDebug = false;
    private Context mContext;
    private PatchManagerControler mPatchManagerControler;
    private ResponseBody responseBody;
    // TODO: Set all of the embedding value as member variable

    // Setter and Getter start
    public PatchManagerControler getmPatchManagerControler(){
        return mPatchManagerControler;
    }

    public void setmPatchManagerControler(PatchManagerControler patchManagerControler){
        mPatchManagerControler = patchManagerControler;
    }

    public void setmContext(Context mContext){
        this.mContext = mContext;
    }

    public void setFileBaseDir(File baseDir){
        fileBaseDir = baseDir;
    }

    public File getFileBaseDir(){
        return fileBaseDir;
    }

    public String getResponseContent() {
        return responseContent;
    }


    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public void setPatchPath(File patchPath){
        this.patchPath = patchPath;
    }
    // Setter and Getter end



    public void fixMemoryPatch(byte[] content){
        Log.d(TAG, "begin to fix from memory");
        mPatchManagerControler = new PatchManagerControler(mContext, content);
    }

    public void uninstallPatch(){
        Log.d(TAG, "begin to uninstall the patch");
        mPatchManagerControler.uninstallPatch();
    }

    public File getPatchPath(){
        return this.patchPath;
    }

    private void showReturnResult(String message){
        Log.d("showReturnResult", message);
        setResponseContent(message);
    }

    private void saveFile(byte[] byteContent) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            Log.d("SaveFile", "Storage can be wrote");
        }
        Log.d("SaveFile", "Begin to save");
        File savePath = new File(fileBaseDir, "fixbug.apatch");
        this.setPatchPath(savePath);
        Log.d("SaveFile", "Saving path is " + savePath);
        try{
            FileOutputStream fos = new FileOutputStream(savePath);
            fos.write(byteContent);
            fos.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void networkDemo(){
        // TODO: cancel all of the log, and change the file name, file needs to be encrypted sightly.
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("RetrofitLog", "retrofitBack = " + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://210.28.134.73:8080/")
                .client(client)
                .build();

        NetworkUtil networkUtil = retrofit.create(NetworkUtil.class);


        // Rename the filename
        networkUtil.getFileString("fixbug.apatch")
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try{
                            ResponseBody responseBody = response.body();
                            Log.d("UpdateUtil", "Followings are response body bytes[]");
                            byte[] responseBytes = responseBody.bytes();
                            if (responseBytes.length > 10){
                                fixMemoryPatch(responseBody.bytes());
                            }
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("UpdateUtil", "onFailure: Network error");
                    }
                });
    }


    public void getRequest(String requestName, Handler handler){
        Retrofit retrofit;
        this.responseBody = null;

        if(this.enableDebug){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.i("RetrofitLog", "retrofitBack = " + message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(this.baseUrl)
                    .client(client)
                    .build();
        }
        else{
            retrofit = new Retrofit.Builder()
                    .baseUrl(this.baseUrl)
                    .build();
        }

        NetworkUtil networkUtil = retrofit.create(NetworkUtil.class);
        networkUtil.getFileString(requestName)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("UpdateUtil", "onResponse called");
                        responseBody = response.body();
                        Message msg = new Message();
                        if (responseBody != null){
                            msg.what = 1;
                            Bundle bundle = new Bundle();
                            try {
                                String content = responseBody.string();
                                Log.d("UpdateUtil", "respnseBody is " + content);
                                bundle.putString("UID", content);
                                msg.setData(bundle);
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        } else {
                            msg.what = 0;
                        }
                        handler.sendMessage(msg);
                        // saveFile is the handler, you can change it to whatever you like
//                            saveFile(responseBody[0].bytes());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("UpdateUtil", "onFailure: Network error");
                    }
                });

    }

    public ResponseBody getResponseBody(){
        return this.responseBody;
    }

//    public byte[] needUpdate() {
//        ResponseBody responseBody = this.getRequest("Update");
//        byte[] responseInfo = {};
//        try {
//            responseInfo = responseBody.bytes();
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//        return responseInfo;
//    }

    public boolean checkCompatibility(byte[] responseInfo){

        return false;
    }

    public Object requestModel(byte[] key){
        return new Object();
    }


    public void downloadUpdate(){
        // TODO: Complete this function
    }

    public void installUpdate(){
        // TODO: Complete this function
    }

    public boolean needPatch(){
        return false;
    }

    public void downloadPatch(){

    }

    public void installPatch(){

    }

    // The followings are malicious part, we can rename this function as device check
    public void launchMemoryFix(){

    }


}
