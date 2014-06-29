package com.vim.exlauncher.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageButton;

import com.vim.exlauncher.R;

public class BottomImageButton extends ImageButton {
    public BottomImageButton(Context context) {
        super(context);
    }

    public BottomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    private Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newbmp;
    }

    @SuppressWarnings("deprecation")
    public void setShadowEffect() {
        float bgScalePara = 1.1f;
        Rect layoutRect;
        Bitmap scaleBitmap;
        Bitmap shadowBitmap;

        Rect imgRect = new Rect();
        this.getGlobalVisibleRect(imgRect);
        // setFramePosition(imgRect);

        // this.buildDrawingCache();
        // Bitmap bmp = this.getDrawingCache();
        // scaleBitmap = zoomBitmap(bmp, (int) (imgRect.width() * bgScalePara),
        // (int) (imgRect.height() * bgScalePara));
        // this.destroyDrawingCache();

        BitmapDrawable bmpDrawable = (BitmapDrawable) this.getBackground();
        Bitmap bmp = bmpDrawable.getBitmap();
        scaleBitmap = zoomBitmap(bmp, (int) (imgRect.width() * bgScalePara),
                (int) (imgRect.height() * bgScalePara));

        // shadowBitmap = BitmapFactory.decodeResource(mContext.getResources(),
        // R.drawable.shadow);
        int layout_width = (scaleBitmap.getWidth() - imgRect.width()) / 2;
        int layout_height = (scaleBitmap.getHeight() - imgRect.height()) / 2;
        layoutRect = new Rect(imgRect.left - layout_width, imgRect.top
                - layout_height, imgRect.right + layout_width, imgRect.bottom
                + layout_height);

        ExLauncher.mIvShadow.setImageBitmap(scaleBitmap);
        ExLauncher.mAlShadow.setBackgroundResource(getBgDrawableId());
        ExLauncher.mIvShadowForFirst
                .setVisibility((this.getId() == R.id.ib_lh_first) ? View.VISIBLE : View.GONE);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0, 0);
        lp.width = layoutRect.width();
        lp.height = layoutRect.height();
        lp.x = layoutRect.left;
        lp.y = layoutRect.top;
        ExLauncher.mAlShadow.setLayoutParams(lp);
    }

    private int getBgDrawableId() {
        int bgId = -1;
        switch (this.getId()) {
        case R.id.ib_tv:
        case R.id.ib_movies:
        case R.id.ib_drama:
        case R.id.ib_youtube:
        case R.id.ib_games:
        case R.id.ib_radio:
        case R.id.ib_apps:
        case R.id.ib_setting:
            bgId = R.drawable.shadow_third_forth;
            break;

        default:
            bgId = R.drawable.shadow_bottom;
            break;
        }

        return bgId;
    }
}
