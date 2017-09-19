package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/11.
 */

public class C6_PathMeasureCustomView extends View {

    public C6_PathMeasureCustomView(Context context) {
        this(context, null);
    }

    public C6_PathMeasureCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        init(context);
    }

    private Paint mPaint = new Paint();
    private void initPaint(){
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
    }

    private int mWidth, mHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private float currentValue = 0;
    private float[] pos;
    private float[] tan;
    private Bitmap mBitmap;
    private Matrix mMatrix;

    private void init(Context context)
    {
        pos = new float[2];
        tan = new float[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow, options);
        mMatrix = new Matrix();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mWidth / 2, mHeight / 2);
        Path path = new Path();
        path.addCircle(0, 0, 200, Path.Direction.CW);
        PathMeasure pm = new PathMeasure(path, false);
        currentValue += 0.05;
        if(currentValue >= 1)
            currentValue = 0;
        pm.getPosTan(pm.getLength() * currentValue, pos, tan);
        mMatrix.reset();
        float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI);
        mMatrix.postRotate(degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        mMatrix.postTranslate(pos[0] - mBitmap.getWidth() / 2, pos[1] - mBitmap.getHeight() / 2);
        canvas.drawPath(path, mPaint);
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        postInvalidateDelayed(50);
    }
}
