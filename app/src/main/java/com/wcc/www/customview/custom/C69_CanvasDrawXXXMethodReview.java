package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 王晨晨 on 2017/10/18.
 */

public class C69_CanvasDrawXXXMethodReview extends View {

    private Paint mPaint;
    public C69_CanvasDrawXXXMethodReview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        float[] points = {20, 20, 120, 20, 70, 20, 70, 120, 20, 120, 120, 120, 150, 20, 250, 20, 150, 20, 150, 120, 250, 20, 250, 120, 150, 120, 250, 120};
//        float[] points = {120, 120, 150, 20, 150, 120, 250, 120};
//        canvas.drawLines(points, mPaint);

        int offset = 200;
        mPaint.setTextSize(80);
        mPaint.setFakeBoldText(true);
        mPaint.setColor(Color.BLACK);
        String text = "Hello text hahahaha";
        canvas.drawText(text, offset, offset * 2, mPaint);
        Rect rect = new Rect();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.getTextBounds(text, 0, text.length(), rect);
        System.out.println(rect.left+" "+rect.top+" "+rect.right+" "+rect.bottom);
        canvas.drawRect(rect, mPaint);
        canvas.drawLine(offset, offset, offset + (rect.right - rect.left), offset, mPaint);
        canvas.drawLine(offset, offset + 10, offset + mPaint.measureText(text), offset + 10, mPaint);
    }
}
