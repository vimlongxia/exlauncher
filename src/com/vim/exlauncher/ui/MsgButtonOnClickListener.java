package com.vim.exlauncher.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.BottomMsgContentProvider;
import com.vim.exlauncher.data.LauncherUtils;
import com.vim.exlauncher.data.MsgContentUtils;

public class MsgButtonOnClickListener implements OnClickListener {
    private static final String TAG = "MsgButtonOnClickListener";
    private Context mContext;
    private static Cursor mMsgCursor;

    public static Cursor getMsgCursor() {
        return mMsgCursor;
    }

    private boolean isMsgDatabaseEmpty() {
        return ((mMsgCursor == null) || (mMsgCursor.getCount() == 0));
    }

    public MsgButtonOnClickListener(Context context) {
        mContext = context;
    }

    private class MsgCursorAdapter extends CursorAdapter {
        private LayoutInflater mInflater;

        public MsgCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, true);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // TODO Auto-generated method stub
            int msgId = cursor.getInt(cursor
                    .getColumnIndex(BottomMsgContentProvider.MSG_ID));
            String bottomMsg = cursor.getString(cursor
                    .getColumnIndex(BottomMsgContentProvider.BOTTOM_MSG));

            TextView tvMsgId = (TextView) view.findViewById(R.id.tv_msg_id);
            TextView tvBottomMsg = (TextView) view
                    .findViewById(R.id.tv_bottom_msg);

            tvMsgId.setText(msgId + "");
            tvBottomMsg.setText(bottomMsg);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = mInflater.inflate(R.layout.msg_list_item_layout, null);
            return view;
        }
    }

    private View getMsgLisView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View listLayout = inflater.inflate(R.layout.msg_list_layout, null);
        LinearLayout llMsgLayout = (LinearLayout) listLayout
                .findViewById(R.id.ll_list_show);
        TextView tvNoMsg = (TextView) listLayout.findViewById(R.id.tv_no_msg);
        final TextView tvMsgContent = (TextView) listLayout
                .findViewById(R.id.tv_msg_content);

        ListView msgList = (ListView) listLayout.findViewById(R.id.lv_msg);
        msgList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int index, long id) {
                // TODO Auto-generated method stub
                if (mMsgCursor.moveToPosition(index)) {
                    String bottomMsg = mMsgCursor.getString(mMsgCursor
                            .getColumnIndex(BottomMsgContentProvider.BOTTOM_MSG));
                    tvMsgContent.setText(bottomMsg);
                }
            }
        });

        tvNoMsg.setVisibility(isMsgDatabaseEmpty() ? View.VISIBLE : View.GONE);
        llMsgLayout.setVisibility(isMsgDatabaseEmpty() ? View.GONE
                : View.VISIBLE);

        msgList.setItemsCanFocus(false);
        msgList.setFocusable(true);

        MsgCursorAdapter msgAdapter = new MsgCursorAdapter(mContext, mMsgCursor);
        msgList.setAdapter(msgAdapter);

        return listLayout;
    }

    private void showMsgBox() {
        mMsgCursor = MsgContentUtils.getAllBottomMsgOrderByDesc(mContext);
        View listLayout = getMsgLisView();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(listLayout);
        builder.setTitle(R.string.msg_list_title);
        builder.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub
                if (mMsgCursor != null) {
                    mMsgCursor.close();
                    mMsgCursor = null;
                }
            }
        });
        AlertDialog dlg = builder.create();
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.alpha = 0.8f;
        lp.dimAmount = 0.0f;
        dlg.getWindow().setAttributes(lp);
        dlg.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dlg.show();
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        String wifiMac = LauncherUtils.getWifiMac(ExLauncher.mContext);
        Log.d(TAG, "wifi mac : " + wifiMac);
        if (!TextUtils.isEmpty(wifiMac)) {
            Log.d(TAG, "format wifi mac : " + wifiMac.replace(":", ""));
        }

        String ethMac = LauncherUtils.getEthMac(ExLauncher.ETH_ADDRESS_PATH);
        Log.d(TAG, "ethernet mac : " + ethMac);
        if (!TextUtils.isEmpty(ethMac)) {
            Log.d(TAG, "format ethernet mac : " + ethMac.replace(":", ""));
        }

        showMsgBox();
        ExLauncher.resetMsgStatusAndRefresh(false);
    }
}
