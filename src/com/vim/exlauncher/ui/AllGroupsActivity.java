package com.vim.exlauncher.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.vim.exlauncher.R;
import com.vim.exlauncher.R.string;
import com.vim.exlauncher.data.ApkInfo;
import com.vim.exlauncher.data.ApplicationInfo;
import com.vim.exlauncher.data.ExLauncherContentProvider;
import com.vim.exlauncher.data.GroupUtils;

public class AllGroupsActivity extends Activity {
    private static final String TAG = "AllGroupsActivity";

    public static ViewFlipper mFlipper;

    private RelativeLayout mRlGroupVideo;
    private RelativeLayout mRlGroupRecommand;
    private RelativeLayout mRlGroupApp;
    private RelativeLayout mRlGroupMusic;
    private RelativeLayout mRlGroupLocal;

    private GroupGridLayout mGridLayoutGroupVideo;
    private GroupGridLayout mGridLayoutGroupRecommand;
    private GroupGridLayout mGridLayoutGroupApp;
    private GroupGridLayout mGridLayoutGroupMusic;
    private GroupGridLayout mGridLayoutGroupLocal;
    private Map<Integer, GroupGridLayout> mHashMapGroupGridLayout = new HashMap<Integer, GroupGridLayout>();

    private static final int INDEX_VIDEO = 0;
    private static final int INDEX_RECOMMAND = 1;
    private static final int INDEX_APP = 2;
    private static final int INDEX_MUSIC = 3;
    private static final int INDEX_LOCAL = 4;
    private static final int INDEX_MAX_SIZE = INDEX_LOCAL + 1;

    private final List<ApkInfo> mListGroupVideo = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupRecommand = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupApp = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupMusic = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupLocal = new ArrayList<ApkInfo>();
    private final Map<Integer, List<ApkInfo>> mHashMapGroup = new HashMap<Integer, List<ApkInfo>>();

    private final List<String> mListGroupVideoFromDb = new ArrayList<String>();
    private final List<String> mListGroupRecommandFromDb = new ArrayList<String>();
    private final List<String> mListGroupMusicFromDb = new ArrayList<String>();
    private final List<String> mListGroupLocalFromDb = new ArrayList<String>();
    private final Map<Integer, List<String>> mHashMapGroupListFromDb = new HashMap<Integer, List<String>>();
    
    public static int sBoundaryCount;
    public static final Object BoundaryObj = new Object(); 
    
    static {
        sBoundaryCount = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_group);

        initView();
        initHashMapGroup();
        initHashMapGroupFromDb();
    }

    private void initView() {
        mFlipper = (ViewFlipper) findViewById(R.id.menu_flipper);

        mRlGroupVideo = (RelativeLayout) findViewById(R.id.menu_layout_video);
        mRlGroupRecommand = (RelativeLayout) findViewById(R.id.menu_layout_recommend);
        mRlGroupApp = (RelativeLayout) findViewById(R.id.menu_layout_app);
        mRlGroupMusic = (RelativeLayout) findViewById(R.id.menu_layout_music);
        mRlGroupLocal = (RelativeLayout) findViewById(R.id.menu_layout_local);

        mGridLayoutGroupVideo = (GroupGridLayout) findViewById(R.id.gv_shortcut_video);
        mGridLayoutGroupRecommand = (GroupGridLayout) findViewById(R.id.gv_shortcut_recommend);
        mGridLayoutGroupApp = (GroupGridLayout) findViewById(R.id.gv_shortcut_app);
        mGridLayoutGroupMusic = (GroupGridLayout) findViewById(R.id.gv_shortcut_music);
        mGridLayoutGroupLocal = (GroupGridLayout) findViewById(R.id.gv_shortcut_local);
        
        mHashMapGroupGridLayout.put(INDEX_APP, mGridLayoutGroupApp);
        mHashMapGroupGridLayout.put(INDEX_LOCAL, mGridLayoutGroupLocal);
        mHashMapGroupGridLayout.put(INDEX_MUSIC, mGridLayoutGroupMusic);
        mHashMapGroupGridLayout.put(INDEX_RECOMMAND, mGridLayoutGroupRecommand);
        mHashMapGroupGridLayout.put(INDEX_VIDEO, mGridLayoutGroupVideo);

        mFlipper.setDisplayedChild(INDEX_APP);
        mFlipper.getChildAt(INDEX_APP).requestFocus();
    }

    private void initHashMapGroup() {
        mHashMapGroup.put(INDEX_VIDEO, mListGroupVideo);
        mHashMapGroup.put(INDEX_RECOMMAND, mListGroupRecommand);
        mHashMapGroup.put(INDEX_APP, mListGroupApp);
        mHashMapGroup.put(INDEX_MUSIC, mListGroupMusic);
        mHashMapGroup.put(INDEX_LOCAL, mListGroupLocal);
    }

    private void initHashMapGroupFromDb() {
        mHashMapGroupListFromDb.put(INDEX_VIDEO, mListGroupVideoFromDb);
        mHashMapGroupListFromDb.put(INDEX_RECOMMAND, mListGroupRecommandFromDb);
        mHashMapGroupListFromDb.put(INDEX_MUSIC, mListGroupMusicFromDb);
        mHashMapGroupListFromDb.put(INDEX_LOCAL, mListGroupLocalFromDb);
    }

    private void loadAppInfoByGroup(int group) {
        List<String> apkInfoList = mHashMapGroupListFromDb.get(group);
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
            if (i== INDEX_APP){
                continue;
            }
            
            loadAppInfoByGroup(i);
        }
    }

    private void loadAppByGroupList(List<ApkInfo> storedlist,
            List<String> pkgList) {
        logd("[loadApp] storedlist : " + storedlist + ", pkgList : " + pkgList);

        storedlist.clear();

        PackageManager manager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(
                mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps == null) {
            loge("[loadApp] apps is null! this should not happen!");
            return;
        }

        for (int i = 0; i < apps.size(); i++) {
            ApplicationInfo application = new ApplicationInfo();
            ResolveInfo info = apps.get(i);

            application.title = info.loadLabel(manager);
            application.setActivity(new ComponentName(
                    info.activityInfo.applicationInfo.packageName,
                    info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            application.icon = info.activityInfo.loadIcon(manager);

            boolean toAdd = true;
            if ((pkgList != null)
                    && !pkgList
                            .contains(info.activityInfo.applicationInfo.packageName)) {
                logd("[loadApp] do not match for this custom group of this pkg : "
                        + info.activityInfo.applicationInfo.packageName);
                toAdd = false;
            }

            if (toAdd) {
                ApkInfo apkInfo = new ApkInfo();
                apkInfo.setTitle(application.title.toString());
                apkInfo.setIntent(application.intent);
                apkInfo.setIcon(application.icon);
                apkInfo.setComponentName(application.componentName);
                storedlist.add(apkInfo);
            }
        }
    }

    private void loadAllGroupApps() {
        for (int i = INDEX_VIDEO; i < mHashMapGroup.size(); i++) {
            loadAppByGroupList(mHashMapGroup.get(i),
                    mHashMapGroupListFromDb.get(i));
        }
    }
    
    private void setAllLayoutView(){
        for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
            mHashMapGroupGridLayout.get(i).setLayoutView(mHashMapGroup.get(i));
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        reLoadAllData();
    }
    
    private void reLoadAllData(){
        loadAllCustomGroupAppsPkgFromDb();
        loadAllGroupApps();
        setAllLayoutView();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
