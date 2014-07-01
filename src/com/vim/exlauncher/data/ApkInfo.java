package com.vim.exlauncher.data;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ApkInfo {
    private String mTitle;
    private Intent mIntent;
    private Drawable mIcon;
    private ComponentName mComponentName;

    public ApkInfo() {

    }

    public ApkInfo(String title, Intent intent, Drawable icon,
            ComponentName componentName) {
        mTitle = title;
        mIntent = intent;
        mIcon = icon;
        mComponentName = componentName;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public void setComponentName(ComponentName componentName) {
        mComponentName = componentName;
    }

    public String getTitle() {
        return mTitle;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }
}
