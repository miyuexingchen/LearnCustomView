package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 王晨晨 on 2017/9/13.
 */

public class C14_MatrixMethodCustomView extends C13_MatrixMethodCustomView {

    private GestureDetector mScrollGestureDetetor;

    public C14_MatrixMethodCustomView(Context context) {
        this(context, null);
    }

    public C14_MatrixMethodCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mScrollGestureDetetor = new GestureDetector(context, new ScrollGestureDetetor());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScrollGestureDetetor.onTouchEvent(event);
    }

    private int mFold = -1;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(mFold == -1)
            mFold = (int) (getWidth() * getFoldPercentage());
        super.dispatchDraw(canvas);
    }

    class ScrollGestureDetetor extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            System.out.println(distanceX);
            mFold -= distanceX;
            if(mFold < 0)
                mFold = 0;
            if(mFold > getWidth())
                mFold = getWidth();

            float foldPercentage = Math.abs(((float) mFold) / ((float) getWidth()));
            setFoldPercentage(foldPercentage);
            return true;
        }
    }
}