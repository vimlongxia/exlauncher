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
        Drawable dbe = getResources().getDrawable(R.drawable.ic_button_movie).mutate();//���������mutate��������ԭͼҲ�ᱻ�ı䣬��Ϊ���õ���Դ��ͬһ�������ж����ǹ���״̬�ġ�
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_button_movie);
        PicWidth = drawable.getIntrinsicWidth();
        PicHegiht = drawable.getIntrinsicHeight();
        drawable.setBounds(posX,posY,posX+PicWidth,posY+PicHegiht);
        dbe.setBounds(0, 0, PicWidth, PicHegiht);
        canvas.drawColor(Color.WHITE);//���û�����ɫ
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        dbe.setColorFilter(0x7f000000,PorterDuff.Mode.SRC_IN);
        canvas.translate(posX + (int)(0.9 * PicWidth/2), posY + PicHegiht/2);//ͼ��ƽ��Ϊ�˸պ���ԭͼ���γ�Ӱ��Ч����
        canvas.skew(-0.9F, 0.0F);//ͼ����бЧ����
        canvas.scale(1.0f, 0.5f);//ͼ����ʵ�ǻ��������ţ�Y������СΪ1/2��
        dbe.draw(canvas);//�˴�Ϊ��ԭͼ��Ӱ��Ч��ͼ����ԭͼ�Ȼ���������²㡣
        drawable.clearColorFilter();
        canvas.restore();
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        drawable.draw(canvas);//�˴�Ϊ��ԭͼ������canvas�в��Ч������˻����Ӱ��֮�ϡ�
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
