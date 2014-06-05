package com.vim.exlauncher.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

public class LauncherUtils {
    private static final String TAG = "LauncherUtils";

    public static void saveBitmapFile(Context context, Bitmap bitmap, String fileName) {
        if (bitmap == null){
            Log.e(TAG, "[saveBitmapFile] bitmap is empty!");
            return;
        }
        
        if (TextUtils.isEmpty(fileName)){
            Log.e(TAG, "[saveBitmapFile] fileName must not be empty!");
            return;
        }
        
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Activity.MODE_PRIVATE);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            bitmap.compress(CompressFormat.PNG, 80, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException ffe) {
            Log.e(TAG, "[saveBitmapFile] FileNotFoundException!");
            ffe.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "[saveBitmapFile] Exception!");
            e.printStackTrace();
        }
    }
    
    public static Bitmap loadBitmap(Context context, String fileName){
        if (TextUtils.isEmpty(fileName)){
            Log.e(TAG, "[loadBitmap] fileName must not be empty!");
            return null;
        }
        
        Bitmap bitmap = null;
        try {
            FileInputStream inputStream = context.openFileInput(fileName);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bitmap = BitmapFactory.decodeStream(bis);
        } catch (FileNotFoundException ffe) {
            Log.e(TAG, "[loadBitmap] FileNotFoundException!");
            ffe.printStackTrace();
            bitmap = null;
        } catch (Exception e) {
            Log.e(TAG, "[loadBitmap] Exception!");
            e.printStackTrace();
            bitmap = null;
        }
        
        return bitmap;
    }
}
