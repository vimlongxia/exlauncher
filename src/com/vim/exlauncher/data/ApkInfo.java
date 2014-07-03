package com.vim.exlauncher.data;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class ApkInfo {
    private String mTitle;
    private Intent mIntent;
    private Drawable mIcon;
    private Drawable mBg;
    private ComponentName mComponentName;
    private String mPkg;
    private boolean mIsSelected;

    public ApkInfo() {

    }

    public ApkInfo(ApkInfo apkInfo) {
        mTitle = apkInfo.getTitle();
        mIntent = apkInfo.getIntent();
        mComponentName = apkInfo.getComponentName();
        mPkg = apkInfo.getPkg();
        mIcon = apkInfo.getIcon();
        mBg = apkInfo.getBg();
        mIsSelected = apkInfo.getIsSelected();
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

    public void setPkg(String pkg) {
        mPkg = pkg;
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

    public String getPkg() {
        return mPkg;
    }
    
    public void setIsSelected(boolean isSelected){
        mIsSelected = isSelected;
    }
    
    public boolean getIsSelected(){
        return mIsSelected;
    }
    
    public void setBg(Drawable bg){
        mBg = bg;
    }
    
    public Drawable getBg(){
        return mBg;
    }
}
