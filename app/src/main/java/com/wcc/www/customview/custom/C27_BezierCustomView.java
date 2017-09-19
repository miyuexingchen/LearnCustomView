package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 王晨晨 on 2017/9/18.
 */

public class C27_BezierCustomView extends View {


    public C27_BezierCustomView(Context context) {
        this(context, null);
    }

    private Paint mPaint;
    private Path mPath;
    public C27_BezierCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(0xffa2d6ae);

        mPath = new Path();
    }

    private int width, height;
    private float waveX, waveY;
    private float ctrX, ctrY;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        waveY = 1 / 8f * h;
        ctrY = -1 / 16f * h;
    }

    private boolean isInc = true;
    private boolean isDown = true;
    @Override
    protected void onDraw(Canvas canvas) {
        mPath.moveTo(-1 / 4f * width, waveY);

        mPath.quadTo(ctrX, ctrY, 5 / 4f * width, waveY);
        mPath.lineTo(5 / 4f * width, height);
        mPath.lineTo(-1 / 4f * width, height);
        mPath.close();

        canvas.drawPath(mPath, mPaint);

        if(ctrX >= 5 / 4f * width)
            isInc = false;
        else if(ctrX <= -1 / 4f * width)
            isInc = true;
        ctrX += isInc ? 20 : -20;
        if(ctrY <= -1 / 16f * height)
            isDown = true;
        else if(ctrY >= height)
            isDown = false;

        if(ctrY < height && isDown)
        {
            ctrY += 2;
            waveY += 2;
        }else if(ctrY > -1 / 16f * height && !isDown){
            ctrY -= 2;
            waveY -= 2;
        }

        mPath.reset();
        invalidate();
    }
}
