package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wcc.www.customview.R;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by 王晨晨 on 2017/9/12.
 */

public class C11_MatrixMethodCustomView extends View {
    public C11_MatrixMethodCustomView(Context context) {
        this(context, null);
    }

    private int pointCount = 0;
    private int triggerRadius = 180;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    public C11_MatrixMethodCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBitmapAndMatrix();
    }

    private float[] src = new float[8], dst = new float[8];
    private Paint pointPaint;
    private void initBitmapAndMatrix()
    {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.p1);

        mMatrix = new Matrix();
        float[] temp = {
                0, 0,
                mBitmap.getWidth(), 0,
                mBitmap.getWidth(), mBitmap.getHeight(),
                0, mBitmap.getHeight()
        };
        src = dst = temp.clone();

        pointPaint = new Paint();
        pointPaint.setStrokeWidth(50);
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(0xffd19165);
        pointPaint.setStrokeCap(Paint.Cap.ROUND);

        mMatrix.setPolyToPoly(src, 0, dst, 0, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:

                float x = event.getX();
                float y = event.getY();
                for(int i = 0; i < pointCount * 2; i += 2)
                {
                    if((Math.abs(x - dst[i]) <= triggerRadius) && (Math.abs(y - dst[i + 1]) <= triggerRadius))
                    {
                        dst[i] = x - 100;
                        dst[i + 1] = y - 100;
                        break;
                    }
                }
                System.out.println("onTouchEvent------>1 dst "+Arrays.toString(dst));
                System.out.println("onTouchEvent------>1 matrix "+mMatrix.toShortString());
                resetMatrix(pointCount);
                System.out.println("onTouchEvent------>2 dst "+Arrays.toString(dst));
                System.out.println("onTouchEvent------>2 matrix "+mMatrix.toShortString());
                invalidate();
                break;
        }
        return true;
    }

    public void resetMatrix(int pointCount)
    {
        mMatrix.reset();
        mMatrix.setPolyToPoly(src, 0, dst, 0, pointCount);
    }

    public void setPointCount(int pointCount)
    {
        this.pointCount = pointCount > 4 || pointCount < 0 ? 4 : pointCount;
        dst = src.clone();
        resetMatrix(pointCount);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(100, 100);
        canvas.drawBitmap(mBitmap, mMatrix, null);
        float[] dst = new float[8];
        System.out.println("onDraw--------->1 src "+Arrays.toString(src));
        System.out.println("onDraw--------->1 matrix "+mMatrix.toShortString());
        System.out.println("onDraw--------->1 global dst"+ Arrays.toString(this.dst));
        System.out.println("onDraw--------->1 dst "+Arrays.toString(dst));
        mMatrix.mapPoints(dst, src);
        for(int i = 0; i < pointCount * 2; i += 2)
            canvas.drawPoint(dst[i], dst[i + 1], pointPaint);
        System.out.println("onDraw--------->2 src "+Arrays.toString(src));
        System.out.println("onDraw--------->2 matrix "+mMatrix.toShortString());
        System.out.println("onDraw--------->2 global "+Arrays.toString(this.dst));
        System.out.println("onDraw--------->2 dst "+Arrays.toString(dst));

    }
}
