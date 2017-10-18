package com.wcc.www.customview.customstore;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/19.
 */

public class C29_RoundProgressBarWithNumberWithNumber extends C28_HorizontalProgressBarWithNumber {
    public C29_RoundProgressBarWithNumberWithNumber(Context context) {
        this(context, null, 0);
    }

    public C29_RoundProgressBarWithNumberWithNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private int mRadius = dp2px(30);

    public C29_RoundProgressBarWithNumberWithNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mReachHeight = (int) (mUnreachHeight * 2.5f);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBarWithNumber);
        mRadius = (int) ta.getDimension(R.styleable.RoundProgressBarWithNumber_radius, mRadius);
        ta.recycle();

        mTextSize = sp2px(14);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

   /* @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int paintHeight = Math.max(mReachHeight, mUnreachHeight);
        if(heightMode != MeasureSpec.EXACTLY)
        {
            int exceptHeight = getPaddingTop() + getPaddingBottom() + mRadius * 2 + paintHeight * 2;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight, MeasureSpec.EXACTLY);
        }

        if(widthMode != MeasureSpec.EXACTLY)
        {
            int exceptWidth = getPaddingLeft() + getPaddingRight() + mRadius * 2 + paintHeight * 2;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }*/

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);
        int textHeight = (int) ((mPaint.ascent() + mPaint.descent()) / 2);

        mPaint.setStyle(Paint.Style.STROKE);
//        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.translate(getWidth() / 2, getHeight() / 2);
        mPaint.setStrokeWidth(mUnreachHeight);
        mPaint.setColor(mUnreachColor);
//        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        canvas.drawCircle(0, 0, mRadius, mPaint);

        mPaint.setStrokeWidth(mReachHeight);
        mPaint.setColor(mReachColor);
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
//        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), 0, sweepAngle, false, mPaint);
        canvas.drawArc(new RectF(-mRadius, -mRadius, mRadius, mRadius), 0, sweepAngle, false, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawText(text, mRadius - textWidth / 2, mRadius - textHeight, mPaint);
        canvas.drawText(text, -textWidth / 2, -textHeight, mPaint);
    }
}
