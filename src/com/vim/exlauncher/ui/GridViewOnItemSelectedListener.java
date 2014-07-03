package com.vim.exlauncher.ui;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class GridViewOnItemSelectedListener implements OnItemSelectedListener {
    private static final String TAG = "GridViewOnItemSelectedListener";

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub
        logd("[onItemSelected] parent : " + parent + ", view : " + view
                + ", position : " + position + ", id : " + id);
        AllGroupsActivity.setCurGridFocusIndex(position);

        BaseAdapter adapter = (BaseAdapter) ((GridView) parent).getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
