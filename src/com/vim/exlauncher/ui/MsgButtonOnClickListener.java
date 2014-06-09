package com.vim.exlauncher.ui;

import java.util.ArrayList;
import java.util.Map;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.JsonAdData;
import com.vim.exlauncher.data.LauncherUtils;
import com.vim.exlauncher.data.MsgListAdapter;

public class MsgButtonOnClickListener implements OnClickListener {
    private static final String TAG = "MsgButtonOnClickListener";
    private Context mContext;
    private static int mFocusPostion;

    public MsgButtonOnClickListener(Context context) {
        mContext = context;
    }

    private ArrayList<String> getMsgsFromSharedPref() {
        SharedPreferences spMsg = mContext.getSharedPreferences(
                JsonAdData.MSG_SHARED_PREF, Context.MODE_PRIVATE);

        Map<String, ?> allMsgs = spMsg.getAll();
        ArrayList<String> msgArrayList = new ArrayList<String>();

        for (Map.Entry<String, ?> entry : allMsgs.entrySet()) {
            msgArrayList.add(entry.getValue().toString());
        }

        return msgArrayList;
    }

    private View getMsgLisView(final ArrayList<String> msgArrayList) {
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
                tvMsgContent.setText(msgArrayList.get(index));
            }
        });

        msgList.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                mFocusPostion = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        tvNoMsg.setVisibility(msgArrayList.isEmpty() ? View.VISIBLE : View.GONE);
        llMsgLayout.setVisibility(msgArrayList.isEmpty() ? View.GONE
                : View.VISIBLE);

        msgList.setItemsCanFocus(false);
        msgList.setFocusable(true);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mContext,
                R.layout.msg_list_item_layout, msgArrayList);
        msgList.setAdapter(listAdapter);

        // MsgListAdapter listAdapter = new MsgListAdapter(mContext,
        // msgArrayList);
        // msgList.setAdapter(listAdapter);

        return listLayout;
    }

    public static int getFocusPosition() {
        return mFocusPostion;
    }

    private void showMsgBox() {
        ArrayList<String> msgArrayList = getMsgsFromSharedPref();
        View listLayout = getMsgLisView(msgArrayList);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(listLayout);
        builder.setTitle(R.string.msg_list_title);
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
    }
}
