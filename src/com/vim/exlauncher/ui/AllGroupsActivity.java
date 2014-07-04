package com.vim.exlauncher.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;
import com.vim.exlauncher.data.ExLauncherContentProvider;
import com.vim.exlauncher.data.GroupUtils;

public class AllGroupsActivity extends Activity {
    private static final String TAG = "AllGroupsActivity";

    public static ViewFlipper mFlipper;

    public static RelativeLayout sRlFocusUnit;
    public static ImageView sIvFocusBg;
    public static ImageView sIvFrame;

    // private RelativeLayout mRlGroupVideo;
    // private RelativeLayout mRlGroupGames;
    // private RelativeLayout mRlGroupApp;
    // private RelativeLayout mRlGroupMusic;
    // private RelativeLayout mRlGroupLocal;

    private ScrollView mSvVideo;
    private ScrollView mSvGames;
    private ScrollView mSvApp;
    private ScrollView mSvMusic;
    private ScrollView mSvLocal;
    private SparseArray<ScrollView> mSparseArrayGroupScrollView = new SparseArray<ScrollView>();

    private GroupGridLayout mGridLayoutGroupVideo;
    private GroupGridLayout mGridLayoutGroupGames;
    private GroupGridLayout mGridLayoutGroupApp;
    private GroupGridLayout mGridLayoutGroupMusic;
    private GroupGridLayout mGridLayoutGroupLocal;
    private SparseArray<GroupGridLayout> mSparseArrayGroupGridLayout = new SparseArray<GroupGridLayout>();

    private GroupGridView mGridViewGroupVideo;
    private GroupGridView mGridViewGroupGames;
    private GroupGridView mGridViewGroupApp;
    private GroupGridView mGridViewGroupMusic;
    private GroupGridView mGridViewGroupLocal;
    private SparseArray<GroupGridView> mSparseArrayGroupGridView = new SparseArray<GroupGridView>();

    public static final String START_TO_GAME = "start_to_game";

    // the following groups means that we are in the corresponding sub page
    private static final int INDEX_VIDEO = 0;
    private static final int INDEX_GAMES = 1;
    private static final int INDEX_APPS = 2;
    private static final int INDEX_MUSIC = 3;
    private static final int INDEX_LOCAL = 4;
    private static final int INDEX_MAX_SIZE = INDEX_LOCAL + 1;

    public static final List<ApkInfo> sListAllApps = new ArrayList<ApkInfo>();

    private final List<ApkInfo> mListGroupVideo = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupGames = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupApp = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupMusic = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupLocal = new ArrayList<ApkInfo>();
    private final SparseArray<List<ApkInfo>> mSparseArrayGroupAllApp = new SparseArray<List<ApkInfo>>();

    private final List<String> mListGroupVideoFromDb = new ArrayList<String>();
    private final List<String> mListGroupGamesFromDb = new ArrayList<String>();
    private final List<String> mListGroupMusicFromDb = new ArrayList<String>();
    private final List<String> mListGroupLocalFromDb = new ArrayList<String>();
    public static final SparseArray<List<String>> mSparseArrayGroupFromDb = new SparseArray<List<String>>();

    public static int sBoundaryCount;
    public static final Object BoundaryObj = new Object();

    private static final int SCREEN_HEIGHT = 719;
    private static final int CONTENT_HEIGHT = 300;
    public static Bitmap sScreenShot;
    public static Bitmap sScreenShotKeep;

    private static int sCurGridFocusIndex;
    private static int sCurGridPage;

    private static Activity mInstance;

    static {
        sBoundaryCount = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_group);

        initView();
        initHashMapGroupAllApp();
        initHashMapGroupFromDb();

        sCurGridFocusIndex = 0;

        reLoadAllData();

        mInstance = this;
    }

    public static Activity getAllGroupActivity() {
        return mInstance;
    }

    private void initView() {
        mFlipper = (ViewFlipper) findViewById(R.id.menu_flipper);

        sRlFocusUnit = (RelativeLayout) findViewById(R.id.rl_focus_unit);
        sIvFocusBg = (ImageView) findViewById(R.id.iv_focus_bg);
        sIvFrame = (ImageView) findViewById(R.id.iv_frame);

        // mRlGroupVideo = (RelativeLayout)
        // findViewById(R.id.menu_layout_video);
        // mRlGroupGames = (RelativeLayout)
        // findViewById(R.id.menu_layout_games);
        // mRlGroupApp = (RelativeLayout) findViewById(R.id.menu_layout_app);
        // mRlGroupMusic = (RelativeLayout)
        // findViewById(R.id.menu_layout_music);
        // mRlGroupLocal = (RelativeLayout)
        // findViewById(R.id.menu_layout_local);

        // gridlayout init start
        mSvVideo = (ScrollView) findViewById(R.id.sv_video);
        mSvGames = (ScrollView) findViewById(R.id.sv_games);
        mSvApp = (ScrollView) findViewById(R.id.sv_app);
        mSvMusic = (ScrollView) findViewById(R.id.sv_music);
        mSvLocal = (ScrollView) findViewById(R.id.sv_local);

        mSparseArrayGroupScrollView.put(INDEX_APPS, mSvVideo);
        mSparseArrayGroupScrollView.put(INDEX_LOCAL, mSvGames);
        mSparseArrayGroupScrollView.put(INDEX_MUSIC, mSvApp);
        mSparseArrayGroupScrollView.put(INDEX_GAMES, mSvMusic);
        mSparseArrayGroupScrollView.put(INDEX_VIDEO, mSvLocal);

        mGridLayoutGroupVideo = (GroupGridLayout) findViewById(R.id.gv_shortcut_video);
        mGridLayoutGroupGames = (GroupGridLayout) findViewById(R.id.gv_shortcut_games);
        mGridLayoutGroupApp = (GroupGridLayout) findViewById(R.id.gv_shortcut_app);
        mGridLayoutGroupMusic = (GroupGridLayout) findViewById(R.id.gv_shortcut_music);
        mGridLayoutGroupLocal = (GroupGridLayout) findViewById(R.id.gv_shortcut_local);

        mSparseArrayGroupGridLayout.put(INDEX_APPS, mGridLayoutGroupApp);
        mSparseArrayGroupGridLayout.put(INDEX_LOCAL, mGridLayoutGroupLocal);
        mSparseArrayGroupGridLayout.put(INDEX_MUSIC, mGridLayoutGroupMusic);
        mSparseArrayGroupGridLayout.put(INDEX_GAMES, mGridLayoutGroupGames);
        mSparseArrayGroupGridLayout.put(INDEX_VIDEO, mGridLayoutGroupVideo);
        // gridlayout init end

        // gridview init start
        mGridViewGroupVideo = (GroupGridView) findViewById(R.id.gv_video);
        mGridViewGroupGames = (GroupGridView) findViewById(R.id.gv_games);
        mGridViewGroupApp = (GroupGridView) findViewById(R.id.gv_app);
        mGridViewGroupMusic = (GroupGridView) findViewById(R.id.gv_music);
        mGridViewGroupLocal = (GroupGridView) findViewById(R.id.gv_local);

        mSparseArrayGroupGridView.put(INDEX_APPS, mGridViewGroupApp);
        mSparseArrayGroupGridView.put(INDEX_LOCAL, mGridViewGroupLocal);
        mSparseArrayGroupGridView.put(INDEX_MUSIC, mGridViewGroupMusic);
        mSparseArrayGroupGridView.put(INDEX_GAMES, mGridViewGroupGames);
        mSparseArrayGroupGridView.put(INDEX_VIDEO, mGridViewGroupVideo);
        // gridview init end

        if (getIntent().getBooleanExtra(START_TO_GAME, false)) {
            mFlipper.setDisplayedChild(INDEX_GAMES);
            mFlipper.getChildAt(INDEX_GAMES).requestFocus();
            setCurGridPage(INDEX_GAMES);
        } else {
            mFlipper.setDisplayedChild(INDEX_APPS);
            mFlipper.getChildAt(INDEX_APPS).requestFocus();
            setCurGridPage(INDEX_APPS);
        }
    }

    private void setScrollViewVisibility(int visibility) {
        for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
            mSparseArrayGroupScrollView.get(i).setVisibility(visibility);
        }
    }

    private void setGroupGridViewVisibility(int visibility) {
        for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
            mSparseArrayGroupGridView.get(i).setVisibility(visibility);
        }
    }

    public static void setCurGridPage(int page) {
        sCurGridPage = page;
    }

    public static int getCurGridPage() {
        return sCurGridPage;
    }

    private void initHashMapGroupAllApp() {
        mSparseArrayGroupAllApp.put(INDEX_VIDEO, mListGroupVideo);
        mSparseArrayGroupAllApp.put(INDEX_GAMES, mListGroupGames);
        mSparseArrayGroupAllApp.put(INDEX_APPS, mListGroupApp);
        mSparseArrayGroupAllApp.put(INDEX_MUSIC, mListGroupMusic);
        mSparseArrayGroupAllApp.put(INDEX_LOCAL, mListGroupLocal);
    }

    private void initHashMapGroupFromDb() {
        mSparseArrayGroupFromDb.put(INDEX_VIDEO, mListGroupVideoFromDb);
        mSparseArrayGroupFromDb.put(INDEX_GAMES, mListGroupGamesFromDb);
        mSparseArrayGroupFromDb.put(INDEX_MUSIC, mListGroupMusicFromDb);
        mSparseArrayGroupFromDb.put(INDEX_LOCAL, mListGroupLocalFromDb);
    }

    public static int getCurGridFocusIndex() {
        logd("[getCurGridFocusIndex] sCurGridFocusIndex : "
                + sCurGridFocusIndex);
        return sCurGridFocusIndex;
    }

    public static void setCurGridFocusIndex(int index) {
        logd("[setCurGridFocusIndex] index : " + index);
        sCurGridFocusIndex = index;
    }

    private void loadAppInfoByGroup(int group) {
        List<String> apkInfoList = mSparseArrayGroupFromDb.get(group);
        apkInfoList.clear();

        Cursor cursor = GroupUtils.getGroupDataByGroup(this, group);
        if ((cursor == null) || (cursor.getCount() == 0)) {
            logd("[loadAppInfoByGroup] we don't get any date of this group : "
                    + group);
            return;
        }

        try {
            while (cursor.moveToNext()) {
                String pkg = cursor
                        .getString(cursor
                                .getColumnIndex(ExLauncherContentProvider.TABLE_GROUP_PACKAGE));
                apkInfoList.add(pkg);
            }
        } catch (Exception e) {
            Log.e(TAG, "[loadAppInfoByGroup] error when get pkg!");
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    private void loadAllCustomGroupAppsPkgFromDb() {
        for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
            if (i == INDEX_APPS) {
                continue;
            }

            loadAppInfoByGroup(i);
        }
    }

    private void loadAppByGroupList(List<ApkInfo> storedlist,
            List<String> pkgList) {
        logd("[loadAppByGroupList] storedlist : " + storedlist + ", pkgList : "
                + pkgList);

        storedlist.clear();

        if (sListAllApps.size() == 0) {
            loge("[loadAppByGroupList] mListAllApps size 0! this should not happen!");
            return;
        }

        if (pkgList == null) {
            // this is apps list
            storedlist.addAll(sListAllApps);
        } else {
            // this is other groups
            ApkInfo allApkInfo = null;
            for (int i = 0; i < sListAllApps.size(); i++) {
                allApkInfo = sListAllApps.get(i);

                if (pkgList.contains(allApkInfo.getPkg())) {
                    logd("[loadAppByGroupList] match ! pkg : "
                            + allApkInfo.getPkg());
                    ApkInfo apkInfo = new ApkInfo(allApkInfo);
                    storedlist.add(apkInfo);
                }
            }

            // add the last item "Add"
            allApkInfo = new ApkInfo();
            allApkInfo.setTitle(getString(R.string.str_add));
            allApkInfo.setIcon(getResources().getDrawable(
                    R.drawable.item_img_add));
            storedlist.add(allApkInfo);
        }
    }

    private void loadAllGroupApps() {
        for (int i = INDEX_VIDEO; i < mSparseArrayGroupAllApp.size(); i++) {
            loadAppByGroupList(mSparseArrayGroupAllApp.get(i),
                    mSparseArrayGroupFromDb.get(i, null));
        }
    }

    public static void setPopWindow(Activity activity, int top, int bottom) {
        View view = activity.getWindow().getDecorView();
        view.layout(0, 0, 1279, SCREEN_HEIGHT);
        view.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
        view.destroyDrawingCache();

        if (bottom > SCREEN_HEIGHT / 2) {
            if (top + 3 - CONTENT_HEIGHT > 0) {
                logd("[setPopWindow] 1");
                sScreenShot = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                        top);
                sScreenShotKeep = Bitmap.createBitmap(bmp, 0, CONTENT_HEIGHT,
                        bmp.getWidth(), top + 3 - CONTENT_HEIGHT);
            } else {
                logd("[setPopWindow] 2");
                sScreenShot = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                        CONTENT_HEIGHT);
                sScreenShotKeep = null;
            }
        } else {
            logd("[setPopWindow] 3");
            sScreenShot = Bitmap.createBitmap(bmp, 0, bottom, bmp.getWidth(),
                    SCREEN_HEIGHT - bottom);
            sScreenShotKeep = Bitmap.createBitmap(bmp, 0, bottom,
                    bmp.getWidth(), SCREEN_HEIGHT - (bottom + CONTENT_HEIGHT));
        }
    }

    private void setAllLayoutView() {
        if (ExLauncher.S_GRID_LAYOUT) {
            setScrollViewVisibility(View.VISIBLE);
            setGroupGridViewVisibility(View.GONE);

            for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
                mSparseArrayGroupGridLayout.get(i).setLayoutView(
                        mSparseArrayGroupAllApp.get(i));
            }
        } else {
            setScrollViewVisibility(View.GONE);
            setGroupGridViewVisibility(View.VISIBLE);

            for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
                GroupGridView gv = mSparseArrayGroupGridView.get(i);
                final List<ApkInfo> listApk = mSparseArrayGroupAllApp.get(i);
                BaseAdapter adapter = new GroupGridViewAdapter(this, listApk);
                gv.setAdapter(adapter);
                gv.setOnItemClickListener(new GroupGridView.GridViewOnItemClickListener(
                        this, listApk));
                gv.setOnKeyListener(new GroupGridView.GridViewOnKeyListener(
                        this, AllGroupsActivity.mFlipper));
                gv.setOnItemSelectedListener(new GroupGridView.GridViewOnItemSelectedListener());
                gv.setOnFocusChangeListener(new GroupGridView.GridViewOnFocusChangeListener());
            }
        }
    }

    public static void startCustomAct(Context mContext, View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);

        setPopWindow(mInstance, rect.top - 10, rect.bottom + 10);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra(CustomActivity.STR_TOP, rect.top - 10);
        intent.putExtra(CustomActivity.STR_BOTTOM, rect.bottom + 10);
        intent.putExtra(CustomActivity.STR_LEFT, rect.left);
        intent.putExtra(CustomActivity.STR_RIGHT, rect.right);
        intent.setComponent(new ComponentName("com.vim.exlauncher",
                "com.vim.exlauncher.ui.CustomActivity"));
        intent.putExtra(CustomActivity.GROUP_TYPE, getCurGridPage());
        mContext.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setAllLayoutView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadAllApps() {
        sListAllApps.clear();

        PackageManager manager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(
                mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps == null) {
            loge("[loadAllApps] apps is null! this should not happen!");
            return;
        }

        ApkInfo apkInfo = null;
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);

            apkInfo = new ApkInfo();
            apkInfo.setTitle(info.loadLabel(manager).toString());

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName componentName = new ComponentName(
                    info.activityInfo.applicationInfo.packageName,
                    info.activityInfo.name);
            intent.setComponent(componentName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            apkInfo.setIntent(intent);

            apkInfo.setComponentName(componentName);
            apkInfo.setPkg(info.activityInfo.applicationInfo.packageName);

            Drawable icon = parseItemIcon(apkInfo.getPkg());
            if (icon != null) {
                apkInfo.setIcon(icon);
            } else {
                apkInfo.setIcon(info.activityInfo.loadIcon(manager));
            }

            apkInfo.setIsSelected(false);
            apkInfo.setBg(getResources().getDrawable(parseItemBackground(i)));

            logd("[loadAllApps] apk title : " + apkInfo.getTitle());
            sListAllApps.add(apkInfo);
        }
    }

    public static int getItemBackground(int num) {
        switch (num % 8 + 1) {
        case 1:
            return R.drawable.item_child_1;
        case 2:
            return R.drawable.item_child_2;
        case 3:
            return R.drawable.item_child_3;
        case 4:
            return R.drawable.item_child_4;
        case 5:
            return R.drawable.item_child_5;
        case 6:
            return R.drawable.item_child_6;
        case 7:
            return R.drawable.item_child_7;
        case 8:
            return R.drawable.item_child_8;
        default:
            return R.drawable.item_child_1;
        }
    }

    private Drawable parseItemIcon(String packageName) {
        int resId = -1;

        if (packageName.equals("com.fb.FileBrower")) {
            resId = R.drawable.icon_filebrowser;
        } else if (packageName.equals("com.amlogic.OOBE")) {
            resId = R.drawable.icon_oobe;
        } else if (packageName.equals("com.android.browser")) {
            resId = R.drawable.icon_browser;
        } else if (packageName.equals("com.gsoft.appinstall")) {
            resId = R.drawable.icon_appinstaller;
        } else if (packageName.equals("com.farcore.videoplayer")) {
            resId = R.drawable.icon_videoplayer;
        } else if (packageName.equals("com.aml.settings")) {
            resId = R.drawable.icon_amlsetting;
        } else if (packageName.equals("com.amlogic.mediacenter")) {
            resId = R.drawable.icon_mediacenter;
        } else if (packageName.equals("com.amlapp.update.otaupgrade")) {
            resId = R.drawable.icon_backupandupgrade;
        } else if (packageName.equals("com.android.gallery3d")) {
            resId = R.drawable.icon_pictureplayer;
        } else if (packageName.equals("com.amlogic.netfilebrowser")) {
            resId = R.drawable.icon_networkneiborhood;
        } else if (packageName.equals("st.com.xiami")) {
            resId = R.drawable.icon_xiami;
        } else if (packageName.equals("com.android.providers.downloads.ui")) {
            resId = R.drawable.icon_download;
        } else if (packageName.equals("app.android.applicationxc")) {
            resId = R.drawable.icon_xiaocong;
        } else if (packageName.equals("com.example.airplay")) {
            resId = R.drawable.icon_airplay;
        } else if (packageName.equals("com.amlogic.miracast")) {
            resId = R.drawable.icon_miracast;
        } else if (packageName.equals("com.amlogic.PPPoE")) {
            resId = R.drawable.icon_pppoe;
        } else if (packageName.equals("com.android.service.remotecontrol")) {
            resId = R.drawable.icon_remotecontrol;
        } else if (packageName.equals("com.mbx.settingsmbox")) {
            resId = R.drawable.icon_setting;
        } else if (packageName.equals("com.android.music")) {
            resId = R.drawable.icon_music;
        } else {
            return null;
        }

        return getResources().getDrawable(resId);
    }

    private int parseItemBackground(int num) {
        switch (num % 13) {
        case 0:
            return R.drawable.item_1;
        case 1:
            return R.drawable.item_2;
        case 2:
            return R.drawable.item_3;
        case 3:
            return R.drawable.item_4;
        case 4:
            return R.drawable.item_5;
        case 5:
            return R.drawable.item_6;
        case 6:
            return R.drawable.item_7;
        case 7:
            return R.drawable.item_8;
        case 8:
            return R.drawable.item_9;
        case 9:
            return R.drawable.item_10;
        case 10:
            return R.drawable.item_11;
        case 11:
            return R.drawable.item_12;
        case 12:
            return R.drawable.item_13;
        default:
            return R.drawable.item_1;
        }
    }

    private void reLoadAllData() {
        loadAllApps();
        loadAllCustomGroupAppsPkgFromDb();
        loadAllGroupApps();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
