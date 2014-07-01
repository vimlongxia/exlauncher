package com.vim.exlauncher.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;

public class GroupGridLayout extends GridLayout {
    private final static String TAG = "GroupGridLayout";
    private Context mContext;
    private final String strCameraApp = "com.android.camera.CameraLauncher";

    public GroupGridLayout(Context context) {
        super(context);
    }

    public GroupGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public GroupGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void addTheAddImg() {
        ViewGroup apkItem = (ViewGroup) View.inflate(mContext,
                R.layout.layout_grid_item, null);

        ((ImageView) apkItem.getChildAt(0))
                .setImageResource(R.drawable.item_img_add);
        ((TextView) apkItem.getChildAt(1)).setText(R.string.str_add);

        this.addView(apkItem);
    }

    public void setLayoutView(List<ApkInfo> list) {
        int index = 0;

        if (this.getChildCount() > 0)
            this.removeAllViews();

        for (ApkInfo apkInfo : list) {
            index++;

            ViewGroup apkItem = (ViewGroup) View.inflate(mContext,
                    R.layout.layout_grid_item, null);
            ((TextView) apkItem.getChildAt(1)).setText(apkInfo.getTitle());

            ImageView imgBg = (ImageView) apkItem.getChildAt(0);
            imgBg.setBackgroundResource(parseItemBackground(index));
            imgBg.setImageDrawable(apkInfo.getIcon());

             apkItem.setOnKeyListener(new GridItemOnKeyListener(mContext, apkInfo.getIntent(), AllGroupsActivity.mFlipper));
//             apkItem.setOnTouchListener(new MyOnTouchListener());

            apkItem.setFocusable(true);
            apkItem.setFocusableInTouchMode(true);
            apkItem.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            this.addView(apkItem);
        }

        if (this.getId() != R.id.gv_shortcut_app)
            addTheAddImg();
    }

    private int parseItemBackground(int num) {
        switch (num % 8 + 1) {
        case 1:
            return R.drawable.item_child_1;
        case 2:
            return R.drawable.item_child_2;
        case 3:
            return R.drawable.item_child_3;
        case 4:
            return R.drawable.item_child_4;
        case 5:
            return R.drawable.item_child_5;
        case 6:
            return R.drawable.item_child_6;
        case 7:
            return R.drawable.item_child_7;
        case 8:
            return R.drawable.item_child_8;
        default:
            return R.drawable.item_child_1;
        }
    }
}
