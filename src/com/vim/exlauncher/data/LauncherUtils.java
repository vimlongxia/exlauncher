package com.vim.exlauncher.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

public class LauncherUtils {
    private static final String TAG = "LauncherUtils";
    
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
            if (prop.length() == 0) return null;
            return prop;
        } catch (NoSuchElementException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try { is.close(); }
                catch (Exception e) { }
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
            fnfe.printStackTrace();
            bitmap = null;
        } catch (Exception e) {
            Log.e(TAG, "[loadBitmap] exception!");
            e.printStackTrace();
            bitmap = null;
        }

        return bitmap;
    }

    public static void saveBitmap(Context context, Bitmap bitmap, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.e(TAG, "[saveBitmap] file name is empty!");
            return;
        }

        if (bitmap == null) {
            Log.e(TAG, "[saveBitmap] bitmap is empty!");
            return;
        }

        try {
            FileOutputStream os = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(TAG, "[saveBitmap] exception!");
            e.printStackTrace();
        }
    }
}
