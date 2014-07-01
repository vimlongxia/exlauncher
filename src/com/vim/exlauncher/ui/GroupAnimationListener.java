package com.vim.exlauncher.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class GroupAnimationListener implements AnimationListener {
    private View mView;

    public GroupAnimationListener(View view) {
        mView = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mView.clearFocus();
        mView.requestFocus();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
