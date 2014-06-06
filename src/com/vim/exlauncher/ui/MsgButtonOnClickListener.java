package com.vim.exlauncher.ui;

import java.util.ArrayList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.JsonAdData;

public class MsgButtonOnClickListener implements OnClickListener {
    private static final String TAG = "MsgButtonOnClickListener";
    private Context mContext;

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

    private void showMsgBox() {
        final ArrayList<String> msgArrayList = getMsgsFromSharedPref();

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
            public void onItemClick(AdapterView<?> parent, View view, int index,
                    long id) {
                // TODO Auto-generated method stub
                tvMsgContent.setText(msgArrayList.get(index));
            }
        });

        tvNoMsg.setVisibility(msgArrayList.isEmpty() ? View.VISIBLE : View.GONE);
        llMsgLayout.setVisibility(msgArrayList.isEmpty() ? View.GONE
                : View.VISIBLE);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(mContext,
                R.layout.msg_list_item_layout, msgArrayList);
        msgList.setAdapter(listAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(listLayout);
        AlertDialog dlg = builder.create();
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        dlg.getWindow().setAttributes(lp);
        dlg.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dlg.show();
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        showMsgBox();
    }
}
