package com.vim.exlauncher.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.vim.exlauncher.R;

public class GridItemOnKeyListener implements OnKeyListener {
    private static final String TAG = "GridItemOnKeyListener";

    private Context mContext;
    private Intent mIntent;
    private ViewFlipper mFlipper;

    private Animation mAnimLeftIn;
    private Animation mAnimLeftOut;
    private Animation mAnimRightIn;
    private Animation mAnimRightOut;

    public GridItemOnKeyListener(Context context, Intent intent, ViewFlipper viewFlipper) {
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

    private void processLeftKey(View view) {
        synchronized (AllGroupsActivity.BoundaryObj) {
            if (isNextItemNull(view, View.FOCUS_LEFT)) {
                AllGroupsActivity.sBoundaryCount = 0;
                mFlipper.setInAnimation(mAnimLeftIn);
                mFlipper.setOutAnimation(mAnimLeftOut);
                mFlipper.showPrevious();
            }
        }
    }

    private void processRightKey(View view) {
        synchronized (AllGroupsActivity.BoundaryObj) {
            if (isNextItemNull(view, View.FOCUS_RIGHT)) {
                AllGroupsActivity.sBoundaryCount = 0;
                mFlipper.setInAnimation(mAnimRightIn);
                mFlipper.setOutAnimation(mAnimRightOut);
                mFlipper.showNext();
            }
        }
    }

    private boolean isNextItemNull(View view, int dec) {
        ViewGroup gridLayout = (ViewGroup) view.getParent();
        View nextView = FocusFinder.getInstance().findNextFocus(gridLayout,
                gridLayout.findFocus(), dec);
        logd("[isNextItemNull] view : " + view + ", nextView : " + nextView + ", dec : " + dec);
        if (nextView == null) {
            AllGroupsActivity.sBoundaryCount++;
        } else {
            AllGroupsActivity.sBoundaryCount = 0;
        }

        return (AllGroupsActivity.sBoundaryCount > 1);
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        logd("[onKey] keyCode : " + keyCode + ", event : " + event);

        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            if (mIntent != null) {
                mContext.startActivity(mIntent);
            }
            return true;

        case KeyEvent.KEYCODE_DPAD_LEFT:
            processLeftKey(view);
            return true;

        case KeyEvent.KEYCODE_DPAD_RIGHT:
            processRightKey(view);
            return true;

        default:
            logd("[onKey] this keyCode has been discarded");
            break;
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
