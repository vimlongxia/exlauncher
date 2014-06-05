package com.vim.exlauncher.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

public class LauncherUtils {
    private static final String TAG = "LauncherUtils";

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
