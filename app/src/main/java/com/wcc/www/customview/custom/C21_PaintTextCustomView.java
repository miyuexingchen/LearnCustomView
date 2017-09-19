package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/15.
 */

public class C21_PaintTextCustomView extends View {
    public C21_PaintTextCustomView(Context context) {
        this(context, null);
    }

    private Paint mPaint;
    public C21_PaintTextCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint()
    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(40f);
        mPaint.setColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.colorPrimary));
        String text = "This is a test of drawText method.";
        float textWidth = mPaint.measureText(text);
        float x = (getWidth() - textWidth) / 2;
//        float y = getHeight() / 2 + (Math.abs(mPaint.ascent()) - mPaint.descent()) / 2;
        float y = getHeight() / 2 - (mPaint.descent() + mPaint.ascent()) / 2;
        canvas.drawText(text, x, y, mPaint);
    }
}
