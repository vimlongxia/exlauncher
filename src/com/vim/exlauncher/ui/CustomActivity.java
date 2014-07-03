package com.vim.exlauncher.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;
import com.vim.exlauncher.data.ApplicationInfo;
import com.vim.exlauncher.data.GroupUtils;

public class CustomActivity extends Activity {
    /** Called when the activity is first created. */
    private static final String TAG = "CustomAppsActivity";

    private ImageView img_screen_shot = null;
    private ImageView img_screen_shot_keep = null;
    private ImageView img_dim = null;
    private GridView gv = null;
    private Context mContext = null;

    public static final String STR_TOP = "top";
    public static final String STR_BOTTOM = "bottom";
    public static final String STR_LEFT = "left";
    public static final String STR_RIGHT = "right";

    public static final String GROUP_TYPE = "group_type";

    private File mFile;
    private String[] list_custom_apps;
    private String str_custom_apps;

    public final static String SHORTCUT_PATH = "/data/data/com.amlogic.mediaboxlauncher/shortcut.cfg";
    public final static String DEFAULT_SHORTCUR_PATH = "/system/etc/default_shortcut.cfg";
    public final static String HOME_SHORTCUT_HEAD = "Home_Shortcut:";
    public final static String VIDEO_SHORTCUT_HEAD = "Video_Shortcut:";
    public final static String RECOMMEND_SHORTCUT_HEAD = "Recommend_Shortcut:";
    public final static String MUSIC_SHORTCUT_HEAD = "Music_shortcut:";
    public final static String LOCAL_SHORTCUT_HEAD = "Local_Shortcut:";
    public static String current_shortcutHead = HOME_SHORTCUT_HEAD;
    public static final int CONTENT_HEIGHT = 300;
    private final int SCREEN_HEIGHT = 719;
    private int homeShortcutCount = 0;
    private TranslateAnimation exitTransAnim;
    
    private int mGroupType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "------onCreate");
        int top = getIntent().getIntExtra(STR_TOP, 0);
        int bottom = getIntent().getIntExtra(STR_BOTTOM, 0);
        int left = getIntent().getIntExtra(STR_LEFT, 0);
        int right = getIntent().getIntExtra(STR_RIGHT, 0);

        setContentView(R.layout.layout_custom_apps);

        gv = (GridView) findViewById(R.id.grid_add_apps);
        gv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                    long id) {
                ApkInfo apkInfo = (ApkInfo) parent.getItemAtPosition(pos);

                /*
                 * if (item.get("item_type").equals(R.drawable.item_img_exit)) {
                 * finish(); } else
                 */
                if (!apkInfo.getIsSelected()) {
                    GroupUtils.inserIntoGroupTable(CustomActivity.this, mGroupType, apkInfo.getPkg(), apkInfo.getTitle());
                    apkInfo.setIsSelected(true);
                    updateView();
                } else {
                    GroupUtils.deleteByPkgAndType(CustomActivity.this, apkInfo.getPkg(), mGroupType);
                    apkInfo.setIsSelected(false);
                    updateView();
                }

            }
        });

        mContext = this;
        img_screen_shot = (ImageView) findViewById(R.id.img_screenshot);
        img_screen_shot_keep = (ImageView) findViewById(R.id.img_screenshot_keep);
        img_dim = (ImageView) findViewById(R.id.img_dim);
        
        mGroupType = getIntent().getIntExtra(GROUP_TYPE, AllApps.INDEX_APPS);
        logd("[onCreate] mGroupType : " + mGroupType);
        if (mGroupType == AllApps.INDEX_APPS){
            loge("[onCreate] mGroupType is error!");
            finish();
            return;
        }
                
        displayView();
        setViewPosition(top, bottom, left, right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "------onResume");

        str_custom_apps = "";
        displayView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "------onPause");

        saveShortcut(SHORTCUT_PATH, str_custom_apps);
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
            img_screen_shot.bringToFront();
            Animation anim = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_alpha_disappear);
            img_dim.startAnimation(anim);
            img_dim.setVisibility(View.INVISIBLE);
            img_screen_shot.startAnimation(exitTransAnim);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void displayView() {
        CustomGridViewAdapter ad = new CustomGridViewAdapter(this,
                AllGroupsActivity.sListAllApps);
        gv.setAdapter(ad);
    }

    private void updateView() {
        ((BaseAdapter) gv.getAdapter()).notifyDataSetChanged();
    }

    private void setViewPosition(int top, int bottom, int left, int right) {
        TranslateAnimation translateAnimation;
        int arrow_x_center = left + (right - left) / 2;
        img_screen_shot.setImageBitmap(Launcher.screenShot);

        if (bottom > SCREEN_HEIGHT / 2) {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            lp.y = 0;
            img_screen_shot.setLayoutParams(lp);
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
            gv.setLayoutParams(lp1);
            translateAnimation.setAnimationListener(new MyAnimationListener(
                    lp1.y, arrow_x_center, 0));
        } else {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            lp.y = bottom;
            img_screen_shot.setLayoutParams(lp);
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
            gv.setLayoutParams(lp1);
            translateAnimation.setAnimationListener(new MyAnimationListener(
                    lp1.y, arrow_x_center, 1));
        }

        exitTransAnim.setAnimationListener(new exitAnimationListener());
        img_screen_shot.startAnimation(translateAnimation);
    }

    private String[] loadCustomApps(String path, String shortcut_head) {
        String[] list_custom_apps;
        mFile = new File(path);

        if (!mFile.exists()) {
            return null;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            String str = null;
            while ((str = br.readLine()) != null) {
                if (str.startsWith(shortcut_head)) {
                    // Log.d(TAG, "@@@@@@@@@@@@@@@@@@ get CustomApps" + str);
                    break;
                }
            }
            str_custom_apps = str.replaceAll(shortcut_head, "");
            list_custom_apps = str_custom_apps.split(";");
            homeShortcutCount = list_custom_apps.length;
        } catch (Exception e) {
            return null;
        }
        return list_custom_apps;

    }

    public void saveShortcut(String path, String str_apps) {
        mFile = new File(path);
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage().toString());
            }
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            String str = null;
            List list = new ArrayList();

            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            if (list.size() == 0) {
                list.add(HOME_SHORTCUT_HEAD);
                list.add(VIDEO_SHORTCUT_HEAD);
                list.add(RECOMMEND_SHORTCUT_HEAD);
                list.add(MUSIC_SHORTCUT_HEAD);
                list.add(LOCAL_SHORTCUT_HEAD);
            }
            // Log.d(TAG, "@@@@@@@@@@@@@ size" + list.size());

            BufferedWriter bw = new BufferedWriter(new FileWriter(mFile));
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).toString().startsWith(current_shortcutHead)) {
                    str_apps = current_shortcutHead + str_apps;
                    bw.write(str_apps);
                    // Log.d(TAG, "@@@@@@@@@@@@@@@@@@ wirte " + str_apps);
                } else {
                    bw.write(list.get(i).toString());
                    // Log.d(TAG, "@@@@@@@@@@@@@@@@@@ wirte " +
                    // list.get(i).toString());
                }
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            Log.d(TAG, "   " + e);
        }
    }

    public void getShortcutFromDefault(String srcPath, String desPath) {
        File srcFile = new File(srcPath);
        File desFile = new File(desPath);
        if (!srcFile.exists()) {
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(srcFile));
            String str = null;
            List list = new ArrayList();

            while ((str = br.readLine()) != null) {
                list.add(str);
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(mFile));
            for (int i = 0; i < list.size(); i++) {
                bw.write(list.get(i).toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            Log.d(TAG, "   " + e);
        }
    }

    private class MyAnimationListener implements AnimationListener {
        private int mTop;
        private int up_or_down;
        private int arrow_x_center;

        // private Animation mAnim;

        public MyAnimationListener(int top, int x_center, int flag) {
            mTop = top;
            up_or_down = flag;
            arrow_x_center = x_center;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // setArrowPosition(mTop, arrow_x_center, up_or_down);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            img_screen_shot_keep.setImageBitmap(Launcher.screenShot_keep);
            if (up_or_down == 0) {
                lp.y = 0;
            } else {
                lp.y = mTop + CONTENT_HEIGHT;
            }
            img_screen_shot_keep.setLayoutParams(lp);
            // mAnim.reset();
            img_screen_shot.setVisibility(View.INVISIBLE);
            Animation anim = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_alpha_show);
            anim.setAnimationListener(new dimAnimationListener());
            img_dim.startAnimation(anim);
            // img_dim.setVisibility(View.VISIBLE);
            gv.bringToFront();
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
            img_dim.setVisibility(View.VISIBLE);
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
            img_screen_shot.setVisibility(View.VISIBLE);
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
