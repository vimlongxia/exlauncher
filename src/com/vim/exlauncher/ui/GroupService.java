package com.vim.exlauncher.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.GroupUtils;

public class GroupService extends Service {
    private static final String TAG = "GrouService";
    private ArrayList<String> mArrayListPendingPkg;
    private ArrayList<String> mArrayListPreinstallPkg;
    private Map<String, Integer> mMapPreinstallPkg;

    private AlertDialog mPkgDlg;
    private Drawable mApkIcon;
    private CharSequence mApkTitle;

    public static final boolean OLD_STYLE_APPS = false;

    private final BroadcastReceiver mAppReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();
            final String pkg = intent.getData().getSchemeSpecificPart();
            logd("[onReceive] action : " + action + ", pkg : " + pkg);

            if (TextUtils.isEmpty(pkg)) {
                Log.e(TAG, "[onReceive] pkg is empty!");
                return;
            }

            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                if (!isLauncher(pkg)) {
                    logd("[onReceive] this pkg " + pkg + " is not a app.");
                    return;
                }

                if (mArrayListPreinstallPkg.contains(pkg)) {
                    logd("[onReceive] this pkg " + pkg
                            + " is contained in the preinstall list.");
                    int index = mMapPreinstallPkg.get(pkg);
                    if (index == AllApps.INDEX_APPS) {
                        logd("[onReceive] this apk does not belong to any type");
                        return;
                    }

                    getPkgTitleAndIcon(pkg);
                    addPkgToDb(index, pkg);
                } else {
                    if (OLD_STYLE_APPS) {
                        mArrayListPendingPkg.add(pkg);
                        showAppAddedDlg(pkg);
                    }
                }
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                processAppRemoved(pkg);
            }
        }
    };

    private void processAppRemoved(String pkg) {
        logd("[processAppRemoved] remove pkg : " + pkg);
        GroupUtils.deleteByPkgAndType(this, pkg, -1);
    }

    private void getPkgTitleAndIcon(String pkg) {
        PackageManager manager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(
                mainIntent, 0);

        if (apps != null) {
            for (int i = 0; i < apps.size(); i++) {
                ResolveInfo info = apps.get(i);

                if (info.activityInfo.applicationInfo.packageName.contains(pkg)) {
                    mApkTitle = info.loadLabel(manager);
                    mApkIcon = info.activityInfo.loadIcon(manager);

                    logd("mApkTitle : " + mApkTitle.toString()
                            + ", mApkIcon : " + mApkIcon);
                    break;
                }
            }
        }
    }

    private boolean isLauncher(String pkg) {
        boolean ret = false;
        PackageManager manager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(
                mainIntent, 0);

        if (apps == null) {
            Log.e(TAG, "[getApkData] this is no any apks in this system!");
            return false;
        }

        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);

            if (info.activityInfo.applicationInfo.packageName.contains(pkg)) {
                logd("[isLauncher] match! this pkg " + pkg + " is a app.");
                return true;
            }
        }
        return ret;
    }

    private void showAppAddedDlg(final String pkg) {
        if (mPkgDlg != null) {
            logd("package dialog is showing, some pkg must be processing!");
            return;
        }

        getPkgTitleAndIcon(pkg);
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupService.this);
        builder.setTitle(mApkTitle);
        builder.setItems(R.array.add_to_group_type,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // TODO Auto-generated method stub
                        logd("[onClick] whichButton : " + whichButton
                                + ", pkg : " + pkg);

                        // the first type is INDEX_APPS, and we don't need to
                        // save this type
                        addPkgToDb(whichButton + 1, pkg);
                    }
                });

        builder.setIcon(mApkIcon);

        builder.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub
                logd("[onDismiss] mPkgDlg : " + mPkgDlg);
                if (mPkgDlg != null) {
                    mPkgDlg = null;
                }

                mArrayListPendingPkg.remove(pkg);
                if (mArrayListPendingPkg.size() > 0) {
                    logd("[onDismiss] mArrayListPendingPkg size : "
                            + mArrayListPendingPkg.size()
                            + ", continue to process the next pending pkg.");

                    showAppAddedDlg(mArrayListPendingPkg.get(0));
                }
            }
        });

        builder.setCancelable(true);

        mPkgDlg = builder.create();
        mPkgDlg.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mPkgDlg.show();
    }

    private void addPkgToDb(int groupType, String pkg) {
        GroupUtils.deleteByPkgAndType(this, pkg, groupType);
        GroupUtils.inserIntoGroupTable(this, groupType, pkg,
                mApkTitle.toString());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        logd("[onBind] intent : " + intent);
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        logd("[onCreate] register the app receiver");

        mArrayListPendingPkg = new ArrayList<String>();

        initPreinstallList();

        // Register intent receivers
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mAppReceiver, filter);
    }

    private void initPreinstallList() {
        mArrayListPreinstallPkg = new ArrayList<String>();
        mMapPreinstallPkg = new HashMap<String, Integer>();

        String[] preinstallPkgGames = this.getResources().getStringArray(
                R.array.preinstall_pkg_games);
        String[] preinstallPkgMusic = this.getResources().getStringArray(
                R.array.preinstall_pkg_music);
        String[] preinstallPkgMedia = this.getResources().getStringArray(
                R.array.preinstall_pkg_media);
        String[] preinstallPkgOther = this.getResources().getStringArray(
                R.array.preinstall_pkg_other);

        if ((preinstallPkgGames != null) && (preinstallPkgGames.length > 0)) {
            for (String pkg : preinstallPkgGames) {
                mArrayListPreinstallPkg.add(pkg);
                mMapPreinstallPkg.put(pkg, AllApps.INDEX_GAMES);
            }
        }

        if ((preinstallPkgMusic != null) && (preinstallPkgMusic.length > 0)) {
            for (String pkg : preinstallPkgMusic) {
                mArrayListPreinstallPkg.add(pkg);
                mMapPreinstallPkg.put(pkg, AllApps.INDEX_MUSIC);
            }
        }

        if ((preinstallPkgMedia != null) && (preinstallPkgMedia.length > 0)) {
            for (String pkg : preinstallPkgMedia) {
                mArrayListPreinstallPkg.add(pkg);
                mMapPreinstallPkg.put(pkg, AllApps.INDEX_MEDIA);
            }
        }

        if ((preinstallPkgOther != null) && (preinstallPkgOther.length > 0)) {
            for (String pkg : preinstallPkgMedia) {
                mArrayListPreinstallPkg.add(pkg);
                mMapPreinstallPkg.put(pkg, AllApps.INDEX_APPS);
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        logd("[onDestroy] unregister the app receiver");
        unregisterReceiver(mAppReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        logd("[onStartCommand] intent : " + intent + ", flags : " + flags
                + ", startId : " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    private void logd(String strs) {
        Log.d(TAG, strs);
    }
}
