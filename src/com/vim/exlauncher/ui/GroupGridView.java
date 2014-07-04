package com.vim.exlauncher.ui;

import java.lang.reflect.Method;
import java.util.List;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemSelectedListener;

public class GroupGridView extends GridView {
    private Context mContext;
    private static final String TAG = "GroupGridView";

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

    final class GridViewOnItemClickListener implements OnItemClickListener {
        private Context mContext;
        private List<ApkInfo> mListApp;

        public GridViewOnItemClickListener(Context context, List<ApkInfo> list) {
            mContext = context;
            mListApp = list;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // TODO Auto-generated method stub
            logd("[onItemClick] view : " + view + ", position : " + position);
            if (position == (mListApp.size() - 1)) {
                AllGroupsActivity.startCustomAct(mContext, view);
            } else {
                mContext.startActivity(mListApp.get(position).getIntent());
            }
        }
    }

    final class GridViewOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            logd("[onFocusChange] v : " + v + ", hasFocus : " + hasFocus);
            if (!hasFocus) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<GridView> c = (Class<GridView>) Class
                            .forName("android.widget.GridView");
                    Method[] flds = c.getDeclaredMethods();
                    for (Method f : flds) {
                        if ("setSelectionInt".equals(f.getName())) {
                            f.setAccessible(true);
                            f.invoke(v, new Object[] { Integer.valueOf(-1) });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ((GridView) v).setSelection(0);
                AllGroupsActivity.setCurGridFocusIndex(0);
            }
        }
    }

    final class GridViewOnKeyListener implements OnKeyListener {
        private Animation mAnimLeftIn;
        private Animation mAnimLeftOut;
        private Animation mAnimRightIn;
        private Animation mAnimRightOut;

        private ViewFlipper mFlipper;

        public GridViewOnKeyListener(Context context, ViewFlipper viewFlipper) {
            mFlipper = viewFlipper;

            mAnimLeftIn = AnimationUtils.loadAnimation(context,
                    R.anim.push_left_in);
            mAnimLeftOut = AnimationUtils.loadAnimation(context,
                    R.anim.push_left_out);
            mAnimRightIn = AnimationUtils.loadAnimation(context,
                    R.anim.push_right_in);
            mAnimRightOut = AnimationUtils.loadAnimation(context,
                    R.anim.push_right_out);
        }

        private boolean processLeftKey(View view) {
            boolean ret = false;

            if (((AllGroupsActivity.getCurGridFocusIndex() % GroupGridViewAdapter.COLUMN_NUMBER) == 0)
                    || (((GridView) view).getCount() == 0)) {
                mFlipper.setInAnimation(mAnimLeftIn);
                mFlipper.setOutAnimation(mAnimLeftOut);
                mFlipper.showPrevious();

                if (AllGroupsActivity.getCurGridPage() == AllApps.INDEX_VIDEO) {
                    AllGroupsActivity.setCurGridPage(AllApps.INDEX_LOCAL);
                } else {
                    AllGroupsActivity.setCurGridPage(AllGroupsActivity
                            .getCurGridPage() - 1);
                }

                ret = true;
            }

            return ret;
        }

        private boolean processRightKey(View view) {
            boolean ret = false;

            if (((AllGroupsActivity.getCurGridFocusIndex() % GroupGridViewAdapter.COLUMN_NUMBER) == (GroupGridViewAdapter.COLUMN_NUMBER - 1))
                    || (AllGroupsActivity.getCurGridFocusIndex() == (((GridView) view)
                            .getCount() - 1))
                    || (((GridView) view).getCount() == 0)) {
                mFlipper.setInAnimation(mAnimRightIn);
                mFlipper.setOutAnimation(mAnimRightOut);
                mFlipper.showNext();

                if (AllGroupsActivity.getCurGridPage() == AllApps.INDEX_LOCAL) {
                    AllGroupsActivity.setCurGridPage(AllApps.INDEX_VIDEO);
                } else {
                    AllGroupsActivity.setCurGridPage(AllGroupsActivity
                            .getCurGridPage() + 1);
                }

                ret = true;
            }

            return ret;
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            logd("[onKey] view : " + view + ", keyCode : " + keyCode
                    + ", event : " + event);

            logd("childs count : " + ((GridView) view).getCount());
            logd("AllGroupsActivity.getCurGridFocusIndex : "
                    + AllGroupsActivity.getCurGridFocusIndex());

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (processLeftKey(view)) {
                        return true;
                    } else {
                        break;
                    }

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (processRightKey(view)) {
                        return true;
                    } else {
                        break;
                    }
                }
            }
            return view.onKeyDown(keyCode, event);
        }

    }

    static class GridViewOnItemSelectedListener implements
            OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
            // TODO Auto-generated method stub
            logd("[onItemSelected] parent : " + parent + ", view : " + view
                    + ", position : " + position + ", id : " + id);
            AllGroupsActivity.setCurGridFocusIndex(position);

            BaseAdapter adapter = (BaseAdapter) ((GridView) parent)
                    .getAdapter();
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }

}
