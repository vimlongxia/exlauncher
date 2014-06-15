package com.vim.exlauncher.data;

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

public class BottomMsgContentProvider extends ContentProvider {
    private static final String TAG = "BottomMsgContentProvider";

    private static final String AUTHORITY = "bottommsg";
    private static final String DATABASE_NAME = "bottommsg.db";
    private static final String TABLE_MSG = "msg";
    public static final String _ID = "_id";
    public static final String MSG_ID = "msg_id";
    public static final String BOTTOM_MSG = "message";
    public static final String ORDER_ASC = MSG_ID + " ASC";
    public static final String ORDER_DESC = MSG_ID + " DESC";

    public static final Uri BOTTOM_MSG_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_MSG);
    public static final String[] PROJECTION = new String[] { MSG_ID, BOTTOM_MSG };

    private static final int URL_MSG = 1;
    private static final int URL_ID = 2;

    private DatabaseHelper mOpenHelper;

    private static final UriMatcher s_urlMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        s_urlMatcher.addURI(AUTHORITY, TABLE_MSG, URL_MSG);
        s_urlMatcher.addURI(AUTHORITY, TABLE_MSG + "/#", URL_ID);
    }

    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        // TODO Auto-generated method stub
        Log.d(TAG, "[delete] url : " + url + ", where : " + where
                + ", whereArgs : " + whereArgs);
        int count = 0;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = s_urlMatcher.match(url);
        switch (match) {
        case URL_MSG:
        case URL_ID:
            count = db.delete(TABLE_MSG, where, whereArgs);
            break;
        }

        return count;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri url, ContentValues contentValues) {
        // TODO Auto-generated method stub
        Log.d(TAG, "[insert] url : " + url);
        Uri result = null;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = s_urlMatcher.match(url);

        switch (match) {
        case URL_MSG:
        case URL_ID:
            long rowID = db.insert(TABLE_MSG, null, contentValues);
            if (rowID > 0) {
                result = ContentUris.withAppendedId(url, rowID);
            }

            break;
        }

        return result;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri url, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
        // TODO Auto-generated method stub
        Log.d(TAG, "[query] url : " + url + ", selection : " + selection
                + ", selectionArgs : " + selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_MSG);

        int match = s_urlMatcher.match(url);
        switch (match) {
        case URL_MSG:
        case URL_ID:
            break;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs, null,
                null, sort);

        return ret;
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        // TODO Auto-generated method stub
        return 0;
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
            db.execSQL("CREATE TABLE " + TABLE_MSG + "(" + _ID
                    + " INTEGER PRIMARY KEY," + MSG_ID + " INTEGER,"
                    + BOTTOM_MSG + " TEXT);");
        }
    }
}
