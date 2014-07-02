package com.vim.exlauncher.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class GridViewOnItemSelectedListener implements OnItemSelectedListener{

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub
        AllGroupsActivity.setCurGridFocusIndex(position);
        
        BaseAdapter adapter = (BaseAdapter) ((GridView) parent).getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        
    }

}
