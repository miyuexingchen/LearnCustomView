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
 * Created by 王晨晨 on 2017/10/17.
 */

public class C65_RefreshLayoutExercise12 extends ViewGroup {

    private final boolean mEnablePullDown;
    private final boolean mEnablePullUp;
    private final Drawable mBackground;
    private View refreshView;
    private ImageView iv_refresh_arrow;
    private ImageView iv_refresh_loading;
    private TextView tv_refresh_hint;
    private AnimationDrawable refresh_loading_anim;
    private View loadmoreView;
    private ImageView iv_loadmore_arrow;
    private ImageView iv_loadmore_loading;
    private TextView tv_loadmore_hint;
    private AnimationDrawable loadmore_loading_anim;

    private Scroller mScroller;
    private int distanceOverToLoad;

    public C65_RefreshLayoutExercise12(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        distanceOverToLoad = dp2px(60);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.C42_RefreshLayout3);
        mEnablePullDown = ta.getBoolean(R.styleable.C42_RefreshLayout3_smart_ui_enable_pull_down, true);
        mEnablePullUp = ta.getBoolean(R.styleable.C42_RefreshLayout3_smart_ui_enable_pull_up, true);
        mBackground = ta.getDrawable(R.styleable.C42_RefreshLayout3_smart_ui_background);
        ta.recycle();
    }

    public void stopPullBehavior(int state)
    {
        updateState(state);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                updateState(State.NORMAL);
            }
        }, 1000);
//        new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                updateState(State.NORMAL);
//            }
//        }.sendEmptyMessageDelayed(0, 1000);
    }

    private boolean enableToScroll = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if(enableToScroll)
                    doScroll(mLastY - y);
                break;

            case MotionEvent.ACTION_UP:
                if(enableToScroll)
                    onStopScroll();
                break;
        }
        mLastY = y;
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
                mScroller.startScroll(0, scrollY, 0, -distanceOverToLoad-scrollY);
            }
        }else
            updateState(State.NORMAL);
    }

    private int currentState;
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
                if (-scrollY >= distanceOverToLoad) {
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
        switch (state) {
            case State.NORMAL:
                reset();
                break;

            case State.DOWN:
                if (currentState != State.NORMAL)
                    rotateArrow(iv_refresh_arrow);
                tv_refresh_hint.setText("pull down to refresh");
                break;

            case State.DOWN_RELEASABLE:
                rotateArrow(iv_refresh_arrow);
                tv_refresh_hint.setText("release to refresh");
                break;

            case State.DOWN_RELEASE:
                enableToScroll = false;
                iv_refresh_arrow.setVisibility(INVISIBLE);
                iv_refresh_loading.setVisibility(VISIBLE);
                refresh_loading_anim.start();
                tv_refresh_hint.setText("refreshing");
                if (mOnPullListener != null)
                    mOnPullListener.pullDown();
                break;

            case State.UP:
                if (currentState != State.NORMAL)
                    rotateArrow(iv_loadmore_arrow);
                tv_loadmore_hint.setText("pull up to load more");
                break;

            case State.UP_RELEASABLE:
                rotateArrow(iv_loadmore_arrow);
                tv_loadmore_hint.setText("release to load more");
                break;

            case State.UP_RELEASE:
                enableToScroll = false;
                iv_loadmore_arrow.setVisibility(INVISIBLE);
                iv_loadmore_loading.setVisibility(VISIBLE);
                loadmore_loading_anim.start();
                tv_loadmore_hint.setText("loading more");
                if (mOnPullListener != null)
                    mOnPullListener.pullUp();
                break;

            case State.SUCCESS:
                if (currentState == State.DOWN_RELEASE) {
                    refresh_loading_anim.stop();
                    iv_refresh_loading.setVisibility(INVISIBLE);
                    iv_refresh_arrow.setVisibility(VISIBLE);
                    iv_refresh_arrow.setRotation(0);
                    iv_refresh_arrow.setImageResource(R.drawable.refresh_succeed);
                    tv_refresh_hint.setText("refresh succeed");
                } else if (currentState == State.UP_RELEASE) {
                    loadmore_loading_anim.stop();
                    iv_loadmore_loading.setVisibility(INVISIBLE);
                    iv_loadmore_arrow.setVisibility(VISIBLE);
                    iv_loadmore_arrow.setRotation(0);
                    iv_loadmore_arrow.setImageResource(R.drawable.refresh_succeed);
                    tv_loadmore_hint.setText("load more succeed");
                }
                break;

            case State.FAILURE:
                if (currentState == State.DOWN_RELEASE) {
                    refresh_loading_anim.stop();
                    iv_refresh_loading.setVisibility(INVISIBLE);
                    iv_refresh_arrow.setVisibility(VISIBLE);
                    iv_refresh_arrow.setRotation(0);
                    iv_refresh_arrow.setImageResource(R.drawable.refresh_failed);
                    tv_refresh_hint.setText("refresh failed");
                } else if (currentState == State.UP_RELEASE)
                {
                    loadmore_loading_anim.stop();
                    iv_loadmore_loading.setVisibility(INVISIBLE);
                    iv_loadmore_arrow.setVisibility(VISIBLE);
                    iv_loadmore_arrow.setRotation(0);
                    iv_loadmore_arrow.setImageResource(R.drawable.refresh_failed);
                    tv_loadmore_hint.setText("load more failed");
                }
                break;
        }

        if(state != State.SUCCESS && state != State.FAILURE)
            currentState = state;
    }
    private OnPullListener mOnPullListener;
    public void setOnPullListener(OnPullListener listener)
    {
        mOnPullListener = listener;
    }
    public interface OnPullListener{
        void pullDown();

        void pullUp();
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

    private void reset()
    {
        if(currentState != State.NORMAL)
        {
            if(currentState <= State.DOWN_RELEASE)
            {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_refresh_arrow.setRotation(0);
                        iv_refresh_arrow.setImageResource(R.drawable.smart_ui_pullable_layout_indicate_arrow);
                        tv_refresh_hint.setText("pull down to refresh");
                    }
                }, 500);
            }else if(currentState >= State.UP)
            {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_loadmore_arrow.setRotation(0);
                        iv_loadmore_arrow.setImageResource(R.drawable.smart_ui_pullable_layout_indicate_arrow_bottom);
                        tv_loadmore_hint.setText("pull up to load more");
                    }
                }, 500);
            }
        }

        enableToScroll = true;
        int scrollY = getScrollY();
        if(scrollY != 0)
            mScroller.startScroll(0, scrollY, 0, -scrollY);
    }

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

    private float mLastY;
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
                    if(mLastY > y)
                        intercept = !ViewCompat.canScrollVertically(mTarget, 1);
                    else if(mLastY < y)
                        intercept = !ViewCompat.canScrollVertically(mTarget, -1);
                }
                break;
        }
        mLastY = y;
        return intercept;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mTarget == null) ensureTarget();
        if(mTarget == null) return;
        int childCount = getChildCount();
        for(int i = 0; i < childCount; i ++)
        {
            View child = getChildAt(i);
            if(child == refreshView)
                child.layout(0, -child.getMeasuredHeight(), child.getMeasuredWidth(), 0);
            else if(child == loadmoreView)
                child.layout(0, mTarget.getMeasuredHeight(), child.getMeasuredWidth(), mTarget.getMeasuredHeight() + child.getMeasuredHeight());
            else
                child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) ensureTarget();
        if (mTarget == null) return;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++)
            getChildAt(i).measure(
                    MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                            MeasureSpec.EXACTLY)
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
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if(mEnablePullDown)
        {
            refreshView = inflater.inflate(R.layout.layout_refresh_header, null);
            iv_refresh_arrow = (ImageView) refreshView.findViewById(R.id.iv_header_arrow);
            iv_refresh_loading = (ImageView) refreshView.findViewById(R.id.iv_header_loading);
            tv_refresh_hint = (TextView) refreshView.findViewById(R.id.tv_header_hint);
            refresh_loading_anim = (AnimationDrawable) iv_refresh_loading.getBackground();
            if(mBackground != null)
                refreshView.setBackground(mBackground);
            addView(refreshView, 0);
        }

        if(mEnablePullUp)
        {
            loadmoreView = inflater.inflate(R.layout.layout_refresh_footer, null);
            iv_loadmore_arrow = (ImageView) loadmoreView.findViewById(R.id.iv_footer_arrow);
            iv_loadmore_loading = (ImageView) loadmoreView.findViewById(R.id.iv_footer_loading);
            tv_loadmore_hint = (TextView) loadmoreView.findViewById(R.id.tv_footer_hint);
            loadmore_loading_anim = (AnimationDrawable) iv_loadmore_loading.getBackground();
            if(mBackground != null)
                loadmoreView.setBackground(mBackground);
            addView(loadmoreView, getChildCount());
        }
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
}
