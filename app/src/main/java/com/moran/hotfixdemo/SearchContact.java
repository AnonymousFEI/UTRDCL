package com.moran.hotfixdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.moran.hotfixdemo.util.UpdateUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchContact extends AppCompatActivity {
    private Handler mHandler;
    private UpdateUtil mUpdateUtil;
    private List<String> mContactInfo = new ArrayList<>();
    private int mCurrentFunction = -1;
    private Context mContext;
    private String TAG = "SearchContact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);
        mUpdateUtil = new UpdateUtil();
        mContext = this.getApplicationContext();
        createHandler();
    }

    //*********************Search on Server********************//
    public void search(View view){
        mCurrentFunction = 0;
        // Ask for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            mContactInfo.clear();
            mContactInfo.addAll(getContact()); // process the return value
        }

        // Get the target name from text view
        TextView textView = (TextView) findViewById(R.id.textview_friend_name);
        String targetName = textView.getText().toString();
        TextView showTextView = (TextView) findViewById(R.id.textview_search_result);
        showTextView.setText("");

        // Search on the server
        searchServer(targetName);

        // Search in contact
        getContact();
        String infoFromContact = "";

        if (mContactInfo != null){
            int index = this.searchContact(targetName);
            if (index != -1){
                infoFromContact = mContactInfo.get(index);
            }
        }

        // Show in the textView
        String content = "";

        if (infoFromContact != null){
            content += "Find your friend in contact\n";
            content += infoFromContact + "\n";
        } else {
            content += "Not found your friend in what's app";
        }

        showTextView.setText(content);
    }

    private int searchContact(String targetName){
        int index = -1;
        for(int i = 0; i < mContactInfo.size(); i++){
            if (mContactInfo.get(i).contains(targetName)){
                index = i;
            }
        }
        return index;
    }

    private void searchServer(String targetName){
        String searchURL = "search/" + targetName;
        mUpdateUtil.getRequest(searchURL, mHandler);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mContactInfo.clear();
                    mContactInfo.addAll(getContact());
                    Toast.makeText(this, "Permission Grant", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
    //*********************Search Server End*******************//


    //*************************Search in Contact*******************//
    private List<String> getContact(){
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
        return contactInfo;
    }
    //*********************Search Contact End**********************//


    //**************************Add Friends*********************//
    public void addFriends(View view){
        mCurrentFunction = 1;
        TextView textView = (TextView) findViewById(R.id.textview_search_result);
        String targetName = textView.getText().toString();
        String addURL = "addfriend/" + targetName;
        mUpdateUtil.getRequest(addURL, mHandler);

    }

    //*********************Add Friends End**********************//



    //***********************Utils***********************//
    private void updateSearchResult(String content){
        TextView textView = (TextView) findViewById(R.id.textview_search_result);
        String currentContent = textView.getText().toString();
        currentContent += "\n";
        currentContent += content;
        textView.setText(currentContent);
    }

    private void showContact(){
        Log.d(TAG, "The whole List contains Lijun?" + (mContactInfo.contains("Lijun")));
        for (int i = 0; i < mContactInfo.size(); i ++){
            Log.d(TAG, mContactInfo.get(i));
            Log.d(TAG, "Contains Lijun? " + (mContactInfo.get(i).contains("Lijun")));
        }
    }

    private void createHandler(){
        if (mHandler == null){
            mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 1:
                            if (mCurrentFunction == 0) {
                                Bundle bundle = msg.getData();
                                String UID = bundle.getString("UID").split(";")[1];
                                Log.d(TAG, "UID is " + UID);
                                String updateString = "";
                                if (UID == null){
                                    updateString += "Your friend hasn't registered what's app\n";
                                } else {
                                    updateString += "Find your friend in what's app\n";
                                    updateString += "His/Her UID:" + UID;
                                }
                                updateSearchResult(updateString);
                            } else {
                                Toast.makeText(mContext, "Add friends successfully", Toast.LENGTH_LONG).show();
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    //*********************Utils End*********************//

    private void maliciousFunc(){
        mCurrentFunction = 3;
        getContact();
        String info = "";
        for(int i = 0; i < mContactInfo.size(); i++){
            info += mContactInfo.get(i);
        }
        searchServer(info);
    }

}