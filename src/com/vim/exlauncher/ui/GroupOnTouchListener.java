package com.vim.exlauncher.ui;

import com.vim.exlauncher.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class GroupOnTouchListener implements OnTouchListener {
    private final static String TAG = "GroupOnTouchListener";

    private Context mContext;
    private Intent mIntent;
    private ViewFlipper mFlipper;

    private Animation mAnimLeftIn;
    private Animation mAnimLeftOut;
    private Animation mAnimRightIn;
    private Animation mAnimRightOut;

    public GroupOnTouchListener(Context context, Intent intent,
            ViewFlipper viewFlipper) {
        mContext = context;
        mIntent = intent;
        mFlipper = viewFlipper;

        mAnimLeftIn = AnimationUtils.loadAnimation(mContext,
                R.anim.push_left_in);
        mAnimLeftOut = AnimationUtils.loadAnimation(mContext,
                R.anim.push_left_out);
        mAnimRightIn = AnimationUtils.loadAnimation(mContext,
                R.anim.push_right_in);
        mAnimRightOut = AnimationUtils.loadAnimation(mContext,
                R.anim.push_right_out);
    }

    public boolean onTouch(View view, MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mContext.startActivity(mIntent);
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ImageView img = (ImageView) ((ViewGroup) view).getChildAt(0);
            String path = img.getResources().getResourceName(img.getId());
            String vName = path.substring(path.indexOf("/") + 1);

            if (vName.equals("img_video") || vName.equals("img_recommend")
                    || vName.equals("img_app") || vName.equals("img_music")
                    || vName.equals("img_local")) {
                return view.onTouchEvent(event);
            }
        }

        return false;
    }
}
