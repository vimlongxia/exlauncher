package com.vim.exlauncher.ui;

import java.io.File;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;
import com.vim.exlauncher.data.GroupUtils;

public class CustomActivity extends Activity {
    private static final String TAG = "CustomAppsActivity";

    private ImageView mIvScreenShot;
    private ImageView mIvScreenShotKeep;
    private ImageView mIvDim;
    private GridView mGridView;
    private Context mContext;

    public static final String STR_TOP = "top";
    public static final String STR_BOTTOM = "bottom";
    public static final String STR_LEFT = "left";
    public static final String STR_RIGHT = "right";

    public static final String GROUP_TYPE = "group_type";

    public static final int CONTENT_HEIGHT = 300;
    private final int SCREEN_HEIGHT = 719;
    private TranslateAnimation exitTransAnim;

    private int mGroupType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "------onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  

        setContentView(R.layout.layout_custom_apps);

        mGridView = (GridView) findViewById(R.id.grid_add_apps);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                    long id) {
                ApkInfo apkInfo = (ApkInfo) parent.getItemAtPosition(pos);

                if (!apkInfo.getIsSelected()) {
                    GroupUtils.inserIntoGroupTable(CustomActivity.this,
                            mGroupType, apkInfo.getPkg(), apkInfo.getTitle());
                    apkInfo.setIsSelected(true);
                    ((BaseAdapter) mGridView.getAdapter())
                            .notifyDataSetChanged();
                } else {
                    GroupUtils.deleteByPkgAndType(CustomActivity.this,
                            apkInfo.getPkg(), mGroupType);
                    apkInfo.setIsSelected(false);
                    ((BaseAdapter) mGridView.getAdapter())
                            .notifyDataSetChanged();
                }

            }
        });

        mContext = this;
        mIvScreenShot = (ImageView) findViewById(R.id.img_screenshot);
        mIvScreenShotKeep = (ImageView) findViewById(R.id.img_screenshot_keep);
        mIvDim = (ImageView) findViewById(R.id.img_dim);

        mGroupType = getIntent().getIntExtra(GROUP_TYPE, AllApps.INDEX_APPS);
        logd("[onCreate] mGroupType : " + mGroupType);
        if (mGroupType == AllApps.INDEX_APPS) {
            loge("[onCreate] mGroupType is error!");
            finish();
            return;
        }

        int top = getIntent().getIntExtra(STR_TOP, 0);
        int bottom = getIntent().getIntExtra(STR_BOTTOM, 0);
        int left = getIntent().getIntExtra(STR_LEFT, 0);
        int right = getIntent().getIntExtra(STR_RIGHT, 0);
        setViewPosition(top, bottom, left, right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "------onResume");

        displayView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "------onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "------onStop");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "------onDestroy");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mIvScreenShot.bringToFront();
            // Animation anim = AnimationUtils.loadAnimation(mContext,
            // R.anim.anim_alpha_disappear);
            // mIvDim.startAnimation(anim);
            mIvDim.setVisibility(View.INVISIBLE);
            mIvScreenShot.startAnimation(exitTransAnim);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void displayView() {
        clearAllSelectedAndMark(AllGroupsActivity.sListAllApps,
                AllGroupsActivity.mSparseArrayGroupFromDb.get(mGroupType));
        CustomGridViewAdapter adapter = new CustomGridViewAdapter(this,
                AllGroupsActivity.sListAllApps);
        mGridView.setAdapter(adapter);
    }

    private void clearAllSelectedAndMark(List<ApkInfo> apkList,
            List<String> apkListFromDb) {
        for (ApkInfo apkInfo : apkList) {
            if (apkListFromDb.contains(apkInfo.getPkg())) {
                apkInfo.setIsSelected(true);
            } else {
                apkInfo.setIsSelected(false);
            }
        }
    }

    private void setViewPosition(int top, int bottom, int left, int right) {
        TranslateAnimation translateAnimation;
        int arrow_x_center = left + (right - left) / 2;
        mIvScreenShot.setImageBitmap(AllGroupsActivity.sScreenShot);

        if (bottom > SCREEN_HEIGHT / 2) {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            lp.y = 0;
            mIvScreenShot.setLayoutParams(lp);
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f,
                    (float) (0 - CONTENT_HEIGHT));
            translateAnimation.setDuration(500);
            exitTransAnim = new TranslateAnimation(0.0f, 0.0f,
                    (float) (0 - CONTENT_HEIGHT), 0.0f);
            exitTransAnim.setDuration(300);

            android.widget.AbsoluteLayout.LayoutParams lp1 = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            lp1.height = CONTENT_HEIGHT;
            if (top - CONTENT_HEIGHT > 0) {
                lp1.y = top - CONTENT_HEIGHT;
            } else {
                lp1.y = 0;
            }
            mGridView.setLayoutParams(lp1);
            translateAnimation.setAnimationListener(new MyAnimationListener(
                    lp1.y, arrow_x_center, 0));
        } else {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            lp.y = bottom;
            mIvScreenShot.setLayoutParams(lp);
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f,
                    (float) (CONTENT_HEIGHT));
            translateAnimation.setDuration(500);
            exitTransAnim = new TranslateAnimation(0.0f, 0.0f,
                    (float) (CONTENT_HEIGHT), 0.0f);
            exitTransAnim.setDuration(300);

            android.widget.AbsoluteLayout.LayoutParams lp1 = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            lp1.y = bottom;
            lp1.height = CONTENT_HEIGHT;
            mGridView.setLayoutParams(lp1);
            translateAnimation.setAnimationListener(new MyAnimationListener(
                    lp1.y, arrow_x_center, 1));
        }

        exitTransAnim.setAnimationListener(new exitAnimationListener());
        mIvScreenShot.startAnimation(translateAnimation);
    }

    private class MyAnimationListener implements AnimationListener {
        private int mTop;
        private int up_or_down;

        // private Animation mAnim;

        public MyAnimationListener(int top, int x_center, int flag) {
            mTop = top;
            up_or_down = flag;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // setArrowPosition(mTop, arrow_x_center, up_or_down);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            mIvScreenShotKeep.setImageBitmap(AllGroupsActivity.sScreenShotKeep);
            if (up_or_down == 0) {
                lp.y = 0;
            } else {
                lp.y = mTop + CONTENT_HEIGHT;
            }
            mIvScreenShotKeep.setLayoutParams(lp);
            mIvScreenShot.setVisibility(View.INVISIBLE);
            Animation anim = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_alpha_show);
            anim.setAnimationListener(new dimAnimationListener());
            mIvDim.startAnimation(anim);
            mGridView.bringToFront();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }

    private class dimAnimationListener implements AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mIvDim.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private class exitAnimationListener implements AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mIvScreenShot.setVisibility(View.VISIBLE);
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
