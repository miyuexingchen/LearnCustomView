package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/8.
 */

public class C3_CanvasPictureTextCustomView extends View {
    public C3_CanvasPictureTextCustomView(Context context) {
        this(context, null);
    }

    public C3_CanvasPictureTextCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.raw.b2);
    }

    private static final int ANIM_NULL = 0, ANIM_CHECK = 1, ANIM_UNCHECK = 2;
    private int animCurrentPage = -1, animMaxPage = 12;
    private boolean isCheck = false;
    private int animDuration = 1000;
    private int animState = ANIM_NULL;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(animCurrentPage < animMaxPage && animCurrentPage >= 0)
            {
                invalidate();
                if(animState == ANIM_NULL)
                    return;
                else if(animState == ANIM_CHECK)
                    animCurrentPage ++;
                else if(animState == ANIM_UNCHECK)
                    animCurrentPage --;
                sendEmptyMessageDelayed(0, animDuration / animMaxPage);
            }else
            {
                if(isCheck)
                    animCurrentPage = animMaxPage;
                else
                    animCurrentPage = -1;
                invalidate();
                animState = ANIM_NULL;
            }
        }
    };

    private Paint mPaint;
    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private Bitmap bitmap;
    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(centerX, centerY);
        mPaint.setColor(Color.YELLOW);
        canvas.drawCircle(0, 0, 240, mPaint);
        int sideLength = bitmap.getHeight();
        Rect src = new Rect(sideLength * animCurrentPage, 0, sideLength * (animCurrentPage + 1), sideLength);
        Rect dst = new Rect(-200, -200, 200, 200);
        canvas.drawBitmap(bitmap, src, dst, mPaint);
    }

    public void check()
    {
        if(animState != ANIM_NULL || isCheck)
            return;
        animCurrentPage = 0;
        animState = ANIM_CHECK;
        handler.sendEmptyMessageDelayed(0, animDuration / animMaxPage);
        isCheck = true;
    }

    public void uncheck()
    {
        if(animState != ANIM_NULL || (!isCheck))
            return;
        animCurrentPage = animMaxPage - 1;
        animState = ANIM_UNCHECK;
        handler.sendEmptyMessageDelayed(0, animDuration / animMaxPage);
        isCheck = false;
    }

    private int centerX, centerY;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }
}
