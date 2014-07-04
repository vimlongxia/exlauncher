package com.vim.exlauncher.ui;

import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.RelativeLayout.LayoutParams;

import com.vim.exlauncher.R;
import com.vim.exlauncher.data.ApkInfo;

public class GroupGridLayout extends GridLayout {
    private final static String TAG = "GroupGridLayout";
    private Context mContext;

    public GroupGridLayout(Context context) {
        super(context);
        mContext = context;
    }

    public GroupGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public GroupGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setLayoutView(final List<ApkInfo> list) {
        int index = 0;

        logd("[setLayoutView] list counts " + list.size());

        if (this.getChildCount() > 0)
            this.removeAllViews();

        for (ApkInfo apkInfo : list) {
            index++;

            View view = View.inflate(mContext,
                    R.layout.layout_grid_layout_item, null);

            ImageView iv = (ImageView) view.findViewById(R.id.item_bg);
            TextView tv = (TextView) view.findViewById(R.id.item_name);

            view.setBackgroundResource(AllGroupsActivity
                    .getItemBackground(index));
            iv.setImageDrawable(apkInfo.getIcon());
            tv.setText(apkInfo.getTitle());

            view.setOnKeyListener(new GridLayoutItemOnKeyListener(
                    AllGroupsActivity.getAllGroupActivity(), apkInfo
                            .getIntent(), AllGroupsActivity.mFlipper));

            // for the mouse click
            // view.setOnTouchListener(new
            // GridLayoutItemOnTouchListener(mContext,
            // apkInfo.getIntent(), AllGroupsActivity.mFlipper));

            view.setOnFocusChangeListener(new GridLayoutItemOnFocusChangeListener(
                    mContext));

            view.setOnClickListener(new GridLayoutItemOnClickListener(
                    AllGroupsActivity.getAllGroupActivity(), apkInfo
                            .getIntent()));
            view.setClickable(true);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);

            logd("[setLayoutView] call addView");
            this.addView(view);
        }
    }

    final class GridLayoutItemOnTouchListener implements OnTouchListener {
        private Context mContext;
        private Intent mIntent;

        public GridLayoutItemOnTouchListener(Context context, Intent intent) {
            mContext = context;
            mIntent = intent;
        }

        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            logd("[onTouch] view : " + view + ", event action : "
                    + event.getAction());

            if (event.getAction() == MotionEvent.ACTION_UP) {
                mContext.startActivity(mIntent);
            }

            return false;
        }
    }

    final class GridLayoutItemOnFocusChangeListener implements
            OnFocusChangeListener {
        private Context mContext;

        private static final float frameParam = 1.01f;
        private static final float scaleParam = 1.1f;

        public GridLayoutItemOnFocusChangeListener(Context context) {
            mContext = context;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            // TODO Auto-generated method stub
            logd("[onFocusChange] view : " + view + ", hasFocus : " + hasFocus);
            setShadowEffect(view);

            AllGroupsActivity.sRlFocusUnit.setVisibility(View.VISIBLE);
            AllGroupsActivity.sIvFrame.setVisibility(View.VISIBLE);
            AllGroupsActivity.sIvFocusBg.setVisibility(View.VISIBLE);
        }

        private void setShadowEffect(View view) {
            AllGroupsActivity.sIvFrame.bringToFront();
            AllGroupsActivity.sRlFocusUnit.bringToFront();

            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            AllGroupsActivity.sIvFocusBg.setBackground(view.getBackground());
            setFocusBgPosition(rect);

            setFramePosition(rect);

            ImageView ivFocus = (ImageView) AllGroupsActivity.sRlFocusUnit
                    .findViewById(R.id.iv_focus);
            TextView tvFocus = (TextView) AllGroupsActivity.sRlFocusUnit
                    .findViewById(R.id.tv_focus);

            ImageView ivCur = (ImageView) view.findViewById(R.id.item_bg);
            TextView tvCur = (TextView) view.findViewById(R.id.item_name);

            // set focus bitmap
            ivCur.buildDrawingCache();
            Bitmap bmp = ivCur.getDrawingCache();
            logd("[setShadowEffect] bmp : " + bmp);

            Bitmap scaleBitmap = zoomBitmap(bmp,
                    (int) (rect.width() * scaleParam),
                    (int) (rect.height() * scaleParam));
            ivCur.destroyDrawingCache();
            ivFocus.setImageBitmap(scaleBitmap);

            // set focus text
            String textString = tvCur.getText().toString();
            tvFocus.setText(textString);

            // get the rect to set scale shadow view position and background
            Bitmap shadowBitmap = BitmapFactory.decodeResource(
                    mContext.getResources(), R.drawable.shadow_child_shortcut);
            int layoutWidth = (shadowBitmap.getWidth() - rect.width()) / 2;
            int layoutHeight = (shadowBitmap.getHeight() - rect.height()) / 2;
            Rect layoutRect = new Rect(rect.left - layoutWidth, rect.top
                    - layoutHeight, rect.right + layoutWidth, rect.bottom
                    + layoutHeight);
            shadowBitmap.recycle();
            System.gc();
            AllGroupsActivity.sRlFocusUnit
                    .setBackgroundResource(R.drawable.shadow_child_shortcut);
            setViewPosition(AllGroupsActivity.sRlFocusUnit, layoutRect);
        }

        private void setViewPosition(View view, Rect rect) {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            lp.width = rect.width();
            lp.height = rect.height();
            lp.x = rect.left;
            lp.y = rect.top;

            view.setLayoutParams(lp);
        }

        private void setFocusBgPosition(Rect rect) {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            int rectWidth = rect.right - rect.left;
            int rectHeight = rect.bottom - rect.top;

            lp.width = rectWidth;
            lp.height = rectHeight;
            lp.x = rect.left;
            lp.y = rect.top;

            AllGroupsActivity.sIvFocusBg.setLayoutParams(lp);
        }
        
        private void setFramePosition(Rect rect) {
            android.widget.AbsoluteLayout.LayoutParams lp = new android.widget.AbsoluteLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
            int rectWidth = rect.right - rect.left;
            int rectHeight = rect.bottom - rect.top;
            
            lp.width = (int) (rectWidth * frameParam);
            lp.height = (int) (rectHeight * frameParam);
            lp.x = rect.left + (int) ((rectWidth - lp.width) / 2);
            lp.y = rect.top + (int) ((rectHeight - lp.height) / 2);
            
            AllGroupsActivity.sIvFrame.setLayoutParams(lp);
        }

        private Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = (float) w / width;
            float scaleHeight = (float) h / height;
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                    matrix, true);

            return newBmp;
        }
    }

    final class GridLayoutItemOnClickListener implements OnClickListener {
        private Activity mActivity;
        private Intent mIntent;

        public GridLayoutItemOnClickListener(Activity activity, Intent intent) {
            mActivity = activity;
            mIntent = intent;
        }

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            logd("[onClik] view : " + view);
            if (mIntent != null) {
                mActivity.startActivity(mIntent);
            } else {
                // this is the add item
                Rect rect = new Rect();
                view.getGlobalVisibleRect(rect);

                AllGroupsActivity.setPopWindow(mActivity, rect.top - 10,
                        rect.bottom + 10);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.putExtra(CustomActivity.STR_TOP, rect.top - 10);
                intent.putExtra(CustomActivity.STR_BOTTOM, rect.bottom + 10);
                intent.putExtra(CustomActivity.STR_LEFT, rect.left);
                intent.putExtra(CustomActivity.STR_RIGHT, rect.right);
                intent.setComponent(new ComponentName("com.vim.exlauncher",
                        "com.vim.exlauncher.ui.CustomActivity"));
                intent.putExtra(CustomActivity.GROUP_TYPE,
                        AllGroupsActivity.getCurGridPage());
                mActivity.startActivity(intent);
            }
        }
    }

    final class GridLayoutItemOnKeyListener implements OnKeyListener {
        private Activity mActivity;
        private Intent mIntent;
        private ViewFlipper mFlipper;

        private Animation mAnimLeftIn;
        private Animation mAnimLeftOut;
        private Animation mAnimRightIn;
        private Animation mAnimRightOut;

        public GridLayoutItemOnKeyListener(Activity activity, Intent intent,
                ViewFlipper viewFlipper) {
            mActivity = activity;
            mIntent = intent;
            mFlipper = viewFlipper;

            mAnimLeftIn = AnimationUtils.loadAnimation(mActivity,
                    R.anim.push_left_in);
            mAnimLeftOut = AnimationUtils.loadAnimation(mActivity,
                    R.anim.push_left_out);
            mAnimRightIn = AnimationUtils.loadAnimation(mActivity,
                    R.anim.push_right_in);
            mAnimRightOut = AnimationUtils.loadAnimation(mActivity,
                    R.anim.push_right_out);
        }

        private void processRightKey(View view) {
            synchronized (AllGroupsActivity.BoundaryObj) {
                if (isNextItemNull(view, View.FOCUS_RIGHT)) {
                    AllGroupsActivity.sBoundaryCount = 0;
                    mFlipper.setInAnimation(mAnimRightIn);
                    mFlipper.setOutAnimation(mAnimRightOut);
                    mFlipper.showNext();
                }
            }
        }

        private boolean isNextItemNull(View view, int dec) {
            ViewGroup gridLayout = (ViewGroup) view.getParent();
            View nextView = FocusFinder.getInstance().findNextFocus(gridLayout,
                    gridLayout.findFocus(), dec);
            logd("[isNextItemNull] view : " + view + ", nextView : " + nextView
                    + ", dec : " + dec + ", sBoundaryCount :"
                    + AllGroupsActivity.sBoundaryCount);
            // if (nextView == null) {
            // AllGroupsActivity.sBoundaryCount++;
            // } else {
            // AllGroupsActivity.sBoundaryCount = 0;
            // }

            if (nextView != null) {
                return false;
            } else {
                AllGroupsActivity.sRlFocusUnit.setVisibility(View.INVISIBLE);
                AllGroupsActivity.sIvFrame.setVisibility(View.INVISIBLE);
                AllGroupsActivity.sIvFocusBg.setVisibility(View.INVISIBLE);
                return true;
            }
        }

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            logd("[onKey] keyCode : " + keyCode + ", event : " + event);

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:

                    if (mIntent != null) {
                        mActivity.startActivity(mIntent);
                    } else {
                        // this is the add item
                        Rect rect = new Rect();
                        view.getGlobalVisibleRect(rect);

                        AllGroupsActivity.setPopWindow(mActivity,
                                rect.top - 10, rect.bottom + 10);

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.putExtra(CustomActivity.STR_TOP, rect.top - 10);
                        intent.putExtra(CustomActivity.STR_BOTTOM,
                                rect.bottom + 10);
                        intent.putExtra(CustomActivity.STR_LEFT, rect.left);
                        intent.putExtra(CustomActivity.STR_RIGHT, rect.right);
                        intent.setComponent(new ComponentName(
                                "com.vim.exlauncher",
                                "com.vim.exlauncher.ui.CustomActivity"));
                        intent.putExtra(CustomActivity.GROUP_TYPE,
                                AllGroupsActivity.getCurGridPage());
                        mActivity.startActivity(intent);
                    }
                    return true;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    synchronized (AllGroupsActivity.BoundaryObj) {
                        if (isNextItemNull(view, View.FOCUS_LEFT)) {
                            AllGroupsActivity.sBoundaryCount = 0;
                            mFlipper.setInAnimation(mAnimLeftIn);
                            mFlipper.setOutAnimation(mAnimLeftOut);
                            mFlipper.showPrevious();
                            return true;
                        } else {
                            break;
                        }
                    }

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    synchronized (AllGroupsActivity.BoundaryObj) {
                        if (isNextItemNull(view, View.FOCUS_RIGHT)) {
                            AllGroupsActivity.sBoundaryCount = 0;
                            mFlipper.setInAnimation(mAnimRightIn);
                            mFlipper.setOutAnimation(mAnimRightOut);
                            mFlipper.showNext();
                            return true;
                        } else {
                            break;
                        }
                    }

                default:
                    logd("[onKey] this keyCode has been discarded");
                    break;
                }
            }

            return view.onKeyDown(keyCode, event);
        }
    }

    private static void logd(String strs) {
        Log.d(TAG, strs);
    }

    private static void loge(String strs) {
        Log.e(TAG, strs);
    }
}
