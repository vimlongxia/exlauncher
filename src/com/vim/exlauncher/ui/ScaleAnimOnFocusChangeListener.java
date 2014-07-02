package com.vim.exlauncher.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.vim.exlauncher.R;

public class ScaleAnimOnFocusChangeListener implements OnFocusChangeListener{
    private static final String TAG = "ScalAnimOnFocusChangeListener";
    
    private Animation mFocusAnimation;
    private Animation mUnfocusAnimation;
    
    public ScaleAnimOnFocusChangeListener(Context context){
        mFocusAnimation = (AnimationSet) AnimationUtils.loadAnimation(context,
                R.anim.anim_focus);

        mUnfocusAnimation = (AnimationSet) AnimationUtils.loadAnimation(context,
                R.anim.anim_unfocus);
    }
    
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        Log.d(TAG, "[mBottomButtonOnFocusChangeListener] view : "
                + view + ", hasFocus : " + hasFocus);
        view.startAnimation(hasFocus ? mFocusAnimation
                : mUnfocusAnimation);
    }
}
