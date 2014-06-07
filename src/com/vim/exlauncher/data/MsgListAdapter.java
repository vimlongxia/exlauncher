package com.vim.exlauncher.data;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.ui.MsgButtonOnClickListener;

public class MsgListAdapter extends BaseAdapter {
    private ArrayList<String> mMsgList;
    private LayoutInflater mInflater;

    public MsgListAdapter(Context context, ArrayList<String> msgList) {
        mMsgList = msgList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mMsgList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater
                    .inflate(R.layout.msg_list_item_layout, null);
            holder = new ViewHolder();
            holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvItem.setText(mMsgList.get(position));

        // if (position == MsgButtonOnClickListener.getFocusPosition()) {
        // holder.tvItem.setBackgroundColor(Color.YELLOW);
        // } else {
        // holder.tvItem.setBackgroundColor(Color.BLACK);
        // }

         convertView.setFocusableInTouchMode(true);

        return convertView;
    }

    class ViewHolder {
        TextView tvItem;
    }
}
