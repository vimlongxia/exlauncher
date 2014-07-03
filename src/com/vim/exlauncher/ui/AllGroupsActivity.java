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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;
import com.vim.exlauncher.data.ApplicationInfo;
import com.vim.exlauncher.data.ExLauncherContentProvider;
import com.vim.exlauncher.data.GroupUtils;

public class AllGroupsActivity extends Activity {
    private static final String TAG = "AllGroupsActivity";

    public static ViewFlipper mFlipper;

    private RelativeLayout mRlGroupVideo;
    private RelativeLayout mRlGroupGames;
    private RelativeLayout mRlGroupApp;
    private RelativeLayout mRlGroupMusic;
    private RelativeLayout mRlGroupLocal;

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

    public static final List<ApkInfo> sListAllApps = new ArrayList<ApkInfo>();

    private final List<ApkInfo> mListGroupVideo = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupGames = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupApp = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupMusic = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupLocal = new ArrayList<ApkInfo>();
    private final SparseArray<List<ApkInfo>> mSparseArrayGroup = new SparseArray<List<ApkInfo>>();

    private final List<String> mListGroupVideoFromDb = new ArrayList<String>();
    private final List<String> mListGroupGamesFromDb = new ArrayList<String>();
    private final List<String> mListGroupMusicFromDb = new ArrayList<String>();
    private final List<String> mListGroupLocalFromDb = new ArrayList<String>();
    private final SparseArray<List<String>> mSparseArrayGroupFromDb = new SparseArray<List<String>>();

    public static int sBoundaryCount;
    public static final Object BoundaryObj = new Object();

    private static int sCurGridFocusIndex;

    static {
        sBoundaryCount = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_group);

        initView();
        initHashMapGroup();
        initHashMapGroupFromDb();

        sCurGridFocusIndex = 0;
    }

    private void initView() {
        mFlipper = (ViewFlipper) findViewById(R.id.menu_flipper);

        mRlGroupVideo = (RelativeLayout) findViewById(R.id.menu_layout_video);
        mRlGroupGames = (RelativeLayout) findViewById(R.id.menu_layout_games);
        mRlGroupApp = (RelativeLayout) findViewById(R.id.menu_layout_app);
        mRlGroupMusic = (RelativeLayout) findViewById(R.id.menu_layout_music);
        mRlGroupLocal = (RelativeLayout) findViewById(R.id.menu_layout_local);

        // mGridLayoutGroupVideo = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_video);
        // mGridLayoutGroupGames = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_Games);
        // mGridLayoutGroupApp = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_app);
        // mGridLayoutGroupMusic = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_music);
        // mGridLayoutGroupLocal = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_local);

        mGridViewGroupVideo = (GroupGridView) findViewById(R.id.gv_video);
        mGridViewGroupGames = (GroupGridView) findViewById(R.id.gv_games);
        mGridViewGroupApp = (GroupGridView) findViewById(R.id.gv_app);
        mGridViewGroupMusic = (GroupGridView) findViewById(R.id.gv_music);
        mGridViewGroupLocal = (GroupGridView) findViewById(R.id.gv_local);

        mSparseArrayGroupGridView.put(AllApps.INDEX_APPS, mGridViewGroupApp);
        mSparseArrayGroupGridView.put(AllApps.INDEX_LOCAL, mGridViewGroupLocal);
        mSparseArrayGroupGridView.put(AllApps.INDEX_MUSIC, mGridViewGroupMusic);
        mSparseArrayGroupGridView.put(AllApps.INDEX_GAMES, mGridViewGroupGames);
        mSparseArrayGroupGridView.put(AllApps.INDEX_VIDEO, mGridViewGroupVideo);

        if (getIntent().getBooleanExtra(START_TO_GAME, false)) {
            mFlipper.setDisplayedChild(AllApps.INDEX_GAMES);
            mFlipper.getChildAt(AllApps.INDEX_GAMES).requestFocus();
        } else {
            mFlipper.setDisplayedChild(AllApps.INDEX_APPS);
            mFlipper.getChildAt(AllApps.INDEX_APPS).requestFocus();
        }
    }

    private void initHashMapGroup() {
        mSparseArrayGroup.put(AllApps.INDEX_VIDEO, mListGroupVideo);
        mSparseArrayGroup.put(AllApps.INDEX_GAMES, mListGroupGames);
        mSparseArrayGroup.put(AllApps.INDEX_APPS, mListGroupApp);
        mSparseArrayGroup.put(AllApps.INDEX_MUSIC, mListGroupMusic);
        mSparseArrayGroup.put(AllApps.INDEX_LOCAL, mListGroupLocal);
    }

    private void initHashMapGroupFromDb() {
        mSparseArrayGroupFromDb.put(AllApps.INDEX_VIDEO, mListGroupVideoFromDb);
        mSparseArrayGroupFromDb.put(AllApps.INDEX_GAMES, mListGroupGamesFromDb);
        mSparseArrayGroupFromDb.put(AllApps.INDEX_MUSIC, mListGroupMusicFromDb);
        mSparseArrayGroupFromDb.put(AllApps.INDEX_LOCAL, mListGroupLocalFromDb);
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
        for (int i = AllApps.INDEX_VIDEO; i < AllApps.INDEX_MAX_SIZE; i++) {
            if (i == AllApps.INDEX_APPS) {
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

            Intent intent = new Intent();
            allApkInfo.setIntent(intent);

            allApkInfo.setIcon(getResources().getDrawable(
                    R.drawable.item_img_add));
            storedlist.add(allApkInfo);
        }
    }

    private void loadAllGroupApps() {
        for (int i = AllApps.INDEX_VIDEO; i < mSparseArrayGroup.size(); i++) {
            loadAppByGroupList(mSparseArrayGroup.get(i),
                    mSparseArrayGroupFromDb.get(i, null));
        }
    }

    private void setAllLayoutView() {
        // for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
        // mSparseArrayGroupGridLayout.get(i).setLayoutView(
        // mSparseArrayGroup.get(i));
        // }

        for (int i = AllApps.INDEX_VIDEO; i < AllApps.INDEX_MAX_SIZE; i++) {
            final GroupGridView gv = mSparseArrayGroupGridView.get(i);
            final List<ApkInfo> listApk = mSparseArrayGroup.get(i);
            BaseAdapter adapter = new GroupGridViewAdapter(this, listApk);
            gv.setAdapter(adapter);
            gv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    // TODO Auto-generated method stub
                    logd("[onItemClick] view : " + view + ", position : "
                            + position);
                    if (position == (listApk.size() - 1)) {
                        startCustomAct(view);
                    } else {
                        AllGroupsActivity.this.startActivity(listApk.get(
                                position).getIntent());
                    }
                }
            });
            gv.setOnKeyListener(new GridViewOnKeyListener(this,
                    AllGroupsActivity.mFlipper));
            gv.setOnItemSelectedListener(new GridViewOnItemSelectedListener());

            gv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    logd("[onFocusChange] v : " + v + ", hasFocus : "
                            + hasFocus);
                    if (!hasFocus) {
                        try {
                            @SuppressWarnings("unchecked")
                            Class<GridView> c = (Class<GridView>) Class
                                    .forName("android.widget.GridView");
                            Method[] flds = c.getDeclaredMethods();
                            for (Method f : flds) {
                                if ("setSelectionInt".equals(f.getName())) {
                                    f.setAccessible(true);
                                    f.invoke(
                                            v,
                                            new Object[] { Integer.valueOf(-1) });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        gv.setSelection(0);
                        sCurGridFocusIndex = 0;
                    }
                }
            });
        }
    }
    
    private void startCustomAct(View view){
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        reLoadAllData();
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
        setAllLayoutView();
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
