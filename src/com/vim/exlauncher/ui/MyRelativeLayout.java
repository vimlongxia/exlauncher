package com.vim.exlauncher.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class MyRelativeLayout extends RelativeLayout {
    private static final String TAG = "MyRelativeLayout";
    private Context mContext;

    private static final int ANIM_DURATION = 110;

    public MyRelativeLayout(Context context) {
        super(context);
        mContext = context;
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return super.onTouchEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
        logd("[onFocusChanged] this : " + this + ", gainFocus : " + gainFocus
                + ", direction : " + direction);
        final AnimationSet animFocus = (AnimationSet) AnimationUtils
                .loadAnimation(mContext, R.anim.anim_focus);
        final AnimationSet animUnFocus = (AnimationSet) AnimationUtils
                .loadAnimation(mContext, R.anim.anim_unfocus);
        
        if (gainFocus) {
            // ScaleAnimation anim = new ScaleAnimation(1.1f, 1f, 1.1f,
            // 1f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            // 0.5f);
            // anim.setZAdjustment(Animation.ZORDER_TOP);
            // anim.setDuration(ANIM_DURATION);
            this.startAnimation(animFocus);
        } else {
            this.startAnimation(animUnFocus);
        }
    }
    */

    private static void logd(String strs) {
        Log.d(TAG, strs + " --- VIM");
    }
}
