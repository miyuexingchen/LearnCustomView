package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 王晨晨 on 2017/9/11.
 */

public class C5_PathOtherCustomView extends View {
    public C5_PathOtherCustomView(Context context) {
        this(context, null);
    }

    public C5_PathOtherCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private Paint mPaint;
    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.BLACK);
    }

    private PointF c = new PointF(0, 0);

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        c.x = w / 2;
        c.y = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(c.x, c.y);
        Path p0 = new Path();
        Path p1 = new Path();
        Path p2 = new Path();
        Path p3 = new Path();
        Path p4 = new Path();
        Path p5 = new Path();
        Path p6 = new Path();
        Path p7 = new Path();
        Path p8 = new Path();
        Path p9 = new Path();
        
        p0.addCircle(-40, 0, 100, Path.Direction.CW);
        p1.addCircle(40, 0, 100, Path.Direction.CW);
        p2.addCircle(-40, -300, 100, Path.Direction.CW);
        p3.addCircle(40, -300, 100, Path.Direction.CW);
        p4.addCircle(-40, 300, 100, Path.Direction.CW);
        p5.addCircle(40, 300, 100, Path.Direction.CW);
        p6.addCircle(-40, -600, 100, Path.Direction.CW);
        p7.addCircle(40, -600, 100, Path.Direction.CW);
        p8.addCircle(-40, 600, 100, Path.Direction.CW);
        p9.addCircle(40, 600, 100, Path.Direction.CW);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(p0, mPaint);
        canvas.drawPath(p2, mPaint);
        canvas.drawPath(p4, mPaint);
        canvas.drawPath(p6, mPaint);
        canvas.drawPath(p8, mPaint);
        canvas.drawPath(p1, mPaint);
        canvas.drawPath(p3, mPaint);
        canvas.drawPath(p5, mPaint);
        canvas.drawPath(p7, mPaint);
        canvas.drawPath(p9, mPaint);
        p0.op(p1, Path.Op.DIFFERENCE);
        p2.op(p3, Path.Op.REVERSE_DIFFERENCE);
        p4.op(p5, Path.Op.INTERSECT);
        p6.op(p7, Path.Op.UNION);
        p8.op(p9, Path.Op.XOR);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(p0, mPaint);
        canvas.drawPath(p2, mPaint);
        canvas.drawPath(p4, mPaint);
        canvas.drawPath(p6, mPaint);
        canvas.drawPath(p8, mPaint);

    }
}
