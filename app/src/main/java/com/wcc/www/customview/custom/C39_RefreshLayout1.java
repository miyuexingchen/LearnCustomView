package com.wcc.www.customview.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/27.
 */

public class C39_RefreshLayout1 extends ViewGroup {

    private View mHeader, mFooter;
    private TextView tvHeader, tvFooter;
    private RelativeLayout pbHeader, pbFooter;
    private int overToRefreshY;
    private Scroller mLayoutScroller;
    public C39_RefreshLayout1(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mHeader = layoutInflater.inflate(R.layout.layout_header_view, null);
        mFooter = layoutInflater.inflate(R.layout.layout_footer_view, null);
        tvHeader = (TextView) mHeader.findViewById(R.id.tv_header);
        pbHeader = (RelativeLayout) mHeader.findViewById(R.id.pb_header);
        tvFooter = (TextView) mFooter.findViewById(R.id.tv_footer);
        pbFooter = (RelativeLayout) mFooter.findViewById(R.id.pb_footer);
        overToRefreshY = dp2px(35);
        mLayoutScroller = new Scroller(context);
        System.out.println("LifeCycle Method Constructor");
    }

    private float mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = mLastY - y;
                if(getScrollY() <= 0 &&
                        -getScrollY() <= dp2px(40))
                {
                    scrollBy(0, (int) dy);
                    System.out.println("mLastY = "+mLastY+", y = "+y+", dy = "+dy+", getScrollY() = "+getScrollY());
                    if(-getScrollY() >= overToRefreshY)
                        tvHeader.setText("松开刷新");
                }else if(getScrollY() > 0 && getScrollY() <= dp2px(40))
                {
                    scrollBy(0, (int) dy);
                    System.out.println("overToRefreshY = "+ overToRefreshY +" mLastY = "+mLastY+", y = "+y+", dy = "+dy+", getScrollY() = "+getScrollY());
                    if(getScrollY() >= overToRefreshY)
                        tvFooter.setText("松开加载更多");
                }
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
                System.out.println("getScrollY() = "+getScrollY());
                if(getScrollY() <= 0 && -getScrollY() >= overToRefreshY)
                {
                    mLayoutScroller.startScroll(0, getScrollY(), 0, -(getScrollY() + overToRefreshY));
                    tvHeader.setVisibility(GONE);
                    pbHeader.setVisibility(VISIBLE);
                    mOnRefreshListener.onRefresh();
                }else if(getScrollY() > 0 && getScrollY() >= overToRefreshY)
                {
                    mLayoutScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - overToRefreshY));
                    tvFooter.setVisibility(GONE);
                    pbFooter.setVisibility(VISIBLE);
                    mOnRefreshListener.onLoad();
                }else
                    mLayoutScroller.startScroll(0, getScrollY(), 0, -getScrollY());
                invalidate();
                break;
        }
        return true;
    }

    public interface OnRefreshListener{
        void onRefresh();

        void onLoad();
    }

    private OnRefreshListener mOnRefreshListener;
    public void setOnRefreshListener(OnRefreshListener onRefreshListener)
    {
        mOnRefreshListener = onRefreshListener;
    }

    public void refreshDone()
    {
        mLayoutScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        pbHeader.setVisibility(GONE);
        tvHeader.setText("下拉刷新");
        tvHeader.setVisibility(VISIBLE);
    }

    public void loadDone()
    {
        mLayoutScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        pbFooter.setVisibility(GONE);
        tvFooter.setText("上拉加载");
        tvFooter.setVisibility(VISIBLE);
    }

    @Override
    public void computeScroll() {
        if(mLayoutScroller.computeScrollOffset())
        {
            scrollTo(mLayoutScroller.getCurrX(), mLayoutScroller.getCurrY());
            invalidate();
        }
    }

    private int dp2px(int dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        System.out.println("LifeCycle Method onSizeChanged");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mHeader.setLayoutParams(params);
        mFooter.setLayoutParams(params);
        addView(mHeader);
        addView(mFooter);
        System.out.println("LifeCycle Method onFinishInflate");

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++)
        {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        System.out.println("LifeCycle Method onMeasure");

    }

    private int mContentHeight;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mContentHeight = 0;
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++)
        {
            View child = getChildAt(i);
            if(child == mHeader)
                child.layout(0, -child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            else if(child == mFooter)
                child.layout(0, mContentHeight, child.getMeasuredWidth(), mContentHeight + child.getMeasuredHeight());
            else
            {
                child.layout(0, mContentHeight, child.getMeasuredWidth(), mContentHeight + child.getMeasuredHeight());
                mContentHeight += child.getMeasuredHeight();
            }
        }
        System.out.println("LifeCycle Method onLayout");

    }
}
