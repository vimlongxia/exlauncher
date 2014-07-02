package com.vim.exlauncher.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

public class GroupGridView extends GridView {
    private Context mContext;

    public GroupGridView(Context context) {
        super(context);
        mContext = context;
    }

    public GroupGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public GroupGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
    }
}
