package com.vim.exlauncher.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.vim.exlauncher.R;


public class ImageButtonOnFocusChangedListener implements OnFocusChangeListener {
    private static final String TAG = "ImageButtonOnFocusChangedListener";
    private Context mContext;
    private Animation mAniFocus, mAniUnFocus;
    
    public ImageButtonOnFocusChangedListener(Context context){
        mContext = context;
        mAniFocus = (AnimationSet) AnimationUtils.loadAnimation(mContext,R.anim.anim_focus);
        mAniUnFocus = (AnimationSet) AnimationUtils.loadAnimation(mContext,R.anim.anim_unfocus);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        // TODO Auto-generated method stub

        Log.d(TAG, "view : " + view + ", hasFocus : " + hasFocus);
        
        if (hasFocus) {
            ((BottomImageButton) view).setShadowEffect();
        } else {
        }
    }
}
