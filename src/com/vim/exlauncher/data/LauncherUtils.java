package com.vim.exlauncher.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

public class LauncherUtils {
    private static final String TAG = "LauncherUtils";

    // Get the STB ethernet MacAddress
    public static String getEthMac(String filePath) {
        StringBuffer fileData = new StringBuffer(1000);
        String ethMac = null;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();

            Log.d(TAG, "[getEthMac] fileData : " + fileData.toString());
            if (!TextUtils.isEmpty(fileData.toString())) {
                ethMac = fileData.toString().toUpperCase().substring(0, 17);
            }
        } catch (FileNotFoundException fnfe) {
            Log.e(TAG, "[getEthMac] FileNotFoundException when openning "
                    + filePath);
            // fnfe.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "[getEthMac] Exception when reading " + filePath);
            // e.printStackTrace();
        }

        Log.d(TAG, "[getEthMac] ethMac : " + ethMac);
        return ethMac;
    }

    // Get the STB wifi MacAddress
    public static String getWifiMac(Context context) {
        String wifiMac = null;
        // TODO
        WifiManager wifiMgr = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr == null) {
            Log.e(TAG, "[getWifiMac] can not get WIfiManager!");
            return null;
        }

        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        if (wifiInfo == null) {
            Log.e(TAG, "[getWifiMac] can not get WifiInfo!");
            return null;
        }

        wifiMac = wifiInfo.getMacAddress();
        Log.d(TAG, "[getWifiMac] wifi mac : " + wifiMac);
        return wifiMac;
    }

    public static String getprop(String name) {
        ProcessBuilder pb = new ProcessBuilder("/system/bin/getprop", name);
        pb.redirectErrorStream(true);

        Process p = null;
        InputStream is = null;
        try {
            p = pb.start();
            is = p.getInputStream();
            Scanner scan = new Scanner(is);
            scan.useDelimiter("\n");
            String prop = scan.next();
            if (prop.length() == 0)
                return null;
            return prop;
        } catch (NoSuchElementException e) {
            return null;
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static Bitmap loadBitmap(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "[loadBitmap] file name is empty!");
            return null;
        }

        Bitmap bitmap = null;
        try {
            FileInputStream is = context.openFileInput(fileName);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException fnfe) {
            Log.e(TAG, "[loadBitmap] " + fileName + " not found!");
            // fnfe.printStackTrace();
            bitmap = null;
        } catch (Exception e) {
            Log.e(TAG, "[loadBitmap] exception!");
            // e.printStackTrace();
            bitmap = null;
        }

        return bitmap;
    }

    public static void saveBitmap(Context context, Bitmap bitmap,
            String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "[saveBitmap] file name is empty!");
            return;
        }

        if (bitmap == null) {
            Log.e(TAG, "[saveBitmap] bitmap is empty!");
            return;
        }

        try {
            FileOutputStream os = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(TAG, "[saveBitmap] exception!");
            // e.printStackTrace();
        }
    }
    
    public static void deleteFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "[saveBitmap] file name is empty!");
            return;
        }
        
        try {
            File file = new File(fileName);
            file.delete();
        } catch (Exception e) {
            Log.e(TAG, "[deleteFile] exception! e : " + e);
            // e.printStackTrace();
        }
    }
}
