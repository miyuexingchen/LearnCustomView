package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Canvas;
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
 * Created by 王晨晨 on 2017/10/31.
 */

public class C82_Strengthen2RefreshLayoutExercise6 extends RelativeLayout {

    private CTimer mTimer;
    private View refreshView;
    private View contentView;
    private View loadmoreView;
    private ImageView iv_refresh_arrow;
    private ImageView iv_refresh_loading;
    private ImageView iv_refresh_hint;
    private TextView tv_refresh_hint;
    private ImageView iv_loadmore_arrow;
    private ImageView iv_loadmore_loading;
    private ImageView iv_loading_hint;
    private TextView tv_loadmore_hint;
    private final Animation arrowAnimation;
    private final Animation loadingAnimation;

    public C82_Strengthen2RefreshLayoutExercise6(Context context, AttributeSet attrs) {
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
        new Handler(){
            @Override
            public void handleMessage(Message msg) {
                currentState = DOWN;
                hide();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    public void loadmoreFinish()
    {
        iv_loadmore_loading.clearAnimation();
        iv_loadmore_loading.setVisibility(INVISIBLE);
        iv_loading_hint.setVisibility(VISIBLE);
        iv_loading_hint.setImageResource(R.drawable.refresh_succeed);
        tv_loadmore_hint.setText("loadmore succeed");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                currentState = UP;
                hide();
            }
        }, 500);
    }

    private float mLastY;
    private boolean canPullDown = true, canPullUp = true;
    private float ratio = 3;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                cancel();
                releasePull();
                break;

            case MotionEvent.ACTION_MOVE:
                if(pullDownY > 0 || canPullDown && !ViewCompat.canScrollVertically(contentView, -1) && currentState != UP_RELEASE)
                {
                    pullDownY += (y - mLastY) / ratio;
                    if(pullDownY < 0)
                    {
                        pullDownY = 0;
                        canPullUp = true;
                        canPullDown = false;
                    }
                    whenRefreshOrLoadmoreDetermineToScroll = true;
                }else if(pullUpY < 0 || canPullUp && !ViewCompat.canScrollVertically(contentView, 1) && currentState != DOWN_RELEASE)
                {
                    pullUpY += (y - mLastY) / ratio;
                    if(pullUpY > 0)
                    {
                        pullUpY = 0;
                        canPullDown = true;
                        canPullUp = false;
                    }
                    whenRefreshOrLoadmoreDetermineToScroll = true;
                }
                mLastY = y;
                ratio = (float) (3 + 3 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY - pullUpY)));
                requestLayout();
                if(pullDownY > 0)
                {
                    if(pullDownY > distanceOverToScroll && currentState != DOWN_RELEASABLE)
                        changeState(DOWN_RELEASABLE);
                    if(pullDownY < distanceOverToScroll && currentState == DOWN_RELEASABLE)
                        changeState(DOWN);
                }else if(pullUpY < 0)
                {
                    if(-pullUpY > distanceOverToScroll && currentState != UP_RELEASABLE)
                        changeState(UP_RELEASABLE);
                    if(-pullUpY < distanceOverToScroll && currentState == UP_RELEASABLE)
                        changeState(UP);
                }

                if(pullDownY - pullUpY > 8)
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                break;

            case MotionEvent.ACTION_UP:
                if(pullDownY > distanceOverToScroll || -pullUpY > distanceOverToScroll)
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

    private void changeState(int state)
    {
        currentState = state;
        switch (state)
        {
            case DOWN:
                iv_refresh_hint.setVisibility(INVISIBLE);
                iv_refresh_arrow.clearAnimation();
                iv_refresh_arrow.setVisibility(VISIBLE);
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
                iv_loading_hint.setVisibility(INVISIBLE);
                iv_loadmore_arrow.setVisibility(VISIBLE);
                iv_loadmore_arrow.clearAnimation();
                tv_loadmore_hint.setText("pull up to loadmore");
                break;

            case UP_RELEASABLE:
                iv_loadmore_arrow.startAnimation(arrowAnimation);
                tv_loadmore_hint.setText("release to loadmore");
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

    private void hide()
    {
        mTimer.schedule(5);
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
            distanceOverToScroll = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight();
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
        iv_loading_hint = (ImageView) loadmoreView.findViewById(R.id.iv_loadmore_hint);
        tv_loadmore_hint = (TextView) loadmoreView.findViewById(R.id.tv_loadmore_hint);
    }

    private float pullDownY, pullUpY;
    private boolean whenRefreshOrLoadmoreDetermineToScroll;
    private float moveSpeed = 8;
    private static final int DOWN = 0;
    private static final int DOWN_RELEASABLE = 1;
    private static final int DOWN_RELEASE = 2;
    private static final int UP = 3;
    private static final int UP_RELEASABLE = 4;
    private static final int UP_RELEASE = 5;
    private int currentState;
    private int distanceOverToScroll;
    private Handler moveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(pullDownY > 0)
                pullDownY -= moveSpeed;
            else if(pullUpY < 0)
                pullUpY += moveSpeed;
            moveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY - pullUpY)));
            if(!whenRefreshOrLoadmoreDetermineToScroll)
            {
                if(currentState == DOWN_RELEASE && pullDownY <= distanceOverToScroll)
                {
                    pullDownY = distanceOverToScroll;
                    cancel();
                }else if(currentState == UP_RELEASE && -pullUpY <= distanceOverToScroll)
                {
                    pullUpY = -distanceOverToScroll;
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

    private void cancel(){
        mTimer.cancel();
    }

    private class CTimer{
        private Handler handler;
        private Timer mTimer;
        private Task mTask;

        public CTimer(Handler handler) {
            mTimer = new Timer();
            this.handler = handler;
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
