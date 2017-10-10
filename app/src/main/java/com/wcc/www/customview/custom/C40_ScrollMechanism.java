package com.wcc.www.customview.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by 王晨晨 on 2017/9/27.
 */

public class C40_ScrollMechanism extends ViewGroup {


    private Scroller mScroller;
    private int mTouchSlop;
    public C40_ScrollMechanism(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int leftBorder, rightBorder;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed)
        {
            int childCount = getChildCount();
            for(int i = 0; i < childCount; i ++)
            {
                View child = getChildAt(i);
                child.layout(i * child.getMeasuredWidth(), 0, (i + 1) * child.getMeasuredWidth(), child.getMeasuredHeight());
            }

            leftBorder = getChildAt(0).getLeft();
            rightBorder = getChildAt(childCount - 1).getRight();
        }
    }

    private float mLastRawX, mMoveRawX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastRawX = ev.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                mMoveRawX = ev.getRawX();
                float dx = Math.abs(mMoveRawX - mLastRawX);
                mLastRawX = mMoveRawX;
                if(dx > mTouchSlop)
                    return true;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                mMoveRawX = event.getRawX();
                float scrolledX = mLastRawX - mMoveRawX;
                if(getScrollX() + scrolledX < leftBorder)
                {
                    scrollTo(leftBorder, 0);
                    return true;
                }else if(getScrollX() + getWidth() + scrolledX > rightBorder)
                {
                    scrollTo(rightBorder - getWidth(), 0);
                    return true;
                }
                scrollBy((int) scrolledX, 0);
                mLastRawX = mMoveRawX;
                break;

            case MotionEvent.ACTION_UP:

                int index = (getScrollX() + getWidth() / 2) / getWidth();
                int dx = index * getWidth() - getScrollX();
                mScroller.startScroll(getScrollX(), 0, dx, 0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset())
        {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }
}
