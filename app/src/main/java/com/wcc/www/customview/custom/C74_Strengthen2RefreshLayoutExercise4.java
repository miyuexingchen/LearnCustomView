package com.wcc.www.customview.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wcc.www.customview.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 王晨晨 on 2017/10/19.
 */

public class C74_Strengthen2RefreshLayoutExercise4 extends RelativeLayout {

    private CTimer mTimer;
    private final Animation arrowAnimation;
    private final Animation loadingAnimation;
    private View refreshView;
    private View contentView;
    private View loadmoreView;
    private ImageView iv_refresh_arrow;
    private ImageView iv_refresh_loading;
    private ImageView iv_refresh_hint;
    private TextView tv_refresh_hint;
    private ImageView iv_loadmore_arrow;
    private ImageView iv_loadmore_loading;
    private ImageView iv_loadmore_hint;
    private TextView tv_loadmore_hint;

    public C74_Strengthen2RefreshLayoutExercise4(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTimer = new CTimer(moveHandler);
        arrowAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_arrow);
        loadingAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_loading);
        LinearInterpolator li = new LinearInterpolator();
        arrowAnimation.setInterpolator(li);
        loadingAnimation.setInterpolator(li);
    }

    public void refreshFinish()
    {
        iv_refresh_loading.clearAnimation();
        iv_refresh_loading.setVisibility(INVISIBLE);
        iv_refresh_hint.setVisibility(VISIBLE);
        iv_refresh_hint.setImageResource(R.drawable.refresh_succeed);
        tv_refresh_hint.setText("refresh succeed");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                currentState = DOWN;
                hide();
            }
        }, 500);
    }

    public void loadmoreFinish()
    {
        iv_loadmore_loading.clearAnimation();
        iv_loadmore_loading.setVisibility(INVISIBLE);
        iv_loadmore_hint.setVisibility(VISIBLE);
        iv_loadmore_hint.setImageResource(R.drawable.refresh_succeed);
        tv_loadmore_hint.setText("load more succeed");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                currentState = UP;
                hide();
            }
        }, 500);
    }

    private boolean canPullDown = true, canPullUp = true;
    private float mLasy;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLasy = y;
                cancel();
                releasePull();
                break;

            case MotionEvent.ACTION_MOVE:
                if(pullDownY > 0 || (canPullDown && !ViewCompat.canScrollVertically(contentView, -1) && currentState != UP_RELEASE))
                {
                    pullDownY += (y - mLasy) / ratio;
                    if(pullDownY < 0)
                    {
                        pullDownY = 0;
                        canPullUp = true;
                        canPullDown = false;
                    }
                    if(pullDownY > getMeasuredHeight())
                        pullDownY = getMeasuredHeight();
                    if(currentState == DOWN_RELEASE)
                        whenRefreshOrLoadmoreDetermineToScroll = true;
                }else if(pullUpY < 0 || (canPullUp && !ViewCompat.canScrollVertically(contentView, 1) && currentState != DOWN_RELEASE))
                {
                    pullUpY += (y - mLasy) / ratio;
                    if(pullUpY > 0)
                    {
                        pullUpY = 0;
                        canPullUp = false;
                        canPullDown = true;
                    }
                    if(pullUpY < -getMeasuredHeight())
                        pullUpY = -getMeasuredHeight();
                    if(currentState == UP_RELEASE)
                        whenRefreshOrLoadmoreDetermineToScroll = true;
                }
                mLasy = y;
                ratio = (float) (3 + 3 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY - pullUpY)));
                requestLayout();
                if(pullDownY > 0)
                {
                    if(pullDownY <= distanceOverToLoad && currentState == DOWN_RELEASABLE)
                        changeState(DOWN);
                    if(pullDownY >= distanceOverToLoad && currentState != DOWN_RELEASABLE)
                        changeState(DOWN_RELEASABLE);
                }else if(pullUpY < 0)
                {
                    if(-pullUpY <= distanceOverToLoad && currentState == UP_RELEASABLE)
                        changeState(UP);
                    if(-pullUpY >= distanceOverToLoad && currentState != UP_RELEASABLE)
                        changeState(UP_RELEASABLE);
                }
                if(pullDownY - pullUpY > 8)
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                break;

            case MotionEvent.ACTION_UP:
                if(pullDownY > distanceOverToLoad || -pullUpY > distanceOverToLoad)
                    whenRefreshOrLoadmoreDetermineToScroll = false;
                if(currentState == DOWN_RELEASABLE)
                    changeState(DOWN_RELEASE);
                else if(currentState == UP_RELEASABLE)
                    changeState(UP_RELEASE);
                hide();
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    private void releasePull()
    {
        canPullDown = true;
        canPullUp = true;
    }

    private boolean isAlreadyLayout;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!isAlreadyLayout)
        {
            isAlreadyLayout = true;
            refreshView = getChildAt(0);
            contentView = getChildAt(1);
            loadmoreView = getChildAt(2);
            initRefreshViewAndLoadmoreView();
            distanceOverToLoad = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight();
        }
        refreshView.layout(0, (int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(), refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
        contentView.layout(0, (int) (pullDownY + pullUpY), contentView.getMeasuredWidth(), (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight());
        loadmoreView.layout(0, (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight(), loadmoreView.getMeasuredWidth(), (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight() + loadmoreView.getMeasuredHeight());
    }

    private void initRefreshViewAndLoadmoreView()
    {
        iv_refresh_arrow = (ImageView) refreshView.findViewById(R.id.iv_refresh_arrow);
        iv_refresh_loading = (ImageView) refreshView.findViewById(R.id.iv_refresh_loading);
        iv_refresh_hint = (ImageView) refreshView.findViewById(R.id.iv_refresh_hint);
        tv_refresh_hint = (TextView) refreshView.findViewById(R.id.tv_refresh_hint);
        iv_loadmore_arrow = (ImageView) loadmoreView.findViewById(R.id.iv_loadmore_arrow);
        iv_loadmore_loading = (ImageView) loadmoreView.findViewById(R.id.iv_loadmore_loading);
        iv_loadmore_hint = (ImageView) loadmoreView.findViewById(R.id.iv_loadmore_hint);
        tv_loadmore_hint = (TextView) loadmoreView.findViewById(R.id.tv_loadmore_hint);
    }

    private float moveSpeed = 8;
    private float pullDownY, pullUpY;
    private float ratio = 3;
    private int currentState;
    public static final int DOWN = 0;
    public static final int DOWN_RELEASABLE = 1;
    public static final int DOWN_RELEASE = 2;
    public static final int UP = 3;
    public static final int UP_RELEASABLE = 4;
    public static final int UP_RELEASE = 5;
    private int distanceOverToLoad;
    private boolean whenRefreshOrLoadmoreDetermineToScroll;
    private Handler moveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(pullDownY > 0) pullDownY -= moveSpeed;
            else if(pullUpY < 0) pullUpY += moveSpeed;
            moveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY - pullUpY)));
            if(!whenRefreshOrLoadmoreDetermineToScroll)
            {
                if(currentState == DOWN_RELEASE && pullDownY <= distanceOverToLoad)
                {
                    pullDownY = distanceOverToLoad;
                    cancel();
                }else if(currentState == UP_RELEASE && -pullUpY <= distanceOverToLoad)
                {
                    pullUpY = -distanceOverToLoad;
                    cancel();
                }
            }
            if(pullDownY < 0)
            {
                pullDownY = 0;
                if(currentState != DOWN_RELEASE)
                    changeState(DOWN);
                cancel();
            }else if(pullUpY > 0)
            {
                pullUpY = 0;
                if(currentState != UP_RELEASE)
                    changeState(UP);
                cancel();
            }
            requestLayout();
        }
    };

    private void cancel()
    {
        mTimer.cancel();
    }

    private void hide()
    {
        mTimer.schedule(5);
    }

    private void changeState(int state)
    {
        currentState = state;
        switch (state)
        {
            case DOWN:
                iv_refresh_arrow.setVisibility(VISIBLE);
                iv_refresh_arrow.clearAnimation();
                iv_refresh_hint.setVisibility(INVISIBLE);
                tv_refresh_hint.setText("pull down to refresh");
                break;

            case DOWN_RELEASABLE:
                iv_refresh_arrow.startAnimation(arrowAnimation);
                tv_refresh_hint.setText("release to refresh");
                break;

            case DOWN_RELEASE:
                iv_refresh_arrow.clearAnimation();
                iv_refresh_arrow.setVisibility(INVISIBLE);
                iv_refresh_loading.setVisibility(VISIBLE);
                iv_refresh_loading.startAnimation(loadingAnimation);
                tv_refresh_hint.setText("refreshing");
                if(mOnPullListener != null)
                    mOnPullListener.onRefresh();
                break;

            case UP:
                iv_loadmore_arrow.setVisibility(VISIBLE);
                iv_loadmore_arrow.clearAnimation();
                iv_loadmore_hint.setVisibility(INVISIBLE);
                tv_loadmore_hint.setText("pull up to load more");
                break;

            case UP_RELEASABLE:
                iv_loadmore_arrow.startAnimation(arrowAnimation);
                tv_loadmore_hint.setText("release to load more");
                break;

            case UP_RELEASE:
                iv_loadmore_arrow.clearAnimation();
                iv_loadmore_arrow.setVisibility(INVISIBLE);
                iv_loadmore_loading.setVisibility(VISIBLE);
                iv_loadmore_loading.startAnimation(loadingAnimation);
                tv_loadmore_hint.setText("loading more");
                if(mOnPullListener != null)
                    mOnPullListener.onLoadmore();
                break;
        }
    }

    public interface OnPullListener{
        void onRefresh();

        void onLoadmore();
    }
    private OnPullListener mOnPullListener;
    public void setOnPullListener(OnPullListener listener)
    {
        mOnPullListener = listener;
    }

    private class CTimer{
        private Timer mTimer;
        private Task mTask;
        private Handler handler;
        public CTimer(Handler handler)
        {
            this.handler = handler;
            mTimer = new Timer();
        }

        public void cancel()
        {
            if(mTask != null)
            {
                mTask.cancel();
                mTask = null;
            }
        }

        public void schedule(long period)
        {
            cancel();
            mTask = new Task(handler);
            mTimer.schedule(mTask, 0, period);
        }

        private class Task extends TimerTask{
            private Handler handler;

            public Task(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }
    }
}
