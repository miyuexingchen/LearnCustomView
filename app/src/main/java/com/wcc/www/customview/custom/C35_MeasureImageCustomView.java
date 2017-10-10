package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 王晨晨 on 2017/9/26.
 */

public class C35_MeasureImageCustomView extends View {
    public C35_MeasureImageCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private Bitmap bitmap;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int resultWidth = 0, resultHeight = 0;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthSpecMode == MeasureSpec.EXACTLY)
            resultWidth = widthSpecSize;
        else
        {
            resultWidth = bitmap.getWidth();
            if(widthSpecMode == MeasureSpec.AT_MOST)
                resultWidth = Math.min(resultWidth, widthSpecSize);
        }

        if(heightSpecMode == MeasureSpec.EXACTLY)
            resultHeight = heightSpecSize;
        else{
            resultHeight = bitmap.getHeight();
            if(heightSpecMode == MeasureSpec.AT_MOST)
                resultHeight = Math.min(resultHeight, heightSpecSize);
        }

        resultWidth += getPaddingLeft() + getPaddingRight();
        resultHeight += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, getPaddingLeft(), getPaddingTop(), null);
    }

    public void setBitmap(Bitmap bmp)
    {
        bitmap = bmp;
    }

}
