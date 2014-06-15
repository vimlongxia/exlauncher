package com.vim.exlauncher.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

public class MsgContentUtils {
    private static final String TAG = "MsgContentUtils";

    public static void inserIntoMsgTable(Context context, String msgNo, String bottomMsg){
        ContentValues contentValues = new ContentValues();
        contentValues.put(BottomMsgContentProvider.MSG_ID, msgNo);
        contentValues.put(BottomMsgContentProvider.BOTTOM_MSG, bottomMsg);
        
        Uri insertUri = context.getContentResolver().insert(BottomMsgContentProvider.BOTTOM_MSG_URI, contentValues);
        Log.d(TAG, "[inserIntoMsgTable] insertUri : " + insertUri);
    }
    
    public static boolean isNewMsg(Context context, String msgNo) {
        boolean isNew = false;
        String selection = BottomMsgContentProvider.MSG_ID + "=?";
        String[] selectionArgs = new String[] { msgNo + "" };
        Cursor cursor = context.getContentResolver().query(
                BottomMsgContentProvider.BOTTOM_MSG_URI,
                BottomMsgContentProvider.PROJECTION, selection, selectionArgs,
                null);

        if (cursor == null) {
            return true;
        }

        if (cursor.getCount() == 0) {
            isNew = true;
        } else {
            isNew = false;
        }

        cursor.close();
        Log.d(TAG, "[isNewMsg] msgNo : " + msgNo + ", isNew : " + isNew);
        return isNew;
    }

    public static boolean reachMaxNum(Context context) {
        boolean reach = false;
        Cursor cursor = context.getContentResolver().query(
                BottomMsgContentProvider.BOTTOM_MSG_URI,
                BottomMsgContentProvider.PROJECTION, null, null, null);

        if (cursor == null) {
            return false;
        }

        reach = !(cursor.getCount() < JsonAdData.MAX_RECENT_MSG);

        cursor.close();
        Log.d(TAG, "[reachMaxNum] reach : " + reach);
        return reach;
    }

    public static int getEarlestMsgId(Context context) {
        int earlestMsgId = -1;

        Cursor cursor = context.getContentResolver().query(
                BottomMsgContentProvider.BOTTOM_MSG_URI, null, null, null,
                BottomMsgContentProvider.ORDER_ASC);

        if ((cursor == null) || (cursor.getCount() == 0)) {
            return -1;
        }

        try {
            if (cursor.moveToFirst()) {
                earlestMsgId = cursor.getInt(cursor
                        .getColumnIndex(BottomMsgContentProvider.MSG_ID));
            } else {
                earlestMsgId = -1;
            }
        } catch (Exception e) {
            Log.e(TAG, "[getEarlestMsgId] error! e : " + e);
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        Log.d(TAG, "[getEarlestMsgId] earlestMsgId : " + earlestMsgId);
        return earlestMsgId;
    }

    public static void deleteMsgById(Context context, String msgId) {
        String selection = BottomMsgContentProvider.MSG_ID + "=?";
        String selectionArgs[] = new String[] { msgId + "" };

        int row = context.getContentResolver().delete(
                BottomMsgContentProvider.BOTTOM_MSG_URI, selection,
                selectionArgs);

        Log.d(TAG, "[deleteMsgById] msgId : " + msgId + ", row : " + row);
    }
    
    public static Cursor getAllBottomMsgOrderByDesc(Context context) {
        Cursor cursor = context.getContentResolver().query(
                BottomMsgContentProvider.BOTTOM_MSG_URI,null,null,
                null, BottomMsgContentProvider.ORDER_DESC);
        
        Log.d(TAG, "[getAllBottomMsgOrderByDesc] cursor : " + cursor);
        if (cursor != null) {
            Log.d(TAG, "[getAllBottomMsgOrderByDesc] cursor counts : " + cursor.getCount());
        }
        
        return cursor;
    }
}
