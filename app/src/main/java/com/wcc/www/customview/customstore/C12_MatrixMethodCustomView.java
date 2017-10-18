package com.wcc.www.customview.customstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/12.
 */

public class C12_MatrixMethodCustomView extends View {

    public C12_MatrixMethodCustomView(Context context) {
        this(context, null);
    }

    private static final int NUM_OF_POINT = 8;
    private int mTotalFoldWidth;
    private float mFoldPercentage = 0.8f;
    private int mNumOfFold = 8;
    private Matrix[] matrices = new Matrix[mNumOfFold];

    private Bitmap mBitmap;
    private Paint mTransparentPaint;
    private int mPerInitWidth;
    private int mPerFoldWidth;

    private Paint mShadowPaint;
    private Matrix mShadowGradientMatrix;
    private LinearGradient mShadowGradientShader;
    public C12_MatrixMethodCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tanyan);

        mTotalFoldWidth = (int) (mBitmap.getWidth() * mFoldPercentage);
        mPerFoldWidth = mTotalFoldWidth / mNumOfFold;
        mPerInitWidth = mBitmap.getWidth() / mNumOfFold;
        for(int i = 0; i < mNumOfFold; i ++)
            matrices[i] = new Matrix();

        mTransparentPaint = new Paint();
        int alpha = (int) (255 * mFoldPercentage * 0.8f);
        mTransparentPaint.setColor(Color.argb((int) (alpha * 0.8f), 0, 0, 0));


        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowGradientShader = new LinearGradient(0, 0, 0.5f, 0, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mShadowPaint.setShader(mShadowGradientShader);

        mShadowGradientMatrix = new Matrix();
        mShadowGradientMatrix.setScale(mPerFoldWidth, 1);
        mShadowGradientShader.setLocalMatrix(mShadowGradientMatrix);
        mShadowPaint.setAlpha(alpha);

        int depth = (int) (Math.sqrt(mPerInitWidth * mPerInitWidth - mPerFoldWidth * mPerFoldWidth) / 2);


        float[] src = new float[NUM_OF_POINT];

        float[] dst = new float[NUM_OF_POINT];

        for(int i = 0; i < mNumOfFold; i ++)
        {
            src[0] = i * mPerInitWidth;
            src[1] = 0;
            src[2] = src[0] + mPerInitWidth;
            src[3] = 0;
            src[4] = src[2];
            src[5] = mBitmap.getHeight();
            src[6] = src[0];
            src[7] = src[5];

            boolean isEven = i % 2 == 0;

            dst[0] = i * mPerFoldWidth;
            dst[1] = isEven ? 0 : depth;
            dst[2] = dst[0] + mPerFoldWidth;
            dst[3] = isEven ? depth : 0;
            dst[4] = dst[2];
            dst[5] = isEven ? mBitmap.getHeight() - depth : mBitmap.getHeight();
            dst[6] = dst[0];
            dst[7] = isEven ? mBitmap.getHeight() : mBitmap.getHeight() - depth;

            matrices[i].setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < mNumOfFold; i ++)
        {
            canvas.save();
            canvas.concat(matrices[i]);
            canvas.clipRect(i * mPerInitWidth, 0, mPerInitWidth * i + mPerInitWidth, mBitmap.getHeight());
            canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.translate(mPerInitWidth * i, 0);
            if(i % 2 == 0)
                canvas.drawRect(0, 0, mPerInitWidth, mBitmap.getHeight(), mTransparentPaint);
            else
                canvas.drawRect(0, 0, mPerInitWidth, mBitmap.getHeight(), mShadowPaint);
            canvas.restore();
        }
    }
}
