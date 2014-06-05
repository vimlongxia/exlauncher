package com.vim.exlauncher.ui;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.vim.exlauncher.R;

public class ImageButtonOnClickListener implements OnClickListener {
    private static final String TAG = "ImageButtonOnClickListener";
    private Context mContext;
    private Toast mToast;
    
    public ImageButtonOnClickListener(Context context) {
        mContext = context;
    }
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
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
            intent.setComponent(new ComponentName("com.google.android.youtube",
                    "com.google.android.youtube.app.honeycomb.Shell$HomeActivity"));
            break;
        }
        
        try {
            mContext.startActivity(intent);
        } catch(ActivityNotFoundException anf){
            Log.e(TAG, "Activity not found!");
            anf.printStackTrace();
            
            if (mToast != null){
                mToast.cancel();
            }
            mToast = Toast.makeText(mContext, "Application Not Found!", Toast.LENGTH_SHORT);
            mToast.show();
        }
        
    }

}
