package com.wcc.www.customview.custom;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/10/20.
 */

public class C79_RefreshLayoutExercise18 extends ViewGroup {

    private Scroller mScroller;
    private int distanceOverToLoad;
    private final boolean mEnablePullDown;
    private final boolean mEnablePullUp;
    private final Drawable mBackground;
    private View header;
    private ImageView header_arrow;
    private ImageView header_loading;
    private TextView header_hint;
    private AnimationDrawable header_anim;
    private View footer;
    private ImageView footer_arrow;
    private ImageView footer_loading;
    private TextView footer_hint;
    private AnimationDrawable footer_anim;

    public C79_RefreshLayoutExercise18(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        distanceOverToLoad = dp2px(50);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.C42_RefreshLayout3);
        mEnablePullDown = ta.getBoolean(R.styleable.C42_RefreshLayout3_smart_ui_enable_pull_down, true);
        mEnablePullUp = ta.getBoolean(R.styleable.C42_RefreshLayout3_smart_ui_enable_pull_up, true);
        mBackground = ta.getDrawable(R.styleable.C42_RefreshLayout3_smart_ui_background);
        ta.recycle();
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
                mScroller.startScroll(0, scrollY, 0, -distanceOverToLoad - scrollY);
            }
        }else
            updateState(State.NORMAL);
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
                if(scrollY >= distanceOverToLoad)
                {
                    if(currentState != State.UP_RELEASABLE)
                    {
                        dy /= 3;
                        updateState(State.UP_RELEASABLE);
                    }
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
                if(-scrollY >= distanceOverToLoad)
                {
                    if(currentState != State.DOWN_RELEASABLE)
                    {
                        dy /= 3;
                        updateState(State.DOWN_RELEASABLE);
                    }
                }
            }
        }
        dy /= 3;
        scrollBy(0, (int) dy);
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
                    rotateArrow(header_arrow);
                header_hint.setText("下拉刷新");
                break;

            case State.DOWN_RELEASABLE:
                rotateArrow(header_arrow);
                header_hint.setText("释放刷新");
                break;

            case State.DOWN_RELEASE:
                enableToScroll = false;
                header_arrow.setVisibility(INVISIBLE);
                header_loading.setVisibility(VISIBLE);
                header_anim.start();
                header_hint.setText("正在刷新");
                if(mOnPullListener != null)
                    mOnPullListener.pullDown();
                break;

            case State.UP:
                if(currentState != State.NORMAL)
                    rotateArrow(footer_arrow);
                footer_hint.setText("上拉加载更多");
                break;

            case State.UP_RELEASABLE:
                rotateArrow(footer_arrow);
                footer_hint.setText("释放加载更多");
                break;

            case State.UP_RELEASE:
                enableToScroll = false;
                footer_arrow.setVisibility(INVISIBLE);
                footer_loading.setVisibility(VISIBLE);
                footer_anim.start();
                footer_hint.setText("正在加载更多");
                if(mOnPullListener != null)
                    mOnPullListener.pullUp();
                break;

            case State.SUCCESS:
                if(currentState == State.DOWN_RELEASE)
                {
                    header_anim.stop();
                    header_loading.setVisibility(INVISIBLE);
                    header_arrow.setVisibility(VISIBLE);
                    header_arrow.setRotation(0);
                    header_arrow.setImageResource(R.drawable.refresh_succeed);
                    header_hint.setText("刷新成功");
                }else if(currentState == State.UP_RELEASE)
                {
                    footer_anim.stop();
                    footer_loading.setVisibility(INVISIBLE);
                    footer_arrow.setVisibility(VISIBLE);
                    footer_arrow.setRotation(0);
                    footer_arrow.setImageResource(R.drawable.refresh_succeed);
                    footer_hint.setText("加载更多成功");
                }
                break;

            case State.FAILURE:
                if(currentState == State.UP_RELEASE)
                {
                    footer_anim.stop();
                    footer_loading.setVisibility(INVISIBLE);
                    footer_arrow.setVisibility(VISIBLE);
                    footer_arrow.setRotation(0);
                    footer_arrow.setImageResource(R.drawable.refresh_failed);
                    footer_hint.setText("加载更多失败");
                }else if(currentState == State.DOWN_RELEASE)
                {
                    header_anim.stop();
                    header_loading.setVisibility(INVISIBLE);
                    header_arrow.setVisibility(VISIBLE);
                    header_arrow.setRotation(0);
                    header_arrow.setImageResource(R.drawable.refresh_failed);
                    header_hint.setText("刷新失败");
                }
                break;
        }

        if(state != State.SUCCESS && state != State.FAILURE)
            currentState = state;
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

    private void reset()
    {
        if(currentState != State.NORMAL)
        {
            if(currentState <= State.DOWN_RELEASE)
            {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        header_arrow.setRotation(0);
                        header_arrow.setImageResource(R.drawable.smart_ui_pullable_layout_indicate_arrow);
                        header_hint.setText("下拉刷新");
                    }
                }, 500);
            }else{
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        footer_arrow.setRotation(0);
                        footer_arrow.setImageResource(R.drawable.smart_ui_pullable_layout_indicate_arrow_bottom);
                        footer_hint.setText("上拉加载更多");
                    }
                }, 500);
            }
        }

        enableToScroll = true;
        int scrollY = getScrollY();
        if(scrollY != 0)
            mScroller.startScroll(0, scrollY, 0, -scrollY);
    }

    private float mLasyY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        float y = ev.getY();
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
                        intercept = !ViewCompat.canScrollVertically(mTarget, 1);
                    else if(mLasyY < y)
                        intercept = !ViewCompat.canScrollVertically(mTarget, -1);
                }
                break;
        }
        mLasyY = y;
        return intercept;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mTarget == null) ensureTarget();
        if(mTarget == null) return;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i ++)
        {
            View child = getChildAt(i);
            if(child == header)
                child.layout(0, -child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            else if(child == footer)
                child.layout(0, mTarget.getMeasuredHeight(), child.getMeasuredWidth(), mTarget.getMeasuredHeight() + child.getMeasuredHeight());
            else
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
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

    private View mTarget;
    private void ensureTarget()
    {
        if(mEnablePullDown)
            mTarget = getChildAt(1);
        else
            mTarget = getChildAt(0);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        if(mEnablePullDown)
        {
            header = layoutInflater.inflate(R.layout.layout_refresh_header, null);
            header_arrow = (ImageView) header.findViewById(R.id.iv_header_arrow);
            header_loading = (ImageView) header.findViewById(R.id.iv_header_loading);
            header_hint = (TextView) header.findViewById(R.id.tv_header_hint);
            header_anim = (AnimationDrawable) header_loading.getBackground();
            if(mBackground != null)
                header.setBackground(mBackground);
            addView(header, 0);
        }

        if(mEnablePullUp)
        {
            footer = layoutInflater.inflate(R.layout.layout_refresh_footer, null);
            footer_arrow = (ImageView) footer.findViewById(R.id.iv_footer_arrow);
            footer_loading = (ImageView) footer.findViewById(R.id.iv_footer_loading);
            footer_hint = (TextView) footer.findViewById(R.id.tv_footer_hint);
            footer_anim = (AnimationDrawable) footer_loading.getBackground();
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
