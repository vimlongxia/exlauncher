package com.vim.exlauncher.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.vim.exlauncher.R;

public class BottomImageButton extends ImageButton {
    private Context mContext;
    
    public BottomImageButton(Context context) {
        super(context);
        mContext = context;
    }

    public BottomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        /*
        int posX = 20;
        int posY = 50;
        int PicWidth,PicHegiht; 
        Drawable drawable = getResources().getDrawable(R.drawable.ic_button_movie);
        Drawable dbe = getResources().getDrawable(R.drawable.ic_button_movie).mutate();//如果不调用mutate方法，则原图也会被改变，因为调用的资源是同一个，所有对象是共享状态的。
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_button_movie);
        PicWidth = drawable.getIntrinsicWidth();
        PicHegiht = drawable.getIntrinsicHeight();
        drawable.setBounds(posX,posY,posX+PicWidth,posY+PicHegiht);
        dbe.setBounds(0, 0, PicWidth, PicHegiht);
        canvas.drawColor(Color.WHITE);//设置画布颜色
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        dbe.setColorFilter(0x7f000000,PorterDuff.Mode.SRC_IN);
        canvas.translate(posX + (int)(0.9 * PicWidth/2), posY + PicHegiht/2);//图像平移为了刚好在原图后形成影子效果。
        canvas.skew(-0.9F, 0.0F);//图像倾斜效果。
        canvas.scale(1.0f, 0.5f);//图像（其实是画布）缩放，Y方向缩小为1/2。
        dbe.draw(canvas);//此处为画原图像影子效果图，比原图先画，则会在下层。
        drawable.clearColorFilter();
        canvas.restore();
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        drawable.draw(canvas);//此处为画原图像，由于canvas有层次效果，因此会盖在影子之上。
        canvas.restore();
        */
    }

    private Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
     }

    
    public void setShadowEffect(){
        float bgScalePara = 1.1f;
        Rect layoutRect;
        Bitmap scaleBitmap;
        Bitmap shadowBitmap;
//        ViewGroup mView = this;
        ImageView scaleImage;
        TextView scaleText;
//        int screen_mode;
//        String text = null;

//        Launcher.trans_frameView.bringToFront();
//        Launcher.layoutScaleShadow.bringToFront();
//        Launcher.frameView.bringToFront();
        

        Rect imgRect = new Rect();
        this.getGlobalVisibleRect(imgRect);
//        setFramePosition(imgRect);
        
//        screen_mode = getScreenMode(mView);
//        scaleImage = (ImageView)Launcher.layoutScaleShadow.findViewById(R.id.img_focus_unit);
//        scaleText = (TextView)Launcher.layoutScaleShadow.findViewById(R.id.tx_focus_unit);
            
        this.buildDrawingCache();
        Bitmap bmp = this.getDrawingCache();
        
        scaleBitmap = zoomBitmap(bmp, (int)(imgRect.width()*bgScalePara),(int)(imgRect.height()*bgScalePara));
        this.destroyDrawingCache();  
        
        shadowBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shadow);
        int layout_width = (shadowBitmap.getWidth() - imgRect.width()) / 2;
        int layout_height = (shadowBitmap.getHeight() - imgRect.height()) / 2;
        layoutRect = new Rect(imgRect.left-layout_width, imgRect.top-layout_height, imgRect.right+layout_width, imgRect.bottom+layout_height);
        
//        scaleImage.setImageBitmap(scaleBitmap);

//        Launcher.layoutScaleShadow.setBackgroundResource(getShadow(mView.getChildAt(0), screen_mode));
//        setViewPosition(Launcher.layoutScaleShadow, layoutRect);  
    }
    
}
