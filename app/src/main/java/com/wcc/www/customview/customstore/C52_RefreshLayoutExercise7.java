package com.wcc.www.customview.customstore;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
 * Created by 王晨晨 on 2017/10/13.
 */

public class C52_RefreshLayoutExercise7 extends ViewGroup {

    private final boolean mEnablePullDown;
    private final boolean mEnablePullUp;
    private final Drawable mBackground;
    private View header;
    private ImageView iv_header_arrow;
    private ImageView iv_header_loading;
    private TextView tv_header_hint;
    private AnimationDrawable header_loading_background;
    private View footer;
    private ImageView iv_footer_arrow;
    private ImageView iv_footer_loading;
    private TextView tv_footer_hint;
    private AnimationDrawable footer_loading_background;

    private Scroller mScroller;
    private int distanceOverToLoad;
    public C52_RefreshLayoutExercise7(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        distanceOverToLoad = dp2px(50);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.C42_RefreshLayout3);
        mEnablePullDown = ta.getBoolean(R.styleable.C42_RefreshLayout3_smart_ui_enable_pull_down, true);
        mEnablePullUp = ta.getBoolean(R.styleable.C42_RefreshLayout3_smart_ui_enable_pull_up, true);
        mBackground = ta.getDrawable(R.styleable.C42_RefreshLayout3_smart_ui_background);
        ta.recycle();
    }

    private boolean enableToScroll = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if(enableToScroll)
                    doScroll(mLasyY - y);
                break;

            case MotionEvent.ACTION_UP:
                if(enableToScroll)
                    onStopScroll();
                break;
        }
        mLasyY = y;
        return true;
    }
    private int currentState;
    public interface State{
        int NORMAL = 0;
        int DOWN = 1;
        int DOWN_RELEASABLE = 2;
        int DOWN_RELEASE = 3;
        int UP = 4;
        int UP_RELEASABLE = 5;
        int UP_RELEASE = 6;
        int SUCCESS = 7;
        int FAILURE = 8;
    }
    private void doScroll(float dy)
    {
        int scrollY = getScrollY();
        if(dy > 0)
        {
            if(scrollY < 0)
            {
                if(-scrollY < distanceOverToLoad)
                {
                    if(currentState != State.DOWN)
                        updateState(State.DOWN);

                    if(dy > -scrollY)
                        dy = -scrollY;
                }
            }else{
                if(!mEnablePullUp)
                    return;
                if(currentState != State.UP_RELEASABLE)
                {
                    dy /= 2;
                    updateState(State.UP_RELEASABLE);
                }
            }
        }else if(dy < 0)
        {
            if(scrollY > 0)
            {
                if(scrollY < distanceOverToLoad)
                {
                    if(currentState != State.UP)
                        updateState(State.UP);

                    if(-dy > scrollY)
                        dy = -scrollY;
                }
            }else{
                if(!mEnablePullDown)
                    return;
                if(currentState != State.DOWN_RELEASABLE)
                {
                    dy /= 2;
                    updateState(State.DOWN_RELEASABLE);
                }
            }
        }
        dy /= 2;
        scrollBy(0, (int) dy);
    }

    private void onStopScroll()
    {
        int scrollY = getScrollY();
        if(Math.abs(scrollY) >= distanceOverToLoad)
        {
            if(scrollY > 0)
            {
                updateState(State.UP_RELEASE);
                mScroller.startScroll(0, scrollY, 0, distanceOverToLoad - scrollY);
            }else if(scrollY < 0)
            {
                updateState(State.DOWN_RELEASE);
                mScroller.startScroll(0, scrollY, 0, -(distanceOverToLoad + scrollY));
            }
        }else{
            updateState(State.NORMAL);
        }
    }

    public void stopPullBehavior(int state)
    {
        updateState(state);
        new Handler(){
            @Override
            public void handleMessage(Message msg) {
                updateState(State.NORMAL);
            }
        }.sendEmptyMessageDelayed(0, 1000);
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
                    rotateArrow(iv_header_arrow);
                tv_header_hint.setText("下拉刷新");
                break;

            case State.DOWN_RELEASABLE:
                rotateArrow(iv_header_arrow);
                tv_header_hint.setText("释放刷新");
                break;

            case State.DOWN_RELEASE:
                enableToScroll = false;
                iv_header_arrow.setVisibility(INVISIBLE);
                iv_header_loading.setVisibility(VISIBLE);
                header_loading_background.start();
                tv_header_hint.setText("正在刷新...");
                if(mOnPullListener != null)
                    mOnPullListener.pullDown();
                break;

            case State.UP:
                if(currentState != State.NORMAL)
                    rotateArrow(iv_footer_arrow);
                tv_footer_hint.setText("上拉加载更多");
                break;

            case State.UP_RELEASABLE:
                rotateArrow(iv_footer_arrow);
                tv_footer_hint.setText("释放加载更多");
                break;

            case State.UP_RELEASE:
                enableToScroll = false;
                iv_footer_arrow.setVisibility(INVISIBLE);
                iv_footer_loading.setVisibility(VISIBLE);
                footer_loading_background.start();
                tv_footer_hint.setText("正在加载更多");
                if(mOnPullListener != null)
                    mOnPullListener.pullUp();
                break;

            case State.SUCCESS:
                if(currentState == State.DOWN_RELEASE)
                {
                    iv_header_loading.setVisibility(INVISIBLE);
                    tv_header_hint.setText("刷新成功");
                }else if(currentState == State.UP_RELEASE)
                {
                    iv_footer_loading.setVisibility(INVISIBLE);
                    tv_footer_hint.setText("加载更多成功");
                }
                break;

            case State.FAILURE:
                if(currentState == State.DOWN_RELEASE)
                {
                    iv_header_loading.setVisibility(INVISIBLE);
                    tv_header_hint.setText("刷新失败");
                }else if(currentState == State.UP_RELEASE)
                {
                    iv_footer_loading.setVisibility(INVISIBLE);
                    tv_footer_hint.setText("加载更多失败");
                }
                break;
        }

        if(state != State.SUCCESS && state != State.FAILURE)
            currentState = state;
    }

    private void reset()
    {
        if(currentState != State.NORMAL)
        {
            if(currentState <= State.DOWN_RELEASE)
            {
                header_loading_background.stop();
                iv_header_loading.setVisibility(INVISIBLE);
                iv_header_arrow.setVisibility(VISIBLE);
                iv_header_arrow.setRotation(0);
                tv_header_hint.setText("下拉刷新");
            }else if(currentState > State.DOWN_RELEASE && currentState < State.SUCCESS)
            {
                footer_loading_background.stop();
                iv_footer_loading.setVisibility(INVISIBLE);
                iv_footer_arrow.setVisibility(VISIBLE);
                iv_footer_arrow.setRotation(0);
                tv_footer_hint.setText("上拉加载更多");
            }
        }

        enableToScroll = true;
        int scrollY = getScrollY();
        if(scrollY != 0)
            mScroller.startScroll(0, scrollY, 0, -scrollY);
    }

    private void rotateArrow(View v)
    {
        float rotation = v.getRotation();
        float offset = rotation % 180;
        float target = rotation + 180 - offset;
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotation", rotation, target);
        animator.setDuration(150);
        animator.start();
    }

    public interface OnPullListener{
        void pullDown();
        void pullUp();
    }
    private OnPullListener mOnPullListener;
    public void setOnPullListener(OnPullListener listener)
    {
        mOnPullListener = listener;
    }

    private float mLasyY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        boolean intercept = false;
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if(mTarget != null)
                {
                    if(mLasyY > y)
                        intercept = !canChildScrollDown();
                    else if(mLasyY < y)
                        intercept = !canChildScrollUp();
                }
                break;
        }
        mLasyY = y;
        return intercept;
    }

    private boolean canChildScrollDown()
    {
        if(Build.VERSION.SDK_INT < 14)
        {
            if(mTarget instanceof AbsListView)
            {
                AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0 && (absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getMeasuredHeight() ||
                        absListView.getLastVisiblePosition() != absListView.getChildCount() - 1);
            }else
                return ViewCompat.canScrollVertically(mTarget, 1) || mTarget.getScrollY() < mTarget.getMeasuredHeight() - getMeasuredHeight();
        }else
            return ViewCompat.canScrollVertically(mTarget, 1);
    }

    private boolean canChildScrollUp()
    {
        if(Build.VERSION.SDK_INT < 14)
        {
            if(mTarget instanceof AbsListView)
            {
                AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 ||
                        absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            }else
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
        }else
            return ViewCompat.canScrollVertically(mTarget, -1);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mTarget == null) ensureTarget();
        if(mTarget == null) return;
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++)
        {
            View child = getChildAt(i);
            if(child == header)
                child.layout(0, - child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            else if(child == footer)
                child.layout(0, mTarget.getMeasuredHeight(), child.getMeasuredWidth(), mTarget.getMeasuredHeight() + child.getMeasuredHeight());
            else
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    private View mTarget;
    private void ensureTarget()
    {
        if(mEnablePullDown)
            mTarget = getChildAt(1);
        else
            mTarget = getChildAt(0);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mTarget == null) ensureTarget();
        if(mTarget == null) return;
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++)
            getChildAt(i).measure(
                    MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY)
            );
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        if(mEnablePullDown)
        {
            header = layoutInflater.inflate(R.layout.layout_refresh_header, null);
            iv_header_arrow = (ImageView) header.findViewById(R.id.iv_header_arrow);
            iv_header_loading = (ImageView) header.findViewById(R.id.iv_header_loading);
            tv_header_hint = (TextView) header.findViewById(R.id.tv_header_hint);
            header_loading_background = (AnimationDrawable) iv_header_loading.getBackground();
            if(mBackground != null)
                header.setBackground(mBackground);
            addView(header, 0);
        }

        if(mEnablePullUp)
        {
            footer = layoutInflater.inflate(R.layout.layout_refresh_footer, null);
            iv_footer_arrow = (ImageView) footer.findViewById(R.id.iv_footer_arrow);
            iv_footer_loading = (ImageView) footer.findViewById(R.id.iv_footer_loading);
            tv_footer_hint = (TextView) footer.findViewById(R.id.tv_footer_hint);
            footer_loading_background = (AnimationDrawable) iv_footer_loading.getBackground();
            if(mBackground != null)
                footer.setBackground(mBackground);
            addView(footer, getChildCount());
        }
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset())
            scrollTo(0, mScroller.getCurrY());
        invalidate();
    }

    private int dp2px(int dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
