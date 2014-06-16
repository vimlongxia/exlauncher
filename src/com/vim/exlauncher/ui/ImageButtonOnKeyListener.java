package com.vim.exlauncher.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.JsonAdData;

public class ImageButtonOnKeyListener implements View.OnKeyListener {
    private static final String TAG = "ImageButtonOnKeyListener";

    private Context mContext;
    private Toast mToast;
    private Map<String, String> mKeyUrlMap;

    public ImageButtonOnKeyListener(Context context) {
        mContext = context;
        mKeyUrlMap = new HashMap<String, String>();

        for (int i = 1; i <= ExLauncher.MAX_AD_PIC_ROTATE_NUM; i++) {
            mKeyUrlMap.put(JsonAdData.AD_PIC_PREFIX + i,
                    JsonAdData.AD_PIC_URL_PREFIX + i);
            mKeyUrlMap.put(JsonAdData.PVAD_PIC_PREFIX + i,
                    JsonAdData.PVAD_PIC_URL_PREFIX + i);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.i(TAG, "[onKey] ACTION_DOWN return for just processing once.");
            return true;
        }

        Log.i(TAG, "[onKey] view : " + v + ", keyCode : " + keyCode);
        if ((keyCode == KeyEvent.KEYCODE_ENTER)
                || (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            String url = null;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            switch (v.getId()) {
            case R.id.ib_lh_first:
                String pvadvKey = ExLauncher.getCurrentPVAdvKey();
                Log.d(TAG, "ib_lh_first pvadvKey : " + pvadvKey);

                String pvadvUrlKey = mKeyUrlMap.get(pvadvKey);
                Log.d(TAG, "ib_lh_first pvadvUrlKey : " + pvadvUrlKey);
                if (TextUtils.isEmpty(pvadvUrlKey)) {
                    Log.e(TAG, "pvadvUrlKey is empty!");
                    return true;
                }

                url = sp.getString(pvadvUrlKey, null);
                if (TextUtils.isEmpty(url)) {
                    Log.e(TAG, "url is empty!");
                    return true;
                }

                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                break;

            case R.id.ib_lh_second:
                String advKey = ExLauncher.getCurrentAdvKey();
                String advUrlKey = mKeyUrlMap.get(advKey);
                if (TextUtils.isEmpty(advUrlKey)) {
                    Log.e(TAG, "advUrlKey is empty!");
                    return true;
                }

                url = sp.getString(advUrlKey, null);
                if (TextUtils.isEmpty(url)) {
                    Log.e(TAG, "url is empty!");
                    return true;
                }

                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                break;

            case R.id.ib_lh_third:
                url = sp.getString(JsonAdData.S_URL_AD1, null);
                if (TextUtils.isEmpty(url)) {
                    Log.e(TAG, "url is empty!");
                    return true;
                }

                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                break;

            case R.id.ib_lh_forth:
                url = sp.getString(JsonAdData.S_URL_AD2, null);
                if (TextUtils.isEmpty(url)) {
                    Log.e(TAG, "url is empty!");
                    return true;
                }

                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                break;

            case R.id.ib_lh_fifth:
                url = sp.getString(JsonAdData.S_URL_AD3, null);
                if (TextUtils.isEmpty(url)) {
                    Log.e(TAG, "url is empty!");
                    return true;
                }

                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                break;

            case R.id.ib_lh_sixth:
                url = sp.getString(JsonAdData.S_URL_AD4, null);
                if (TextUtils.isEmpty(url)) {
                    Log.e(TAG, "url is empty!");
                    return true;
                }

                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                break;

            case R.id.ib_lh_seventh:
                url = sp.getString(JsonAdData.S_URL_AD5, null);
                if (TextUtils.isEmpty(url)) {
                    Log.e(TAG, "url is empty!");
                    return true;
                }

                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                break;
            }

            try {
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException anf) {
                Log.e(TAG, "Activity not found!");
                anf.printStackTrace();

                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(mContext, R.string.app_not_found,
                        Toast.LENGTH_SHORT);
                mToast.show();
            }
        }
        return true;
    }
}
