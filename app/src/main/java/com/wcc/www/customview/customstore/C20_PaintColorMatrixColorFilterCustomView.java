package com.wcc.www.customview.customstore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/14.
 */

public class C20_PaintColorMatrixColorFilterCustomView extends View {

    public C20_PaintColorMatrixColorFilterCustomView(Context context) {
        this(context, null);
    }

    public C20_PaintColorMatrixColorFilterCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private Paint mPaint;

    private void initPaint()
    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setTextSize(60);
//        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.OVERLAY);
        ColorMatrix colorMatrix = new ColorMatrix(
                new float[]{
                        1, 0, 0, 0, 0,
                        0, 2, 0, 0, 0,
                        0, 0, 1, 0, 0,
                        0, 0, 0, 1, 0,
                }
        );
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        mPaint.setColorFilter(colorFilter);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        float w = getWidth() / 2;
        float h = getHeight() / 2;
        float radius = Math.min(w, h);
        canvas.drawCircle(w, h - 300, 100, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST)
            setMeasuredDimension(300, 300);
        else if(widthMode == MeasureSpec.AT_MOST)
            setMeasuredDimension(300, heightSize);
        else if(heightMode == MeasureSpec.AT_MOST)
            setMeasuredDimension(widthSize, 300);
    }
}
