package com.wcc.www.customview.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/19.
 */

public class C28_HorizontalProgressBarWithNumber extends ProgressBar {

    protected int dp2px(int dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }
    public C28_HorizontalProgressBarWithNumber(Context context) {
        this(context, null, 0);
    }

    public C28_HorizontalProgressBarWithNumber(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private static final int DEFAULT_TEXT_SIZE = 10,
    DEFAULT_TEXT_COLOR = 0XFFFC00D1,
    DEFAULT_REACH_COLOR = 0XFFD3D6DA,
    DEFAULT_UNREACH_COLOR = 0XFFD3D6DA,
    DEFAULT_REACH_HEIGHT = 2,
    DEFAULT_UNREACH_HEIGHT = 2,
    DEFAULT_TEXT_OFFSET = 10,
    DEFAULT_TEXT_VISIBILITY = 0;

    protected Paint mPaint = new Paint();
    protected int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor = DEFAULT_TEXT_COLOR;
    protected int mReachColor = DEFAULT_REACH_COLOR;
    protected int mUnreachColor = DEFAULT_UNREACH_COLOR;
    protected int mReachHeight = dp2px(DEFAULT_REACH_HEIGHT);
    protected int mUnreachHeight = dp2px(DEFAULT_UNREACH_HEIGHT);
    protected int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);
    protected int mTextVisibility = DEFAULT_TEXT_VISIBILITY;
    protected int mRealWidth;
    protected static final int VISIBLE = 0, INVISIBLE = 1;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRealWidth = w - getPaddingLeft() - getPaddingRight();
    }

    public C28_HorizontalProgressBarWithNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setHorizontalScrollBarEnabled(true);
        obtainStyledAttributes(attrs);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
//        setMax(100);
    }

    private void obtainStyledAttributes(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBarWithNumber);
        mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressBarWithNumber_text_size, mTextSize);
        mTextColor = ta.getColor(R.styleable.HorizontalProgressBarWithNumber_text_color, mTextColor);
        mReachColor = ta.getColor(R.styleable.HorizontalProgressBarWithNumber_reach_color, mReachColor);
        mUnreachColor = ta.getColor(R.styleable.HorizontalProgressBarWithNumber_unreach_color, mUnreachColor);
        mReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBarWithNumber_reach_segment_height, mReachHeight);
        mUnreachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressBarWithNumber_unreach_segment_height, mUnreachHeight);
        mTextOffset = (int) ta.getDimension(R.styleable.HorizontalProgressBarWithNumber_text_offset, mTextOffset);
        mTextVisibility = ta.getInt(R.styleable.HorizontalProgressBarWithNumber_text_visibility, mTextVisibility);
        ta.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY)
        {
            int textHeight = (int) (mPaint.descent() + mPaint.ascent());
            int exceptHeight = getPaddingTop() + getPaddingBottom() +
                    Math.max(Math.max(mReachHeight, mUnreachHeight), Math.abs(textHeight));
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int i = 0;
    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        setProgress(i);
//        canvas.drawColor(Color.WHITE);
//        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        float progressRadio = getProgress() * 1.0f / getMax();
        float progressPos = mRealWidth * progressRadio;
        String text = getProgress() + "%";
        float textWidth, textHeight;
        if(mTextVisibility == VISIBLE) {
            textWidth = mPaint.measureText(text);
            textHeight = (mPaint.ascent() + mPaint.descent()) / 2;
        }else
        {
            textWidth = 0;
            textHeight = 0;
            mTextOffset = 0;
        }
        if(progressPos + textWidth > mRealWidth)
        {
            progressPos = mRealWidth - textWidth;
//            mTextVisibility = INVISIBLE;
        }

        float endX = progressPos - mTextOffset / 2;
        if(endX > 0)
        {
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        if(mTextVisibility == VISIBLE) {
            mPaint.setColor(mTextColor);
            canvas.drawText(text, progressPos, -textHeight, mPaint);

            /*if(getProgress() == getMax())
            {
                System.out.println("dd");
                mPaint.setColor(Color.WHITE);
                startX = mRealWidth - mTextOffset / 2;
                System.out.println(startX);
                System.out.println(getWidth());
//                canvas.clipRect(startX, -getHeight(), getWidth(), getHeight());
//                canvas.drawColor(Color.WHITE);
                canvas.drawLine(startX, 0, getWidth(), 0, mPaint);
            }*/
        }

        mPaint.setColor(mUnreachColor);
        mPaint.setStrokeWidth(mUnreachHeight);
        float startX = progressPos + mTextOffset / 2 + textWidth;
        if(startX < mRealWidth)
            canvas.drawLine(startX, 0, mRealWidth, 0, mPaint);

//        canvas.restore();
//        i ++;
//        if(i > getMax())
//            i = 0;
//        invalidate();
    }
}
