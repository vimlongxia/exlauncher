package com.vim.exlauncher.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
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
    private RelativeLayout mRlGroupRecommend;
    private RelativeLayout mRlGroupApp;
    private RelativeLayout mRlGroupMusic;
    private RelativeLayout mRlGroupLocal;

    private GroupGridLayout mGridLayoutGroupVideo;
    private GroupGridLayout mGridLayoutGroupRecommend;
    private GroupGridLayout mGridLayoutGroupApp;
    private GroupGridLayout mGridLayoutGroupMusic;
    private GroupGridLayout mGridLayoutGroupLocal;
    private SparseArray<GroupGridLayout> mSparseArrayGroupGridLayout = new SparseArray<GroupGridLayout>();

    private GroupGridView mGridViewGroupVideo;
    private GroupGridView mGridViewGroupRecommend;
    private GroupGridView mGridViewGroupApp;
    private GroupGridView mGridViewGroupMusic;
    private GroupGridView mGridViewGroupLocal;
    private SparseArray<GroupGridView> mSparseArrayGroupGridView = new SparseArray<GroupGridView>();

    private static final int INDEX_VIDEO = 0;
    private static final int INDEX_RECOMMEND = 1;
    private static final int INDEX_APP = 2;
    private static final int INDEX_MUSIC = 3;
    private static final int INDEX_LOCAL = 4;
    private static final int INDEX_MAX_SIZE = INDEX_LOCAL + 1;

    private final List<ApkInfo> mListAllApps = new ArrayList<ApkInfo>();

    private final List<ApkInfo> mListGroupVideo = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupRecommend = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupApp = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupMusic = new ArrayList<ApkInfo>();
    private final List<ApkInfo> mListGroupLocal = new ArrayList<ApkInfo>();
    private final SparseArray<List<ApkInfo>> mSparseArrayGroup = new SparseArray<List<ApkInfo>>();

    private final List<String> mListGroupVideoFromDb = new ArrayList<String>();
    private final List<String> mListGroupRecommendFromDb = new ArrayList<String>();
    private final List<String> mListGroupMusicFromDb = new ArrayList<String>();
    private final List<String> mListGroupLocalFromDb = new ArrayList<String>();
    private final SparseArray<List<String>> mSparseArrayGroupFromDb = new SparseArray<List<String>>();

    public static int sBoundaryCount;
    public static final Object BoundaryObj = new Object();
    
    private static int sPreGridFocusIndex;
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

        sPreGridFocusIndex = -1;
        sCurGridFocusIndex = 0;
    }

    private void initView() {
        mFlipper = (ViewFlipper) findViewById(R.id.menu_flipper);

        mRlGroupVideo = (RelativeLayout) findViewById(R.id.menu_layout_video);
        mRlGroupRecommend = (RelativeLayout) findViewById(R.id.menu_layout_recommend);
        mRlGroupApp = (RelativeLayout) findViewById(R.id.menu_layout_app);
        mRlGroupMusic = (RelativeLayout) findViewById(R.id.menu_layout_music);
        mRlGroupLocal = (RelativeLayout) findViewById(R.id.menu_layout_local);

        // mGridLayoutGroupVideo = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_video);
        // mGridLayoutGroupRecommend = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_recommend);
        // mGridLayoutGroupApp = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_app);
        // mGridLayoutGroupMusic = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_music);
        // mGridLayoutGroupLocal = (GroupGridLayout)
        // findViewById(R.id.gv_shortcut_local);

        mGridViewGroupVideo = (GroupGridView) findViewById(R.id.gv_video);
        mGridViewGroupRecommend = (GroupGridView) findViewById(R.id.gv_recommend);
        mGridViewGroupApp = (GroupGridView) findViewById(R.id.gv_app);
        mGridViewGroupMusic = (GroupGridView) findViewById(R.id.gv_music);
        mGridViewGroupLocal = (GroupGridView) findViewById(R.id.gv_local);

        mSparseArrayGroupGridView.put(INDEX_APP, mGridViewGroupApp);
        mSparseArrayGroupGridView.put(INDEX_LOCAL, mGridViewGroupLocal);
        mSparseArrayGroupGridView.put(INDEX_MUSIC, mGridViewGroupMusic);
        mSparseArrayGroupGridView.put(INDEX_RECOMMEND, mGridViewGroupRecommend);
        mSparseArrayGroupGridView.put(INDEX_VIDEO, mGridViewGroupVideo);

        mFlipper.setDisplayedChild(INDEX_APP);
        mFlipper.getChildAt(INDEX_APP).requestFocus();
    }

    private void initHashMapGroup() {
        mSparseArrayGroup.put(INDEX_VIDEO, mListGroupVideo);
        mSparseArrayGroup.put(INDEX_RECOMMEND, mListGroupRecommend);
        mSparseArrayGroup.put(INDEX_APP, mListGroupApp);
        mSparseArrayGroup.put(INDEX_MUSIC, mListGroupMusic);
        mSparseArrayGroup.put(INDEX_LOCAL, mListGroupLocal);
    }

    private void initHashMapGroupFromDb() {
        mSparseArrayGroupFromDb.put(INDEX_VIDEO, mListGroupVideoFromDb);
        mSparseArrayGroupFromDb.put(INDEX_RECOMMEND, mListGroupRecommendFromDb);
        mSparseArrayGroupFromDb.put(INDEX_MUSIC, mListGroupMusicFromDb);
        mSparseArrayGroupFromDb.put(INDEX_LOCAL, mListGroupLocalFromDb);
    }

    public static int getPreGridFocusIndex(){
        logd("[getPreGridFocusIndex] sPreGridFocusIndex : " + sPreGridFocusIndex);
        return sPreGridFocusIndex;
    }
    
    public static void setPreGridFocusIndex(int index){
        logd("[setPreGridFocusIndex] index : " + index);
        sPreGridFocusIndex = index;
    }
    
    public static int getCurGridFocusIndex(){
        logd("[getCurGridFocusIndex] sCurGridFocusIndex : " + sCurGridFocusIndex);
        return sCurGridFocusIndex;
    }
    
    public static void setCurGridFocusIndex(int index){
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
            if (i == INDEX_APP) {
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

        if (mListAllApps.size() == 0) {
            loge("[loadAppByGroupList] mListAllApps size 0! this should not happen!");
            return;
        }

        if (pkgList == null) {
            // this is apps list
            storedlist.addAll(mListAllApps);
        } else {
            // this is other groups
            ApkInfo allApkInfo = null;
            for (int i = 0; i < mListAllApps.size(); i++) {
                allApkInfo = mListAllApps.get(i);

                if (pkgList.contains(allApkInfo.getPkg())) {
                    logd("[loadAppByGroupList] match ! pkg : "
                            + allApkInfo.getPkg());
                    ApkInfo apkInfo = new ApkInfo(allApkInfo);
                    storedlist.add(apkInfo);
                }
            }
        }

    }

    private void loadAllGroupApps() {
        for (int i = INDEX_VIDEO; i < mSparseArrayGroup.size(); i++) {
            loadAppByGroupList(mSparseArrayGroup.get(i),
                    mSparseArrayGroupFromDb.get(i, null));
        }
    }

    private void setAllLayoutView() {
        // for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
        // mSparseArrayGroupGridLayout.get(i).setLayoutView(
        // mSparseArrayGroup.get(i));
        // }

        for (int i = INDEX_VIDEO; i < INDEX_MAX_SIZE; i++) {
            GroupGridView gv = mSparseArrayGroupGridView.get(i);
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
                    AllGroupsActivity.this.startActivity(listApk.get(position)
                            .getIntent());
                }
            });
//            gv.setOnKeyListener(new GridViewOnKeyListener(this, AllGroupsActivity.mFlipper));
            gv.setOnItemSelectedListener(new GridViewOnItemSelectedListener());
            
            gv.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
                @Override  
                public void onFocusChange(View v, boolean hasFocus) {  
                    if (!hasFocus) {  
                        try {  
                            @SuppressWarnings("unchecked")  
                            Class<GridView> c = (Class<GridView>) Class  
                                    .forName("android.widget.GridView");  
                            Method[] flds = c.getDeclaredMethods();  
                            for (Method f : flds) {  
                                if ("setSelectionInt".equals(f.getName())) {  
                                    f.setAccessible(true);  
                                    f.invoke(v,  
                                            new Object[] { Integer.valueOf(-1) });  
                                }  
                            }  
                        } catch (Exception e) {  
                            e.printStackTrace();  
                        }  
                    }  
                }  
            });  
        }
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
        mListAllApps.clear();

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

        for (int i = 0; i < apps.size(); i++) {
            ApplicationInfo application = new ApplicationInfo();
            ResolveInfo info = apps.get(i);

            application.title = info.loadLabel(manager);
            application.setActivity(new ComponentName(
                    info.activityInfo.applicationInfo.packageName,
                    info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            application.icon = info.activityInfo.loadIcon(manager);

            ApkInfo apkInfo = new ApkInfo();
            apkInfo.setTitle(application.title.toString());
            apkInfo.setIntent(application.intent);
            apkInfo.setIcon(application.icon);
            apkInfo.setComponentName(application.componentName);
            apkInfo.setPkg(info.activityInfo.applicationInfo.packageName);

            logd("[loadAllApps] apk title : " + apkInfo.getTitle());
            mListAllApps.add(apkInfo);
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
