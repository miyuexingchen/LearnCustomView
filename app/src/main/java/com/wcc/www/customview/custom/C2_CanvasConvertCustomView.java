package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 王晨晨 on 2017/9/8.
 */
public class C2_CanvasConvertCustomView extends View {
    public C2_CanvasConvertCustomView(Context context) {
        this(context, null);
    }

    public C2_CanvasConvertCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private Paint mPaint;
    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private int centerX, centerY;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(centerX, centerY);
        Rect r = new Rect(-400, -400, 400, 400);

        for(int i = 0; i < 20; i ++)
        {
            canvas.scale(0.9f, 0.9f);
            canvas.drawRect(r, mPaint);
        }
    }
}

