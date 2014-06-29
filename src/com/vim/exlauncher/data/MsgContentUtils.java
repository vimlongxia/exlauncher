package com.vim.exlauncher.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MsgContentUtils {
    private static final String TAG = "MsgContentUtils";

    public static void inserIntoMsgTable(Context context, String msgNo,
            String bottomMsg) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExLauncherContentProvider.TABLE_MSG_MSG_ID, msgNo);
        contentValues.put(ExLauncherContentProvider.TABLE_MSG_BOTTOM_MSG,
                bottomMsg);

        Uri insertUri = context.getContentResolver().insert(
                ExLauncherContentProvider.URI_MSG, contentValues);
        logd("[inserIntoMsgTable] insertUri : " + insertUri);
    }

    public static boolean isNewMsg(Context context, String msgNo) {
        boolean isNew = false;
        String selection = ExLauncherContentProvider.TABLE_MSG_MSG_ID + "=?";
        String[] selectionArgs = new String[] { msgNo + "" };
        Cursor cursor = context.getContentResolver().query(
                ExLauncherContentProvider.URI_MSG,
                ExLauncherContentProvider.PROJECTION_MSG, selection,
                selectionArgs, null);

        if (cursor == null) {
            logd("[isNewMsg] cursor is null, this msg " + msgNo + " is new.");
            return true;
        }

        if (cursor.getCount() == 0) {
            logd("[isNewMsg] cursor counts 0, this msg " + msgNo + " is new.");
            isNew = true;
        } else {
            isNew = false;
        }

        cursor.close();
        logd("[isNewMsg] msgNo : " + msgNo + ", isNew : " + isNew);
        return isNew;
    }

    public static boolean reachMaxNum(Context context) {
        boolean reach = false;
        Cursor cursor = context.getContentResolver().query(
                ExLauncherContentProvider.URI_MSG,
                ExLauncherContentProvider.PROJECTION_MSG, null, null, null);

        if (cursor == null) {
            logd("[reachMaxNum] cursor is null, we don't get anything, just return false");
            return false;
        }

        reach = !(cursor.getCount() < JsonAdData.MAX_RECENT_MSG);

        cursor.close();
        logd("[reachMaxNum] reach : " + reach);
        return reach;
    }

    public static int getEarlestMsgId(Context context) {
        int earlestMsgId = -1;

        Cursor cursor = context.getContentResolver().query(
                ExLauncherContentProvider.URI_MSG, null, null, null,
                ExLauncherContentProvider.TABLE_MSG_ORDER_ASC);

        if ((cursor == null) || (cursor.getCount() == 0)) {
            return -1;
        }

        try {
            if (cursor.moveToFirst()) {
                earlestMsgId = cursor
                        .getInt(cursor
                                .getColumnIndex(ExLauncherContentProvider.TABLE_MSG_MSG_ID));
            } else {
                earlestMsgId = -1;
            }
        } catch (Exception e) {
            Log.e(TAG, "[getEarlestMsgId] error! e : " + e);
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        logd("[getEarlestMsgId] earlestMsgId : " + earlestMsgId);
        return earlestMsgId;
    }

    public static void deleteMsgById(Context context, String msgId) {
        String selection = ExLauncherContentProvider.TABLE_MSG_MSG_ID + "=?";
        String selectionArgs[] = new String[] { msgId + "" };

        int row = context.getContentResolver().delete(
                ExLauncherContentProvider.URI_MSG, selection, selectionArgs);

        logd("[deleteMsgById] msgId : " + msgId + ", row : " + row);
    }

    public static void deleteAllMsg(Context context) {
        int row = context.getContentResolver().delete(
                ExLauncherContentProvider.URI_MSG, null, null);

        logd("[deleteMsgById] row : " + row);
    }

    public static Cursor getAllBottomMsgOrderByDesc(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ExLauncherContentProvider.URI_MSG, null, null, null,
                ExLauncherContentProvider.TABLE_MSG_ORDER_DESC);

        logd("[getAllBottomMsgOrderByDesc] cursor : " + cursor);
        if (cursor != null) {
            logd("[getAllBottomMsgOrderByDesc] cursor counts : "
                    + cursor.getCount());
        }

        return cursor;
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }
}
