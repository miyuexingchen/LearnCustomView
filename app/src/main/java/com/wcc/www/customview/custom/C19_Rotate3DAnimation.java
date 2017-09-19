package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by 王晨晨 on 2017/9/14.
 */

public class C19_Rotate3DAnimation extends Animation {

    private final float mFromDegrees, mToDegrees, mCenterX, mCenterY, mDepthZ;
    private final boolean mReverse;
    private Camera mCamera;
    private float scale = 1;
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    public C19_Rotate3DAnimation(Context context, float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ, boolean reverse)
    {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
        scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degrees = mFromDegrees + ((mToDegrees - mFromDegrees)) * interpolatedTime;
        Matrix matrix = t.getMatrix();
        mCamera.save();
        if(mReverse)
            mCamera.translate(0, 0, mDepthZ * interpolatedTime);
        else
            mCamera.translate(0, 0, mDepthZ * (1 - interpolatedTime));
        mCamera.rotateX(degrees);
        mCamera.getMatrix(matrix);
        mCamera.restore();

        float[] values = new float[9];
        matrix.getValues(values);
        values[6] = values[6] / scale;
        values[7] = values[7] / scale;
        matrix.setValues(values);

        matrix.preTranslate(-mCenterX, -mCenterY);
        matrix.postTranslate(mCenterX, mCenterY);
    }
}
