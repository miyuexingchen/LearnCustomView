package com.wcc.www.customview.customstore;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by 王晨晨 on 2017/9/12.
 */

public class C10_ZoomImageViewExeciseCustomView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private Matrix mScaleMatrix;

    public C10_ZoomImageViewExeciseCustomView(Context context) {
        this(context, null);
    }

    private boolean isAutoScale;
    private ScaleGestureDetector mScaleGestureDetetor;
    private GestureDetector mGestureDetector;
    public C10_ZoomImageViewExeciseCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetetor = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(isAutoScale)
                    return true;
                isAutoScale = true;
                float scale = getScale();
                if(scale < mMidScale)
                {
                    postDelayed(new AutoScaleRunnable(e.getX(), e.getY(), mMidScale), 16);
                    System.out.println("mid");
                    /*mScaleMatrix.postScale(mMidScale / scale, mMidScale / scale, e.getX(), e.getY());
                    checkBorderAndCenterWhenScale();
                    setImageMatrix(mScaleMatrix);*/
                }else if(scale < mMaxScale){
                    System.out.println("max");
                    System.out.println("scale = "+scale+", mMaxScale = "+mMaxScale);
                    postDelayed(new AutoScaleRunnable(e.getX(), e.getY(), mMaxScale), 16);
                }else{
                    System.out.println("init");
                    postDelayed(new AutoScaleRunnable(e.getX(), e.getY(), mInitScale), 16);
                   /* mScaleMatrix.postScale(mInitScale / scale, mInitScale / scale, e.getX(), e.getY());
                    checkBorderAndCenterWhenScale();
                    setImageMatrix(mScaleMatrix);*/
                }
                return true;
            }
        });
    }

    private class AutoScaleRunnable implements Runnable{

        private float x, y, mTargetScale, mTmpScale;

        private final float BIGGER = 1.07F, SMALLER = 0.93F;
        public AutoScaleRunnable(float x, float y, float mTargetScale) {
            this.x = x;
            this.y = y;
            this.mTargetScale = mTargetScale;
            float scale = getScale();
            if(scale < mTargetScale)
                mTmpScale = BIGGER;
            if(scale > mTargetScale)
                mTmpScale = SMALLER;
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(mTmpScale, mTmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            float currentScale = getScale();
            if((currentScale < mTargetScale && mTmpScale > 1.0f) || (currentScale > mTargetScale && mTmpScale < 1.0f))
            {
                postDelayed(this, 16);
            }else
            {
                mScaleMatrix.postScale(mTargetScale / currentScale, mTargetScale / currentScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private float mInitScale, mMidScale, mMaxScale;
    @Override
    public void onGlobalLayout() {
        int width = getWidth();
        int height = getHeight();
        Drawable d = getDrawable();
        if(d == null)
            return;
        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();
        float scale = 1.0f;
        if(width < dw && height > dh)
            scale = width * 1.0f / dw;
        else if(width > dw && height < dh)
            scale = height * 1.0f / dh;
        else if((width < dw && height < dh) || (width > dw && height > dh))
            scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);

        mInitScale = scale;
        mMidScale = scale * 2;
        mMaxScale = scale * 4;

        mScaleMatrix = new Matrix();
        float dx = (width - dw) / 2;
        float dy = (height - dh) / 2;
        mScaleMatrix.postScale(scale, scale, width / 2, height / 2);
        mScaleMatrix.postTranslate(dx * scale, dy * scale);
        setImageMatrix(mScaleMatrix);
    }

    private float getScale()
    {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    private void checkBorderAndCenterWhenScale()
    {
        if(getDrawable() == null)
            return;
        RectF rectF = getMatrixRectF();
        float deltaX = 0, deltaY = 0;

        if(rectF.width() >= getWidth())
        {
            if(rectF.left > 0)
                deltaX = -rectF.left;
            if(rectF.right < getWidth())
                deltaX = getWidth() - rectF.right;
        }

        if(rectF.height() >= getHeight())
        {
            if(rectF.top > 0)
                deltaY = -rectF.top;
            if(rectF.bottom < getHeight())
                deltaY = getHeight() - rectF.bottom;
        }

        if(rectF.width() < getWidth())
            deltaX = getWidth() / 2 - rectF.right + rectF.width() / 2;
        if(rectF.height() < getHeight())
            deltaY = getHeight() / 2 - rectF.bottom + rectF.height() / 2;

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    private RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable d = getDrawable();
        if(d != null)
        {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            mScaleMatrix.mapRect(rectF);
        }
        return rectF;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f))
        {
            if(scale * scaleFactor < mInitScale)
                scaleFactor = mInitScale / scale;
            if(scale * scaleFactor > mMaxScale)
                scaleFactor = mMaxScale / scale;

            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    private void checkBorderWhenTranslate()
    {
        if(getDrawable() == null)
            return;

        RectF rectF = getMatrixRectF();
        float deltaX = 0, deltaY = 0;
        if(rectF.width() >= getWidth())
        {
            if(rectF.left > 0)
                deltaX = -rectF.left;
            if(rectF.right < getWidth())
                deltaX = getWidth() - rectF.right;
        }

        if(rectF.height() >= getHeight())
        {
            if(rectF.top > 0)
                deltaY = -rectF.top;
            if(rectF.bottom < getHeight())
                deltaY = getHeight() - rectF.bottom;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    private float mTouchSlop;
    private float mLastX, mLastY;
    private int mPointerCount;
    private boolean isCanDrag;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetetor.onTouchEvent(event);
        int pointerCount = event.getPointerCount();
        float x = 0, y = 0;
        for(int i = 0; i < pointerCount; i ++)
        {
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;
        if(pointerCount != mPointerCount)
        {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mPointerCount = pointerCount;

        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if(!isCanDrag)
                    isCanDrag = Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
                if(isCanDrag)
                {
                    RectF rect = getMatrixRectF();
                    if(getDrawable() != null)
                    {
                        if(rect.width() < getWidth())
                            dx = 0;
                        if(rect.height() < getHeight())
                            dy = 0;
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPointerCount = 0;
                break;
        }
        return true;
    }
}
