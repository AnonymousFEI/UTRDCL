package com.moran.hotfixdemo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

/**
 * @FunctionName Utils
 * @Author name
 * @Date 12/10/22
 * @Description
 */
public class Utils {

    public static String updateURL = "";

    public static String getVersionName(Context context){
        String versionName = "1.0.0";
        try{
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;

        } catch (Exception e){
            e.printStackTrace();
        }

        return versionName;
    }

    public static void printLog() {
        String error = null;
//        String error = "hello Moran";
        Log.e("Moran", error);
    }

    public static byte[] encrypt(byte[] rawData){
        byte[] encryptedBytes = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // 加载密钥到KeyStore
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            ks.setKeyEntry("alias", privateKey, null, new Certificate[]{});
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("alias", null);
            PrivateKey loadedPrivateKey = privateKeyEntry.getPrivateKey();
            PublicKey loadedPublicKey = privateKeyEntry.getCertificate().getPublicKey();

            // 加密和解密
            byte[] originalBytes = "hello world".getBytes("UTF-8");

            // 使用公钥加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedBytes = cipher.doFinal(originalBytes);

            // 使用私钥解密
            cipher.init(Cipher.DECRYPT_MODE, loadedPrivateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            String decryptedString = new String(decryptedBytes, "UTF-8");
            Log.i("Util", "Decrypted Data: " + decryptedString);
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException |
                UnrecoverableEntryException | NoSuchPaddingException | InvalidKeyException | BadPaddingException |
                IllegalBlockSizeException e){
            e.printStackTrace();
        }
        return encryptedBytes;
    }

//    public static byte[] encrypt(byte[] rawData, byte[] key) throws Exception{
//        Cipher cipher = Cipher.getInstance("RSA/CBC/PKCS5Padding");
//        IvParameterSpec ivSpec = new IvParameterSpec(new byte[]{});
//        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
//    }
}
