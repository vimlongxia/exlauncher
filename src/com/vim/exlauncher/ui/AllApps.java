package com.vim.exlauncher.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApplicationInfo;
import com.vim.exlauncher.data.ApplicationsAdapter;
import com.vim.exlauncher.data.ExLauncherContentProvider;
import com.vim.exlauncher.data.GroupUtils;

public class AllApps extends Activity {
    private static final String TAG = "AllApps3D";

    private GridView mGrid;
    private ArrayList<ApplicationInfo> mApplications;
    private ApplicationsAdapter mAppAdapter;
    private AppReceiver mAppReceiver;

    private int mCurrentDataType;
    private String[] mAllGroups;
    private String[] mGroupData;
    private AlertDialog mContextDlg;
    private ContentObserver mContentObserver;

    public static final String DATA_TYPE = "data_type";

    // this type means the selected item is an APK
    private static final int INDEX_APK = -2;

    // default means that curent page stands on the group selection page
    private static final int INDEX_DEFAULT = -1;

    // the following groups means that we are in the corresponding sub page
    public static final int INDEX_VIDEO = 0;
    public static final int INDEX_GAMES = 1;
    public static final int INDEX_APPS = 2;
    public static final int INDEX_MUSIC = 3;
    public static final int INDEX_LOCAL = 4;
    public static final int INDEX_MAX_SIZE = INDEX_LOCAL + 1;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.home);
        mAppReceiver = new AppReceiver();

        mCurrentDataType = getIntent().getIntExtra(DATA_TYPE, INDEX_DEFAULT);
        mAllGroups = getResources().getStringArray(R.array.group_type);

        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                // TODO Auto-generated method stub
                super.onChange(selfChange);
                logd("[onChange] selfChange : " + selfChange);
                
                loadApplications();
                bindApplications();
            }
        };

        this.getContentResolver().registerContentObserver(ExLauncherContentProvider.URI_GROUP, true, mContentObserver);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        loadApplications();
        bindApplications();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register intent receivers
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mAppReceiver, filter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(mAppReceiver);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch(keyCode){
        case KeyEvent.KEYCODE_BACK:
            if (mCurrentDataType != INDEX_DEFAULT){
                mCurrentDataType = INDEX_DEFAULT;

                loadApplications();
                bindApplications();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void getAllGroupData() {
        if (mApplications == null) {
            mApplications = new ArrayList<ApplicationInfo>();
        }
        mApplications.clear();

        for (int i = 0; i < INDEX_MAX_SIZE; i++) {
            ApplicationInfo application = new ApplicationInfo();
            application.title = mAllGroups[i];

            switch (i) {
            case INDEX_APPS:
                application.icon = getResources().getDrawable(
                        R.drawable.ic_button_apps);
                application.dataType = INDEX_APPS;
                break;

            case INDEX_GAMES:
                application.icon = getResources().getDrawable(
                        R.drawable.ic_button_games);
                application.dataType = INDEX_GAMES;
                break;

            case INDEX_MUSIC:
                application.icon = getResources().getDrawable(
                        R.drawable.ic_button_radio);
                application.dataType = INDEX_MUSIC;
                break;

            case INDEX_VIDEO:
                application.icon = getResources().getDrawable(
                        R.drawable.ic_button_movie);
                application.dataType = INDEX_VIDEO;
                break;
                
            case INDEX_LOCAL:
                application.icon = getResources().getDrawable(
                        R.drawable.ic_button_movie);
                application.dataType = INDEX_LOCAL;
                break;
            }

            mApplications.add(application);
        }
    }

    private void getApkData() {
        logd("[getApkData] mDataType : " + mCurrentDataType + ", mGroupData : "
                + mGroupData);
        if ((mCurrentDataType != INDEX_APPS)
                && ((mGroupData == null) || (mGroupData.length == 0))) {
            // type games/music/media has no data
            logd("[getApkData] mDataType : " + mCurrentDataType
                    + " has no data yet.");
            if (mApplications == null) {
                mApplications = new ArrayList<ApplicationInfo>();
            }
            mApplications.clear();
            return;
        }

        PackageManager manager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(
                mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps == null) {
            Log.e(TAG, "[getApkData] this is no any apks in this system!");
            return;
        }

        final int count = apps.size();

        if (mApplications == null) {
            mApplications = new ArrayList<ApplicationInfo>();
        }
        mApplications.clear();

        if (mCurrentDataType == INDEX_APPS) {
            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);

                application.title = info.loadLabel(manager);
                application.pkg = info.activityInfo.applicationInfo.packageName;
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);
                application.dataType = INDEX_APK;

                logd("" + application.title.toString());
                mApplications.add(application);
            }
        } else {
            for (int j = 0; j < mGroupData.length; j++) {
                for (int i = 0; i < count; i++) {
                    ApplicationInfo application = new ApplicationInfo();
                    ResolveInfo info = apps.get(i);

                    application.title = info.loadLabel(manager);
                    application.pkg = info.activityInfo.applicationInfo.packageName;
                    application
                            .setActivity(
                                    new ComponentName(
                                            info.activityInfo.applicationInfo.packageName,
                                            info.activityInfo.name),
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    application.icon = info.activityInfo.loadIcon(manager);
                    application.dataType = INDEX_APK;

                    if (info.activityInfo.applicationInfo.packageName
                            .contains(mGroupData[j])) {
                        logd("[getApkData] match! mGroupData[" + j + "] : "
                                + mGroupData[j] + " in type "
                                + mCurrentDataType);
                        mApplications.add(application);
                        break;
                    }
                }
            }
        }
    }

    private void getPkgListByType() {
        Cursor cursor = GroupUtils.getGroupDataByGroup(this, mCurrentDataType);
        if ((cursor == null) || (cursor.getCount() == 0)) {
            logd("[getPkgListByType] we don't get any date of this type : "
                    + mCurrentDataType);
            mGroupData = null;
            return;
        }

        mGroupData = new String[cursor.getCount()];
        int i = 0;
        try {
            while (cursor.moveToNext()) {
                String pkg = cursor
                        .getString(cursor
                                .getColumnIndex(ExLauncherContentProvider.TABLE_GROUP_PACKAGE));
                mGroupData[i++] = pkg;
            }
        } catch (Exception e) {
            Log.e(TAG, "[getPkgListByType] error when get pkg!");
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }
    
    private void loadApplications() {
        if (mCurrentDataType == INDEX_DEFAULT) {
            getAllGroupData();
        } else {
            getPkgListByType();
            getApkData();
        }
    }

    private void addPkgToDb(int groupType, String pkg, String title) {
        if (groupType == AllApps.INDEX_APPS) {
            return;
        }

        GroupUtils.deleteByPkgAndType(this, pkg, groupType);
        GroupUtils.inserIntoGroupTable(this, groupType, pkg, title);
    }

    private void showAppAddedDlg(final ApplicationInfo app) {
        if (mContextDlg != null) {
            logd("mContextDlg is showing, some pkg must be processing!");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(app.title);

        if ((mCurrentDataType >= INDEX_GAMES)
                && (mCurrentDataType <= INDEX_LOCAL)) {
            builder.setItems(R.array.remove_group_type,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            // TODO Auto-generated method stub
                            logd("[onClick] whichButton : " + whichButton
                                    + ", pkg : " + app.pkg);
                            GroupUtils.deleteByPkgAndType(AllApps.this,
                                    app.pkg, mCurrentDataType);
                            loadApplications();
                            bindApplications();
                        }
                    });
        } else {
            builder.setItems(R.array.add_to_group_type,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            // TODO Auto-generated method stub
                            logd("[onClick] whichButton : " + whichButton
                                    + ", pkg : " + app.pkg);
                            addPkgToDb(whichButton + 1, app.pkg, app.title.toString());
                        }
                    });
        }

        builder.setIcon(app.icon);

        builder.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub
                logd("[onDismiss] mContextDlg : " + mContextDlg);
                if (mContextDlg != null) {
                    mContextDlg = null;
                }
            }
        });

        builder.setCancelable(true);

        mContextDlg = builder.create();
        mContextDlg.show();
    }

    /**
     * Creates a new appplications adapter for the grid view and registers it.
     */
    private void bindApplications() {
        mAppAdapter = new ApplicationsAdapter(this, mApplications);
        if (mGrid == null) {
            mGrid = (GridView) findViewById(R.id.all_apps);
        }

        logd("[bindApplications] mApplications size : " + mApplications.size() + ", mCurrentDataType : " + mCurrentDataType);
        mGrid.setAdapter(mAppAdapter);
        mGrid.setSelection(0);
        mGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
                ApplicationInfo app = (ApplicationInfo) parent
                        .getItemAtPosition(position);

                if (mCurrentDataType == INDEX_DEFAULT) {
                    mCurrentDataType = app.dataType;
                    loadApplications();
                    bindApplications();
                } else {
                    startActivity(app.intent);
                }
            }
        });

        if (mCurrentDataType == INDEX_DEFAULT) {
            // the group selecting page, the item of the grid is a group
            mGrid.setOnItemLongClickListener(null);
            mGrid.setNumColumns(4);
        } else {
            // the group inner page, the item of the grid is an apk
            mGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent,
                        View view, int position, long id) {
                    // TODO Auto-generated method stub
                    ApplicationInfo app = (ApplicationInfo) parent
                            .getItemAtPosition(position);
                    if (app.dataType == INDEX_APK) {
                        showAppAddedDlg(app);
                    }
                    return true;
                }

            });
            mGrid.setNumColumns(6);
        }
    }

    public class AppReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();
            logd("[onReceive] action : " + action + ", mCurrentDataType : " + mCurrentDataType);
            if (mCurrentDataType != INDEX_APPS){
                logd("[onReceive] mCurrentDataType is not APPS!");
                return;
            }

            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                final String pkg = intent.getData().getSchemeSpecificPart();
                logd("[onReceive] pkg : " + pkg);

                if (TextUtils.isEmpty(pkg)) {
                    // they sent us a bad intent
                    Log.e(TAG, "[onReceive] pkg is empty!");
                    return;
                }

                loadApplications();
                bindApplications();
            }

        }
    }

    private void logd(String strs) {
        Log.d(TAG, strs);
    }
}
