package com.wcc.www.customview.customstore;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/10/10.
 */

public class C43_RefreshLayoutExercise1 extends ViewGroup {

    private View mTargetContent;
    private Scroller mScroller;
    private final Drawable background;
    private final boolean enable_pull_up;
    private final boolean enable_pull_down;
    private ImageView iv_header_arrow;
    private ImageView iv_header_loading;
    private TextView tv_header_hint;
    private AnimationDrawable header_loading_background;
    private View headerView;
    private AnimationDrawable footer_loading_background;
    private TextView tv_footer_hint;
    private ImageView iv_footer_loading;
    private ImageView iv_footer_arrow;
    private View footerView;
    private int distanceOverToRefresh;

    public C43_RefreshLayoutExercise1(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.C42_RefreshLayout3);
        background = ta.getDrawable(R.styleable.C42_RefreshLayout3_smart_ui_background);
        enable_pull_up = ta.getBoolean(R.styleable.C42_RefreshLayout3_smart_ui_enable_pull_up, true);
        enable_pull_down = ta.getBoolean(R.styleable.C42_RefreshLayout3_smart_ui_enable_pull_down, true);
        ta.recycle();

        mScroller = new Scroller(context);
        distanceOverToRefresh = dp2px(50);
    }

    private float mLastY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        float y = ev.getY();
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                break;

            case MotionEvent.ACTION_UP:
                intercept = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if(mTargetContent != null)
                {
                    if(y > mLastY)
                        intercept = !canChildScrollUp();
                    else
                        intercept = !canChildScrollDown();
                }
                break;
        }
        mLastY = y;
        return intercept;
    }

    private boolean enableToScroll = true;
    public void setEnabled(boolean enable)
    {
        enableToScroll = enable;
    }

    public interface State{
        int NORMAL = 0;
        int DOWN = 1;
        int DOWN_RELEASABLE = 2;
        int DOWN_RELEASE = 3;
        int UP = 4;
        int UP_RELEASABLE = 5;
        int UP_RELEASE = 6;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        float dy = mLastY - y;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if(enableToScroll)
                    doScroll(dy);
                break;

            case MotionEvent.ACTION_UP:
                onStopScroll();
                break;
        }
        mLastY = y;
        return true;
    }

    private int currentState;
    private static final int DECELERATE_INTERPOLATION_FACTOR = 2;
    private void doScroll(float dy)
    {
        int scrollY = getScrollY();
        if(dy < 0) // 下拉
        {
            if(scrollY > 0) // 想要从上拉的状态恢复默认状态，取消上拉
            {
                if(scrollY < distanceOverToRefresh)
                {
                    if(currentState != State.UP)
                        updateState(State.UP);

                    if(- dy > scrollY)
                        dy = -scrollY;
                }
            }else
            {
                if(!enable_pull_down || (currentState > State.DOWN_RELEASABLE))
                    return;

                if(- scrollY >= distanceOverToRefresh)
                {
                    dy /= DECELERATE_INTERPOLATION_FACTOR;
                    if(currentState != State.DOWN_RELEASABLE)
                        updateState(State.DOWN_RELEASABLE);
                }
            }
        }else if(dy > 0) // 上拉
        {
            if(scrollY < 0) // 已经是下拉状态，取消下拉
            {
                if(- scrollY < distanceOverToRefresh)
                {
                    if(currentState != State.DOWN)
                        updateState(State.DOWN);

                    if(dy > - scrollY)
                        dy = - scrollY;
                }
            }else
            {
                if(!enable_pull_up || (currentState < State.UP && currentState != State.NORMAL))
                    return;

                if(scrollY >= distanceOverToRefresh)
                {
                    dy /= DECELERATE_INTERPOLATION_FACTOR;
                    if(currentState != State.UP_RELEASABLE)
                        updateState(State.UP_RELEASABLE);
                }
            }

        }

        dy /= DECELERATE_INTERPOLATION_FACTOR;
        scrollBy(0, (int) dy);
    }

    private void onStopScroll()
    {
        int scrollY = getScrollY();
        if(Math.abs(scrollY) >= distanceOverToRefresh)
        {
            if(scrollY > 0) // 上拉
            {
                updateState(State.UP_RELEASE);
                mScroller.startScroll(0, scrollY, 0, distanceOverToRefresh - scrollY);
            }else if(scrollY < 0) // 下拉
            {
                updateState(State.DOWN_RELEASE);
                mScroller.startScroll(0, scrollY, 0, - (distanceOverToRefresh + scrollY));
            }
        }else
        {
            if(scrollY != 0)
                mScroller.startScroll(0, scrollY, 0, -scrollY);
            updateState(State.NORMAL);
        }
    }

    public interface OnPullListener{
        void pullUp();

        void pullDown();
    }

    private OnPullListener mOnPullLintener;
    public void setOnPullListener(OnPullListener onPullListener)
    {
        this.mOnPullLintener = onPullListener;
    }

    private void updateState(int state)
    {
        switch (state)
        {
            case State.NORMAL:
                reset();
                break;

            case State.DOWN:
                if(currentState != State.NORMAL)
                    rotateHeaderArrow();
                tv_header_hint.setText("下拉刷新");
                break;

            case State.DOWN_RELEASABLE:
                rotateHeaderArrow();
                tv_header_hint.setText("释放刷新");
                break;

            case State.DOWN_RELEASE:
                enableToScroll = false;
                iv_header_arrow.setVisibility(INVISIBLE);
                iv_header_loading.setVisibility(VISIBLE);
                header_loading_background.start();
                tv_header_hint.setText("正在刷新...");
                if(mOnPullLintener != null)
                    mOnPullLintener.pullDown();
                break;

            case State.UP:
                if(currentState != State.NORMAL)
                    rotateFooterArrow();
                tv_footer_hint.setText("上拉加载更多");
                break;

            case State.UP_RELEASABLE:
                rotateFooterArrow();
                tv_footer_hint.setText("释放加载更多");
                break;

            case State.UP_RELEASE:
                enableToScroll = false;
                iv_footer_arrow.setVisibility(INVISIBLE);
                iv_footer_loading.setVisibility(VISIBLE);
                footer_loading_background.start();
                tv_footer_hint.setText("正在加载更多...");
                if(mOnPullLintener != null)
                    mOnPullLintener.pullUp();
                break;
        }

        currentState = state;
    }

    private void reset()
    {
        if(currentState != State.NORMAL)
        {
            if(currentState <= State.DOWN_RELEASE)
            {
                header_loading_background.stop();
                iv_header_arrow.setVisibility(VISIBLE);
                iv_header_arrow.setRotation(0);
                iv_header_loading.setVisibility(INVISIBLE);
                tv_header_hint.setText("下拉刷新");
            }else{
                footer_loading_background.stop();
                iv_footer_arrow.setVisibility(VISIBLE);
                iv_footer_arrow.setRotation(0);
                iv_footer_loading.setVisibility(INVISIBLE);
                tv_footer_hint.setText("上拉加载");
            }
        }

        enableToScroll = true;
        if(getScrollY() != 0)
            mScroller.startScroll(0, getScrollY(), 0, - getScrollY());
    }

    public void stopPullBehavior()
    {
        updateState(State.NORMAL);
    }

    private void rotateHeaderArrow()
    {
        float rotation = iv_header_arrow.getRotation();
        float offset = rotation % 180;
        float target = rotation + 180 - offset;
        ObjectAnimator animator = ObjectAnimator.ofFloat(iv_header_arrow, "rotation", rotation, target);
        animator.setDuration(150);
        animator.start();
    }

    private void rotateFooterArrow()
    {
        float rotation = iv_footer_arrow.getRotation();
        float offset = rotation % 180;
        float target = rotation + 180 - offset;
        ObjectAnimator animator = ObjectAnimator.ofFloat(iv_footer_arrow, "rotation", rotation, target);
        animator.setDuration(150);
        animator.start();
    }

    private boolean canChildScrollUp()
    {
        if(Build.VERSION.SDK_INT < 14)
        {
            if(mTargetContent instanceof AbsListView)
            {
                AbsListView absListView = (AbsListView) mTargetContent;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 ||
                        absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            }else
                return ViewCompat.canScrollVertically(mTargetContent, -1) || mTargetContent.getScrollY() > 0;
        }else
            return ViewCompat.canScrollVertically(mTargetContent, -1);
    }

    private boolean canChildScrollDown()
    {
        if(Build.VERSION.SDK_INT < 14)
        {
            if(mTargetContent instanceof AbsListView)
            {
                AbsListView absListView = (AbsListView) mTargetContent;
                return absListView.getChildCount() > 0 && (absListView.getLastVisiblePosition() != absListView.getChildCount() - 1 ||
                        absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getMeasuredHeight());
            }else
                return ViewCompat.canScrollVertically(mTargetContent, 1) ||
                         mTargetContent.getScrollY() < mTargetContent.getMeasuredHeight() - getMeasuredHeight();
        }else
            return ViewCompat.canScrollVertically(mTargetContent, 1);
    }

    private int dp2px(int dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset())
            scrollTo(0, mScroller.getCurrY());
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflateRefreshLayout();
    }

    private void inflateRefreshLayout()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if(enable_pull_down)
        {
            headerView = inflater.inflate(R.layout.layout_refresh_header, null);
            if(background != null)
                headerView.setBackground(background);
            iv_header_arrow = (ImageView) headerView.findViewById(R.id.iv_header_arrow);
            iv_header_loading = (ImageView) headerView.findViewById(R.id.iv_header_loading);
            tv_header_hint = (TextView) headerView.findViewById(R.id.tv_header_hint);
            header_loading_background = (AnimationDrawable) iv_header_loading.getBackground();
            addView(headerView, 0);
        }

        if(enable_pull_up)
        {
            footerView = inflater.inflate(R.layout.layout_refresh_footer, null);
            if(background != null)
                footerView.setBackground(background);
            iv_footer_arrow = (ImageView) footerView.findViewById(R.id.iv_footer_arrow);
            iv_footer_loading = (ImageView) footerView.findViewById(R.id.iv_footer_loading);
            tv_footer_hint = (TextView) footerView.findViewById(R.id.tv_footer_hint);
            footer_loading_background = (AnimationDrawable) iv_footer_loading.getBackground();
            addView(footerView, getChildCount());
        }
    }

    private void ensureTarget()
    {
        if(enable_pull_down)
            mTargetContent = getChildAt(1);
        else
            mTargetContent = getChildAt(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mTargetContent == null)
            ensureTarget();
        if(mTargetContent == null)
            return;
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++)
        {
            View child = getChildAt(i);
            child.measure(
                    MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                            MeasureSpec.EXACTLY)
            );
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(mTargetContent == null)
            ensureTarget();
        if(mTargetContent == null)
            return;
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++)
        {
            View child = getChildAt(i);
            if(child == headerView)
                child.layout(0, - child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            else if(child == footerView)
                child.layout(0, mTargetContent.getMeasuredHeight(), child.getMeasuredWidth(), mTargetContent.getMeasuredHeight() + child.getMeasuredHeight());
            else
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }
}
