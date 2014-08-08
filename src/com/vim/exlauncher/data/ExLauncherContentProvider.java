package com.vim.exlauncher.data;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.vim.exlauncher.R;

public class ExLauncherContentProvider extends ContentProvider {
    private static final String TAG = "ExLauncherContentProvider";

    private static final String AUTHORITY = "com.vim.exlauncher";
    public static final String DATABASE_NAME = "exlauncher.db";

    // bottom message table
    private static final String TABLE_MSG = "table_msg";
    public static final String TABLE_MSG_ID = "_id";
    public static final String TABLE_MSG_MSG_ID = "msg_id";
    public static final String TABLE_MSG_BOTTOM_MSG = "message";
    public static final String TABLE_MSG_ORDER_ASC = TABLE_MSG_MSG_ID + " ASC";
    public static final String TABLE_MSG_ORDER_DESC = TABLE_MSG_MSG_ID
            + " DESC";

    public static final Uri URI_MSG = Uri.parse("content://" + AUTHORITY + "/"
            + TABLE_MSG);
    public static final String[] PROJECTION_MSG = new String[] {
            TABLE_MSG_MSG_ID, TABLE_MSG_BOTTOM_MSG };

    // package group table
    private static final String TABLE_GROUP = "table_group";
    public static final String TABLE_GROUP_ID = "_id";
    public static final String TABLE_GROUP_TYPE = "group_type";
    public static final String TABLE_GROUP_PACKAGE = "package";
    public static final String TABLE_GROUP_TITLE = "title";
    public static final String TABLE_GROUP_ORDER_ASC = TABLE_GROUP_TITLE
            + " ASC";

    public static final Uri URI_GROUP = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE_GROUP);
    public static final String[] PROJECTION_GROUP = new String[] {
            TABLE_GROUP_TYPE, TABLE_GROUP_PACKAGE, TABLE_GROUP_TITLE };

    private static final int URL_MSG_ALL = 1;
    private static final int URL_MSG_ID = 2;

    private static final int URL_GROUP_ALL = 3;
    private static final int URL_GROUP_ID = 4;

    private DatabaseHelper mOpenHelper;

    private static final UriMatcher s_urlMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        s_urlMatcher.addURI(AUTHORITY, TABLE_MSG, URL_MSG_ALL);
        s_urlMatcher.addURI(AUTHORITY, TABLE_MSG + "/#", URL_MSG_ID);
        s_urlMatcher.addURI(AUTHORITY, TABLE_GROUP, URL_GROUP_ALL);
        s_urlMatcher.addURI(AUTHORITY, TABLE_GROUP + "/#", URL_GROUP_ID);
    }

    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        // TODO Auto-generated method stub
        logd("[delete] url : " + url + ", where : " + where + ", whereArgs : "
                + whereArgs);
        if (whereArgs != null) {
            StringBuilder sb = new StringBuilder();
            for (String w : whereArgs) {
                sb.append(w);
                sb.append(", ");
            }

            logd("[delete] whereArgs : " + sb.toString());
        }

        int count = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = s_urlMatcher.match(url);
        switch (match) {
        case URL_MSG_ALL:
        case URL_MSG_ID:
            count = db.delete(TABLE_MSG, where, whereArgs);
            break;

        case URL_GROUP_ALL:
        case URL_GROUP_ID:
            count = db.delete(TABLE_GROUP, where, whereArgs);
            break;

        default:
            logd("[delete] this url : " + url + " does not match anything!");
            break;
        }

        logd("[delete] has deleted " + count + " item(s).");

        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public String getType(Uri url) {
        // TODO Auto-generated method stub
        logd("[getType] url : " + url);
        return null;
    }

    @Override
    public Uri insert(Uri url, ContentValues contentValues) {
        // TODO Auto-generated method stub
        logd("[insert] url : " + url);
        Uri result = null;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = s_urlMatcher.match(url);
        long rowId = -1;

        switch (match) {
        case URL_MSG_ALL:
        case URL_MSG_ID:
            rowId = db.insert(TABLE_MSG, null, contentValues);
            logd("[insert] table : " + TABLE_MSG + ", rowId : " + rowId);
            if (rowId > 0) {
                result = ContentUris.withAppendedId(url, rowId);
            }
            break;

        case URL_GROUP_ALL:
        case URL_GROUP_ID:
            rowId = db.insert(TABLE_GROUP, null, contentValues);
            logd("[insert] table : " + TABLE_GROUP + ", rowId : " + rowId);
            if (rowId > 0) {
                result = ContentUris.withAppendedId(url, rowId);
            }
            break;

        default:
            logd("[insert] this url : " + url + " does not match anything!");
            break;
        }

        logd("[insert] result : " + result);

        getContext().getContentResolver().notifyChange(url, null);
        return result;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        mOpenHelper = new DatabaseHelper(getContext());
        initDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
        // TODO Auto-generated method stub
        logd("[query] url : " + url + ", selection : " + selection
                + ", selectionArgs : " + selectionArgs);
        if (selectionArgs != null) {
            StringBuilder sb = new StringBuilder();
            for (String w : selectionArgs) {
                sb.append(w);
                sb.append(", ");
            }

            logd("[query] selectionArgs : " + sb.toString());
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        int match = s_urlMatcher.match(url);
        switch (match) {
        case URL_MSG_ALL:
        case URL_MSG_ID:
            qb.setTables(TABLE_MSG);
            logd("[query] set table " + TABLE_MSG);
            break;

        case URL_GROUP_ALL:
        case URL_GROUP_ID:
            qb.setTables(TABLE_GROUP);
            logd("[query] set table " + TABLE_GROUP);
            break;

        default:
            logd("[query] this url : " + url + " does not match anything!");
            break;
        }

        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs, null,
                null, sort);

        logd("[query] ret : " + ret);
        if (ret != null) {
            logd("[query] ret counts : " + ret.getCount());
        }

        return ret;
    }

    @Override
    public int update(Uri url, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        logd("[update] url : " + url + ", values : " + values
                + ", selection : " + selection + ", selectionArgs : "
                + selectionArgs);
        if (selectionArgs != null) {
            StringBuilder sb = new StringBuilder();
            for (String w : selectionArgs) {
                sb.append(w);
                sb.append(", ");
            }

            logd("[update] selectionArgs : " + sb.toString());
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = s_urlMatcher.match(url);
        int row = -1;
        switch (match) {
        case URL_MSG_ALL:
        case URL_MSG_ID:
            row = db.update(TABLE_MSG, values, selection, selectionArgs);
            break;

        case URL_GROUP_ALL:
        case URL_GROUP_ID:
            row = db.update(TABLE_GROUP, values, selection, selectionArgs);
            break;

        default:
            logd("[update] this url : " + url + " does not match anything!");
            break;
        }

        logd("[update] has deleted " + row + " item(s).");

        getContext().getContentResolver().notifyChange(url, null);
        return row;
    }

    private void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        /**
         * DatabaseHelper helper class for loading apns into a database.
         * 
         * @param context
         *            of the user.
         */
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Set up the database schema
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MSG + " ("
                    + TABLE_MSG_ID + " INTEGER PRIMARY KEY," + TABLE_MSG_MSG_ID
                    + " INTEGER," + TABLE_MSG_BOTTOM_MSG + " TEXT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GROUP + " ("
                    + TABLE_GROUP_ID + " INTEGER PRIMARY KEY,"
                    + TABLE_GROUP_TYPE + " INTEGER," + TABLE_GROUP_PACKAGE
                    + " TEXT," + TABLE_GROUP_TITLE + " TEXT);");
        }
    }

    private void initDatabase() {
        List<GroupXMLPaser.ApkDataInGroup> preinstallApksList = GroupXMLPaser
                .parse(this.getContext(), R.xml.preinstall_group_pkg);
        
        for (int i=0; i<preinstallApksList.size(); i++){
            GroupXMLPaser.ApkDataInGroup apkData = preinstallApksList.get(i);
            ContentValues values = new ContentValues();
            values.put(TABLE_GROUP_TYPE, apkData.getGroup());
            values.put(TABLE_GROUP_PACKAGE, apkData.getPkg());
            
            mOpenHelper.getWritableDatabase().insert(TABLE_GROUP, null, values);
        }
    }
}
