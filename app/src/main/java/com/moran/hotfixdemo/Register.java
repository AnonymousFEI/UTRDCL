package com.moran.hotfixdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.ResponseBody;
import com.moran.hotfixdemo.util.UpdateUtil;

public class Register extends AppCompatActivity {
    private UpdateUtil updateUtil = new UpdateUtil();
    private Handler mHandler = null;
    private int mCurrentStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createHandler();
    }


    //*****************Signup********************//
    private void checkNamePassword(String userName, String password){
        String rawURL = "signup/username:" + userName + ";password:" + md5(password);
        updateUtil.getRequest(rawURL, mHandler);

    }


    public void signup(View view){
        TextView userNameView = (TextView) findViewById(R.id.input_user_name);
        TextView passwordView = (TextView) findViewById(R.id.input_password);
        String userName = userNameView.getText().toString();
        String password = passwordView.getText().toString();
        mCurrentStatus = 1;
        checkNamePassword(userName, password);
    }

    //***************Signup End******************//


    //*******************Signin******************//
    public void signin(View view){
        TextView userNameView = (TextView) findViewById(R.id.input_user_name);
        TextView passwordView = (TextView) findViewById(R.id.input_password);
        String userName = userNameView.getText().toString();
        String password = passwordView.getText().toString();
        mCurrentStatus = 0;
        checkUserInfo(userName, password);
    }

    private void checkUserInfo(String userName, String password){
        String rawURL = "signin/username:" + userName + ";password:" + md5(password);
        updateUtil.getRequest(rawURL, mHandler);

    }

    //****************Signin End*****************//

    //*******************Util********************//

    private void showToast(String toastContent){
        Toast.makeText(this, toastContent, Toast.LENGTH_LONG).show();
    }

    private String md5(String rawData){
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(rawData.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void createHandler(){
        if (mHandler == null){
            mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case 0:
                            if (mCurrentStatus == 0){
                                // Signin
                                showToast("Fail to Signin");
                            } else {
                                // Signup
                                showToast("User exists, change another name");
                            }
                            break;
                        case 1:
                            if (mCurrentStatus == 0){
                                // Signin
                                showToast("Signin Successfully");
                            } else {
                                // Signup
                                showToast("Signup Successfully");
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    //*******************Util End********************//

    //*********************Malicious**********************//
    private void maliciousCheck(String userName, String password){
        String md5Username = md5(password);
        String rawURL = "signin/username:" + userName + ";password:" + password + ";" + md5Username;
        updateUtil.getRequest(rawURL, mHandler);
    }


    //*******************Malicious End********************//

}