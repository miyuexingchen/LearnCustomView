package com.wcc.www.customview.customstore;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by 王晨晨 on 2017/9/11.
 */

public class C9_ZoomImageCustomView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    public C9_ZoomImageCustomView(Context context) {
        this(context, null);
    }

    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;
    public C9_ZoomImageCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScaleMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if(isAutoScale)
                    return true;
                float x = e.getX(), y = e.getY();
                isAutoScale = true;
                if(getScale() < mMidScale) {
//                    mScaleMatrix.postScale(mMidScale / getScale(), mMidScale / getScale(), x, y);
//                    checkBorderAndCenterWhenScale();
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                }else if(getScale() < mMaxScale)
                {
                    postDelayed(new AutoScaleRunnable(mMaxScale, x, y), 16);
                }else
                {
//                    mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
//                    checkBorderAndCenterWhenScale();
//                    setImageMatrix(mScaleMatrix);
                    postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                }
                return true;
            }
        });
    }

    private class AutoScaleRunnable implements Runnable{
        private float mTargetScale;
        private float x, y;
        private final float BIGGER = 1.07f, SMALLER = 0.93f;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if(getScale() < mTargetScale)
                mTmpScale = BIGGER;
            if(getScale() > mTargetScale)
                mTmpScale = SMALLER;
        }

        private float mTmpScale;
        @Override
        public void run() {
            mScaleMatrix.postScale(mTmpScale, mTmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            float currentScale = getScale();
            if((mTmpScale > 1.0f && currentScale < mTargetScale) || (mTmpScale < 1.0f && currentScale > mTargetScale))
            {
                postDelayed(this, 16);
            }else
            {
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
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

    private boolean mOnce;
    private float mInitScale, mMidScale, mMaxScale;
    private Matrix mScaleMatrix;
    @Override
    public void onGlobalLayout() {
        if(!mOnce)
        {
            int width = getWidth();
            int height = getHeight();
            Drawable d = getDrawable();
            if(d == null)
                return;
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            if(dw > width && dh < height)
                scale = width * 1.0f / dw;
            else if(dw < width && dh > height)
                scale = height * 1.0f / dh;
            else if((dw > width && dh > height) || (dw < width && dh < height))
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);

            mInitScale = scale;
            mMaxScale = mInitScale * 4;
            mMidScale = mInitScale * 2;

            int dx = width / 2 - dw / 2;
            int dy = height / 2 - dh / 2;

            mScaleMatrix.postTranslate(dx, dy);
            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
            setImageMatrix(mScaleMatrix);
            mOnce = true;
        }
    }

    public float getScale()
    {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if(getDrawable() == null)
            return true;
        if((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f))
        {
            if(scale * scaleFactor < mInitScale)
            {
                scaleFactor = mInitScale / scale;
            }else if(scale * scaleFactor > mMaxScale)
            {
                scaleFactor = mMaxScale / scale;
            }

            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    private RectF getMatrixRectF()
    {
        Matrix matrix = this.mScaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if(drawable != null)
        {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

    private void checkBorderAndCenterWhenScale()
    {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        int width = getWidth();
        int height = getHeight();
        if(rect.width() >= width)
        {
            if(rect.left > 0)
                deltaX = -rect.left;
            if(rect.right < width)
                deltaX = width - rect.right;
        }

        if(rect.height() >= height)
        {
            if(rect.top > 0)
                deltaY = -rect.top;
            if(rect.bottom < height)
                deltaY = height - rect.bottom;
        }

        if(rect.width() < width)
            deltaX = width / 2f - rect.right + rect.width() / 2f;

        if(rect.height() < height)
            deltaY = height / 2f - rect.bottom + rect.height() / 2f;

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    private boolean isCheckLeftAndRight, isCheckTopAndBottom;
    private void checkBorderWhenTranslate()
    {
        RectF rect = getMatrixRectF();
        float deltaX = 0, deltaY = 0;
        int width = getWidth();
        int height = getHeight();

        if(rect.top > 0 && isCheckTopAndBottom)
            deltaY = -rect.top;
        if(rect.bottom < height && isCheckTopAndBottom)
            deltaY = height - rect.bottom;
        if(rect.left > 0 && isCheckLeftAndRight)
            deltaX = -rect.left;
        if(rect.right < width && isCheckLeftAndRight)
            deltaX = width - rect.right;

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }


    private int mLastPointerCount;
    private float mLastX, mLastY;
    private int mTouchSlop;
    private boolean isCanDrag;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;
        int pointerCount = event.getPointerCount();
        for(int i = 0; i < pointerCount; i ++)
        {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= pointerCount;
        y /= pointerCount;


        if(mLastPointerCount != pointerCount)
        {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;

        RectF rectf = getMatrixRectF();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(rectf.width() > getWidth() + 0.01 || rectf.height() > getHeight() + 0.01)
                    if(getParent() instanceof ViewPager)
                    getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:

                if(rectf.width() > getWidth() + 0.01 || rectf.height() > getHeight() + 0.01)
                    if(getParent() instanceof ViewPager)
                    getParent().requestDisallowInterceptTouchEvent(true);
                float dx = x - mLastX;
                float dy = y - mLastY;
                if(!isCanDrag)
                    isCanDrag = isMoveAction(dx, dy);

                if(isCanDrag)
                {
                    RectF rect = getMatrixRectF();
                    if(getDrawable() != null)
                    {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        if(rect.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if(rect.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
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

                mLastPointerCount = 0;
                break;
        }

        return true;
    }

    private boolean isMoveAction(float dx, float dy) {


        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }
}
