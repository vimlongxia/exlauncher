package com.vim.exlauncher.ui;

import java.util.List;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomGridViewAdapter extends BaseAdapter {
    private static final String TAG = "CustomGridViewAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ApkInfo> mApkList;

    public CustomGridViewAdapter(Context context, List<ApkInfo> list) {
        mContext = context;
        mApkList = list;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mApkList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mApkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.add_apps_grid_item,
                    parent, false);
        }

        FrameLayout fl = (FrameLayout) convertView.findViewById(R.id.fl);
        ImageView itemIcon = (ImageView) convertView
                .findViewById(R.id.item_icon);
        ImageView itemSel = (ImageView) convertView.findViewById(R.id.item_sel);
        TextView itemName = (TextView) convertView.findViewById(R.id.item_name);
        
        ApkInfo apkInfo = mApkList.get(position);
        fl.setBackground(apkInfo.getBg());
        itemIcon.setImageDrawable(apkInfo.getIcon());
        itemSel.setVisibility(apkInfo.getIsSelected() ? View.VISIBLE : View.INVISIBLE);
        itemName.setText(apkInfo.getTitle());

        return convertView;
    }

}
