package com.vim.exlauncher.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.JsonAdData;

public class ImageButtonOnClickListener implements OnClickListener {
    private static final String TAG = "ImageButtonOnClickListener";
    private Context mContext;
    private Toast mToast;
    private Map<String, String> mKeyUrlMap;

    public ImageButtonOnClickListener(Context context) {
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Log.i(TAG, "[onClick] view : " + v);
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        String url = null;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        switch (v.getId()) {
        case R.id.ib_apps:
            intent.setComponent(new ComponentName("com.vim.exlauncher",
                    "com.vim.exlauncher.ui.AllApps3D"));
            break;

        case R.id.ib_drama:
            intent.setComponent(new ComponentName("com.sharebox.Drama",
                    "com.sharebox.iptvCore.activities.MainActivity"));
            break;

        case R.id.ib_games:
            intent.setComponent(new ComponentName("com.vim.exlauncher",
                    "com.vim.exlauncher.ui.GamesActivity"));
            break;

        case R.id.ib_movies:
            intent.setComponent(new ComponentName("com.farcore.videoplayer",
                    "com.farcore.videoplayer.FileList"));
            break;

        case R.id.ib_radio:
            intent.setComponent(new ComponentName("com.sharebox.Radio",
                    "com.sharebox.iptvCore.activities.MainActivity"));
            break;

        case R.id.ib_setting:
            intent.setComponent(new ComponentName("com.android.settings",
                    "com.android.settings.Settings"));
            break;

        case R.id.ib_tv:
            intent.setComponent(new ComponentName("com.sharebox.fitvLive",
                    "com.sharebox.iptvCore.activities.MainActivity"));
            break;

        case R.id.ib_youtube:
            intent.setComponent(new ComponentName(
                    "com.google.android.youtube.googletv",
                    "com.google.android.youtube.googletv.MainActivity"));
            break;

        case R.id.ib_lh_first:
            String pvadvKey = ExLauncher.getCurrentPVAdvKey();
            Log.d(TAG, "ib_lh_first pvadvKey : " + pvadvKey);

            String pvadvUrlKey = mKeyUrlMap.get(pvadvKey);
            Log.d(TAG, "ib_lh_first pvadvUrlKey : " + pvadvUrlKey);
            if (TextUtils.isEmpty(pvadvUrlKey)) {
                Log.e(TAG, "pvadvUrlKey is empty!");
                return;
            }

            url = sp.getString(pvadvUrlKey, null);
            if (TextUtils.isEmpty(url)) {
                Log.e(TAG, "url is empty!");
                return;
            }

            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "video/*");
            break;

        case R.id.ib_lh_second:
            String advKey = ExLauncher.getCurrentAdvKey();
            String advUrlKey = mKeyUrlMap.get(advKey);
            if (TextUtils.isEmpty(advUrlKey)) {
                Log.e(TAG, "advUrlKey is empty!");
                return;
            }

            url = sp.getString(advUrlKey, null);
            if (TextUtils.isEmpty(url)) {
                Log.e(TAG, "url is empty!");
                return;
            }

            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            break;

        case R.id.ib_lh_third:
            url = sp.getString(JsonAdData.S_URL_AD1, null);
            if (TextUtils.isEmpty(url)) {
                Log.e(TAG, "url is empty!");
                return;
            }

            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            break;

        case R.id.ib_lh_forth:
            url = sp.getString(JsonAdData.S_URL_AD2, null);
            if (TextUtils.isEmpty(url)) {
                Log.e(TAG, "url is empty!");
                return;
            }

            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            break;

        case R.id.ib_lh_fifth:
            url = sp.getString(JsonAdData.S_URL_AD3, null);
            if (TextUtils.isEmpty(url)) {
                Log.e(TAG, "url is empty!");
                return;
            }

            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            break;

        case R.id.ib_lh_sixth:
            url = sp.getString(JsonAdData.S_URL_AD4, null);
            if (TextUtils.isEmpty(url)) {
                Log.e(TAG, "url is empty!");
                return;
            }

            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            break;

        case R.id.ib_lh_seventh:
            url = sp.getString(JsonAdData.S_URL_AD5, null);
            if (TextUtils.isEmpty(url)) {
                Log.e(TAG, "url is empty!");
                return;
            }

            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
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
}
