package com.vim.exlauncher.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class GroupUtils {
    private static final String TAG = "GroupUtils";

    public static void inserIntoGroupTable(Context context, int groupType,
            String pkg, String title) {
        ContentValues contentValues = new ContentValues();
        contentValues
                .put(ExLauncherContentProvider.TABLE_GROUP_TYPE, groupType);
        contentValues.put(ExLauncherContentProvider.TABLE_GROUP_PACKAGE, pkg);
        contentValues.put(ExLauncherContentProvider.TABLE_GROUP_TITLE, title);

        Uri insertUri = context.getContentResolver().insert(
                ExLauncherContentProvider.URI_GROUP, contentValues);
        logd("[inserIntoGroupTable] insertUri : " + insertUri);
    }

    public static int deleteByPkgAndType(Context context, String pkg,
            int groupType) {
        String selection = null;
        String[] selectionArgs = null;

        if (groupType == -1) {
            selection = ExLauncherContentProvider.TABLE_GROUP_PACKAGE + "=?";
            selectionArgs = new String[] { pkg };
        } else {
            selection = ExLauncherContentProvider.TABLE_GROUP_PACKAGE + "=?"
                    + " AND " + ExLauncherContentProvider.TABLE_GROUP_TYPE
                    + "=?";
            selectionArgs = new String[] { pkg, groupType + "" };
        }

        int row = context.getContentResolver().delete(
                ExLauncherContentProvider.URI_GROUP, selection, selectionArgs);

        logd("[deleteByPkg] pkg : " + pkg + ", row : " + row);
        return row;
    }

    public static Cursor getGroupDataByGroup(Context context, int groupType) {
        Cursor cursor = null;

        String selection = ExLauncherContentProvider.TABLE_GROUP_TYPE + "=?";
        String[] selectionArgs = new String[] { groupType + "" };

        cursor = context.getContentResolver().query(
                ExLauncherContentProvider.URI_GROUP,
                ExLauncherContentProvider.PROJECTION_GROUP, selection,
                selectionArgs, ExLauncherContentProvider.TABLE_GROUP_ORDER_ASC);

        logd("[getGroupDataByType] cursor : " + cursor + ", groupType : "
                + groupType);
        if (cursor != null) {
            logd("[getGroupDataByType] cursor counts : " + cursor.getCount());
        }
        return cursor;
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }
}
