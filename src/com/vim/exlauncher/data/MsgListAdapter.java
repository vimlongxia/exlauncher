package com.vim.exlauncher.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MsgListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mMsgList; 
    
    public MsgListAdapter(Context context, ArrayList<String> msgList){
        mContext = context;
        mMsgList = msgList;
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
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        return null;
    }

}
