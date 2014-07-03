package com.vim.exlauncher.ui;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ViewFlipper;

import com.vim.exlauncher.R;

public class GridViewOnKeyListener implements OnKeyListener {
    private static final String TAG = "GridViewOnKeyListener";

    private Animation mAnimLeftIn;
    private Animation mAnimLeftOut;
    private Animation mAnimRightIn;
    private Animation mAnimRightOut;

    private ViewFlipper mFlipper;

    public GridViewOnKeyListener(Context context, ViewFlipper viewFlipper) {
        mFlipper = viewFlipper;

        mAnimLeftIn = AnimationUtils
                .loadAnimation(context, R.anim.push_left_in);
        mAnimLeftOut = AnimationUtils.loadAnimation(context,
                R.anim.push_left_out);
        mAnimRightIn = AnimationUtils.loadAnimation(context,
                R.anim.push_right_in);
        mAnimRightOut = AnimationUtils.loadAnimation(context,
                R.anim.push_right_out);
    }

    private boolean processLeftKey(View view) {
        boolean ret = false;

        if (((AllGroupsActivity.getCurGridFocusIndex() % GroupGridViewAdapter.COLUMN_NUMBER) == 0)
                || (((GridView) view).getCount() == 0)) {
            mFlipper.setInAnimation(mAnimLeftIn);
            mFlipper.setOutAnimation(mAnimLeftOut);
            mFlipper.showPrevious();

            ret = true;
        }

        return ret;
    }

    private boolean processRightKey(View view) {
        boolean ret = false;

        if (((AllGroupsActivity.getCurGridFocusIndex() % GroupGridViewAdapter.COLUMN_NUMBER) == (GroupGridViewAdapter.COLUMN_NUMBER - 1))
                || (AllGroupsActivity.getCurGridFocusIndex() == (((GridView) view)
                        .getCount() - 1))
                || (((GridView) view).getCount() == 0)) {
            mFlipper.setInAnimation(mAnimRightIn);
            mFlipper.setOutAnimation(mAnimRightOut);
            mFlipper.showNext();

            ret = true;
        }

        return ret;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        logd("[onKey] view : " + view + ", keyCode : " + keyCode + ", event : "
                + event);

        logd("childs count : " + ((GridView) view).getCount());
        logd("AllGroupsActivity.getCurGridFocusIndex : "
                + AllGroupsActivity.getCurGridFocusIndex());

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (processLeftKey(view)) {
                    return true;
                } else {
                    break;
                }

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (processRightKey(view)) {
                    return true;
                } else {
                    break;
                }
            }
        }
        return view.onKeyDown(keyCode, event);
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
