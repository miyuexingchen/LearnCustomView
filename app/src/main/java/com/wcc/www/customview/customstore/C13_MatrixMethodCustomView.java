package com.wcc.www.customview.customstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 王晨晨 on 2017/9/13.
 */

public class C13_MatrixMethodCustomView extends ViewGroup {
    public C13_MatrixMethodCustomView(Context context) {
        this(context, null);
    }

    private static final int NUM_OF_POINT = 8, mNumOfFolds = 8;
    private float mTotalFoldWidth, mPerFoldWidth, mPerInitWidth;
    private Matrix[] matrices = new Matrix[mNumOfFolds];

    private Paint mTransparentPaint, mShadowPaint;
    private LinearGradient mShadowGradientShader;
    private Matrix mShadowGradientMatrix;

    public C13_MatrixMethodCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        for(int i = 0; i < mNumOfFolds; i ++)
        {
            matrices[i] = new Matrix();
        }

        mTransparentPaint = new Paint();
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowGradientShader = new LinearGradient(0, 0, 0.5f, 0, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mShadowPaint.setShader(mShadowGradientShader);
        mShadowGradientMatrix = new Matrix();
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View view = getChildAt(0);
        measureChild(view, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private Canvas mCanvas = new Canvas();
    private Bitmap mBitmap;
    private boolean isReady;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        View view = getChildAt(0);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        updateFold();
    }

    private float mFoldPercentage = 0.6f;
    private float anchor = 0;
    private void updateFold() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mTotalFoldWidth = width * mFoldPercentage;
        mPerFoldWidth = mTotalFoldWidth / mNumOfFolds;
        mPerInitWidth = width / mNumOfFolds;

        int alpha = (int) (255 * (1 - mFoldPercentage));
        mTransparentPaint.setColor(Color.argb((int) (alpha * 0.8f), 0, 0, 0));

        mShadowGradientMatrix.setScale(mPerInitWidth, 1);
        mShadowGradientShader.setLocalMatrix(mShadowGradientMatrix);
        mShadowPaint.setAlpha(alpha);

        int depth = (int) (Math.sqrt(mPerInitWidth * mPerInitWidth - mPerFoldWidth * mPerFoldWidth) / 2);
        float[] src = new float[NUM_OF_POINT];
        float[] dst = new float[NUM_OF_POINT];

        float anchorWidth = anchor * width;
        float midFold = anchorWidth / mPerInitWidth;
        System.out.println(anchorWidth+" ,"+midFold);
        for (int i = 0; i < mNumOfFolds; i++)
        {
            matrices[i].reset();
            src[0] = i * mPerInitWidth;
            src[1] = 0;
            src[2] = src[0] + mPerInitWidth;
            src[3] = 0;
            src[4] = src[2];
            src[5] = height;
            src[6] = src[0];
            src[7] = src[5];

            boolean isEvent = i % 2 == 0;

            dst[0] = i * mPerFoldWidth;
            dst[0] = (anchorWidth > i * mPerInitWidth) ? anchorWidth + (i - midFold) * mPerFoldWidth : anchorWidth - (midFold - i) * mPerFoldWidth;
            dst[1] = isEvent ? 0 : depth;
            dst[2] = dst[0] + mPerFoldWidth;
            dst[2] = (anchorWidth > (i + 1) * mPerInitWidth) ? anchorWidth + (i + 1 - midFold) * mPerFoldWidth : anchorWidth - (midFold - i - 1) * mPerFoldWidth;
            dst[3] = isEvent ? depth : 0;
            dst[4] = dst[2];
            dst[5] = isEvent ? height - depth : height;
            dst[6] = dst[0];
            dst[7] = isEvent ? height : height - depth;

            for(int j = 0; j < mNumOfFolds; j ++)
            {
                dst[j] = Math.round(dst[j]);
            }

            matrices[i].setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(mFoldPercentage == 0)
            return;
        if(mFoldPercentage == 1)
        {
            super.dispatchDraw(canvas);
            return;
        }

        for(int i = 0; i < mNumOfFolds; i ++)
        {
            canvas.save();
            canvas.concat(matrices[i]);
            canvas.clipRect(i * mPerInitWidth, 0, i * mPerInitWidth + mPerInitWidth, getHeight());
            if(isReady)
                canvas.drawBitmap(mBitmap, 0, 0, null);
            else
            {
                super.dispatchDraw(mCanvas);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                isReady = true;
            }
            canvas.translate(i * mPerInitWidth, 0);
            if(i % 2 == 0)
                canvas.drawRect(0, 0, mPerInitWidth, getHeight(), mTransparentPaint);
            else
                canvas.drawRect(0, 0, mPerInitWidth, getHeight(), mShadowPaint);
            canvas.restore();
        }
    }

    public void setFoldPercentage(float per)
    {
        mFoldPercentage = per;
        updateFold();
        invalidate();
    }

    public void setAnchor(float anchor)
    {
        this.anchor = anchor;
        updateFold();
        invalidate();
    }

    public float getFoldPercentage()
    {
        return mFoldPercentage;
    }
}
