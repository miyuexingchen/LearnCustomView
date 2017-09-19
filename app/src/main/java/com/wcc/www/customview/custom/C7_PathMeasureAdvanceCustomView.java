package com.wcc.www.customview.custom;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ViewAnimator;

/**
 * Created by 王晨晨 on 2017/9/11.
 */

public class C7_PathMeasureAdvanceCustomView extends View {
    public C7_PathMeasureAdvanceCustomView(Context context) {
        this(context, null);
    }

    public C7_PathMeasureAdvanceCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAll();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSearch(canvas);
    }

    private void drawSearch(Canvas canvas)
    {
        canvas.translate(mWidth / 2, mHeight / 2);
        canvas.drawColor(Color.parseColor("#0082d7"));
        switch (mCurrentState)
        {
            case NONE:
                canvas.drawPath(path_srarch, mPaint);
                break;

            case STARTING:
                mMeasure.setPath(path_srarch, false);
                Path dst = new Path();
                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst, true);
                canvas.drawPath(dst, mPaint);
                break;

            case SEARCHING:
                mMeasure.setPath(path_circle, false);
                Path dst2 = new Path();
                float stop = mMeasure.getLength() * mAnimatorValue;
                float start = (float) (stop - (0.5 - Math.abs(mAnimatorValue - 0.5f)) * 200);
                mMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, mPaint);
                break;

            case ENDING:
                mMeasure.setPath(path_srarch, false);
                Path dst3 = new Path();
                mMeasure.getSegment(mMeasure.getLength() * (1 - mAnimatorValue), mMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, mPaint);
                break;
        }
    }

    private int mWidth, mHeight;
    private Paint mPaint;

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(15);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private Path path_srarch, path_circle;
    private PathMeasure mMeasure;

    private void initPath() {
        path_srarch = new Path();
        path_circle = new Path();
        mMeasure = new PathMeasure();
        RectF circle1 = new RectF(-50, -50, 50, 50);
        path_srarch.addArc(circle1, 45, 359.9f);
        RectF oval = new RectF(-100, -100, 100, 100);
        path_circle.addArc(oval, 45, 359.9f);
        float[] pos = new float[2];
        mMeasure.setPath(path_circle, false);
        mMeasure.getPosTan(0, pos, null);
        path_srarch.lineTo(pos[0], pos[1]);
    }

    private ValueAnimator.AnimatorUpdateListener mUpdateListerner;
    private Animator.AnimatorListener mAnimatorListener;
    private float mAnimatorValue = 0;
    private Handler mAnimatorHandler;
    private void initListener()
    {
        mUpdateListerner = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    public static enum State{
        NONE, STARTING, SEARCHING, ENDING,
    }
    private State mCurrentState = State.NONE;
    private boolean isOver = false;
    private ValueAnimator mStartingAnimator, mSearchingAnimator, mEndingAnimator;
    private int count = 0;
    private void initHandler(){
        mAnimatorHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                switch (mCurrentState)
                {
                    case STARTING:
                        isOver = false;
                        mCurrentState = State.SEARCHING;
                        mSearchingAnimator.start();
                        break;

                    case SEARCHING:
                        if(!isOver)
                        {
                            mSearchingAnimator.start();
                            count ++;
                            if(count > 2)
                                isOver = true;
                        }else
                        {
                            mCurrentState = State.ENDING;
                            mEndingAnimator.start();
                        }
                        break;

                    case ENDING:
                        mCurrentState = State.NONE;
                        mCurrentState = State.STARTING;
                        count = 0;
                        mStartingAnimator.start();
                        break;
                }
            }
        };
    }

    private int mDefaultDuration = 2000;
    private void initAnimator()
    {
        mStartingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDefaultDuration);
        mSearchingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDefaultDuration);
        mEndingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDefaultDuration);

        mStartingAnimator.addUpdateListener(mUpdateListerner);
        mSearchingAnimator.addUpdateListener(mUpdateListerner);
        mEndingAnimator.addUpdateListener(mUpdateListerner);

        mStartingAnimator.addListener(mAnimatorListener);
        mSearchingAnimator.addListener(mAnimatorListener);
        mEndingAnimator.addListener(mAnimatorListener);
    }

    private void initAll()
    {
        initPaint();
        initPath();
        initListener();
        initHandler();
        initAnimator();

        mCurrentState = State.STARTING;
        mStartingAnimator.start();
    }
}
