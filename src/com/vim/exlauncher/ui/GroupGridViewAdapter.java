package com.vim.exlauncher.ui;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;

public class GroupGridViewAdapter extends BaseAdapter {
    private static final String TAG = "GroupGridViewAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ApkInfo> mListApk;

    private Animation mAnimFocus;
    private Animation mAnimUnFocus;

    private static final int ROW_NUMBER = 3;
    public static final int COLUMN_NUMBER = 6;

    public GroupGridViewAdapter(Context context, List<ApkInfo> apkList) {
        mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListApk = apkList;

        mAnimFocus = AnimationUtils.loadAnimation(mContext, R.anim.anim_focus);
        mAnimUnFocus = AnimationUtils.loadAnimation(mContext,
                R.anim.anim_unfocus);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListApk.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mListApk.get(position);
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
            convertView = mInflater.inflate(R.layout.layout_grid_item, parent,
                    false);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.item_bg);
        TextView tv = (TextView) convertView.findViewById(R.id.item_name);

        convertView.setBackgroundResource(AllGroupsActivity.getItemBackground(position + 1));
        iv.setImageDrawable(mListApk.get(position).getIcon());
        tv.setText(mListApk.get(position).getTitle());
        
        if (position == 0) {
            logd("mListApk.get(position).getTitle() : " + mListApk.get(position).getTitle());
        }

        // if (position == AllGroupsActivity.getCurGridFocusIndex()) {
        // iv.setScaleType(ScaleType.CENTER_CROP);
        // rl.startAnimation(mAnimFocus);
        // } else if (position == AllGroupsActivity.getPreGridFocusIndex()) {
        // rl.startAnimation(mAnimUnFocus);
        // } else {
        // iv.setScaleType(ScaleType.CENTER_INSIDE);
        // }

        int gvHeight = parent.getHeight();
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                (gvHeight - 5 * (ROW_NUMBER - 1)) / ROW_NUMBER);
        convertView.setLayoutParams(params);

        // convertView.setOnKeyListener(new GridItemOnKeyListener(mContext,
        // mListApk.get(position).getIntent(), AllGroupsActivity.mFlipper));

        return convertView;
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
