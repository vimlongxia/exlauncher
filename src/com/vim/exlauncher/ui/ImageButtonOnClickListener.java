package com.vim.exlauncher.ui;

import org.json.JSONObject;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.HttpRequest;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ImageButtonOnClickListener implements OnClickListener {
    private static final String TAG = "ImageButtonOnClickListener";
    private Context mContext;
    
    public ImageButtonOnClickListener(Context context) {
        mContext = context;
    }
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        switch (v.getId()) {
        case R.id.ib_apps:
            break;

        case R.id.ib_drama:
            break;

        case R.id.ib_games:
            break;

        case R.id.ib_movies:
            break;

        case R.id.ib_radio:
            break;

        case R.id.ib_setting:
            intent.setComponent(new ComponentName("com.android.settings",
                    "com.android.settings.Settings"));
            break;

        case R.id.ib_tv:
            break;

        case R.id.ib_youtube:
            break;

        }
        
        try {
            mContext.startActivity(intent);
        } catch(ActivityNotFoundException anf){
            Log.e(TAG, "Activity not found! anf : " + anf);
            Toast.makeText(mContext, "Application Not Found!", Toast.LENGTH_SHORT).show();
        }
        
    }

}
