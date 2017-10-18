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
 * Created by 王晨晨 on 2017/10/17.
 */

public class C61_Strengthen2RefreshLayoutExercise2 extends RelativeLayout {

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

    public C61_Strengthen2RefreshLayoutExercise2(Context context, AttributeSet attrs) {
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
        tv_refresh_hint.setText("刷新成功");
        new Handler(){
            @Override
            public void handleMessage(Message msg) {
                currentState = PULL_TO_REFRESH;
                hide();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    public void loadmoreFinish()
    {
        iv_loadmore_loading.clearAnimation();
        iv_loadmore_loading.setVisibility(INVISIBLE);
        iv_loadmore_hint.setVisibility(VISIBLE);
        iv_loadmore_hint.setImageResource(R.drawable.refresh_succeed);
        tv_loadmore_hint.setText("加载更多成功");
        new Handler(){
            @Override
            public void handleMessage(Message msg) {
                currentState = PULL_TO_LOADMORE;
                hide();
            }
        }.sendEmptyMessageDelayed(0, 1000);
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
                mTimer.cancel();
                releasePull();
                break;

            case MotionEvent.ACTION_MOVE:
                if(pullDownY > 0 || (canPullDown && canPullDown() && currentState != LOADMORE))
                {
                    pullDownY += (y - mLastY) / ratio;
                    if(pullDownY < 0) {
                        pullDownY = 0;
                        canPullDown = false;
                        canPullUp = true;
                    }
                    if(pullDownY > getMeasuredHeight())
                        pullDownY = getMeasuredHeight();
                    if(currentState == REFRESH)
                        whenRefreshOrLoadmoreDetermineToScroll = true;
                }else if(pullUpY < 0 || (canPullUp && canPullUp() && currentState != REFRESH))
                {
                    pullUpY += (y - mLastY) / ratio;
                    if(pullUpY > 0) {
                        pullUpY = 0;
                        canPullDown = true;
                        canPullUp = false;
                    }
                    if(pullUpY < -getMeasuredHeight())
                        pullUpY = -getMeasuredHeight();
                    if(currentState == LOADMORE)
                        whenRefreshOrLoadmoreDetermineToScroll = true;
                }
                ratio = (float) (3 + 3 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY - pullUpY)));
                mLastY = y;
                requestLayout();
                if(pullUpY < 0)
                {
                    if(-pullUpY <= distanceOverToScroll && currentState == RELEASE_TO_LOADMORE)
                        changeState(PULL_TO_LOADMORE);
                    if(-pullUpY >= distanceOverToScroll && currentState != RELEASE_TO_LOADMORE)
                        changeState(RELEASE_TO_LOADMORE);
                }else if(pullDownY > 0)
                {
                    if(pullDownY <= distanceOverToScroll && currentState == RELEASE_TO_REFRESH)
                        changeState(PULL_TO_REFRESH);
                    if(pullDownY >= distanceOverToScroll && currentState != RELEASE_TO_REFRESH)
                        changeState(RELEASE_TO_REFRESH);
                }
                if(pullDownY - pullUpY > 8)
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                break;

            case MotionEvent.ACTION_UP:
                System.out.println("up pullDownY "+pullDownY+" pullUpY "+pullUpY);
                if(pullDownY > 0 || pullUpY < 0)
                    whenRefreshOrLoadmoreDetermineToScroll = false;
                System.out.println("currentState "+currentState);
                if(currentState == RELEASE_TO_REFRESH)
                    changeState(REFRESH);
                else if(currentState == RELEASE_TO_LOADMORE)
                    changeState(LOADMORE);
                hide();
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    private void hide()
    {
        mTimer.schedule(5);
    }

    private boolean canPullDown(){
        return !ViewCompat.canScrollVertically(contentView, -1);
    }

    private boolean canPullUp(){
        return !ViewCompat.canScrollVertically(contentView, 1);
    }

    private void releasePull()
    {
        canPullDown = true;
        canPullUp = true;
    }

    private boolean isAreadyLayout;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!isAreadyLayout)
        {
            isAreadyLayout = true;
            refreshView = getChildAt(0);
            contentView = getChildAt(1);
            loadmoreView = getChildAt(2);
            distanceOverToScroll = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight();
            initRefreshAndLoadmoreView();
        }
        refreshView.layout(0, (int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(), refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
        contentView.layout(0, (int) (pullDownY + pullUpY), contentView.getMeasuredWidth(), (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight());
        loadmoreView.layout(0, (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight(), loadmoreView.getMeasuredWidth(), (int) (pullDownY + pullUpY) + contentView.getMeasuredHeight() + loadmoreView.getMeasuredHeight());
    }

    private void initRefreshAndLoadmoreView()
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
    private int currentState;
    private boolean whenRefreshOrLoadmoreDetermineToScroll;
    private int distanceOverToScroll;
    public static final int PULL_TO_REFRESH = 0;
    public static final int RELEASE_TO_REFRESH = 1;
    public static final int REFRESH = 3;
    public static final int PULL_TO_LOADMORE = 4;
    public static final int RELEASE_TO_LOADMORE = 5;
    public static final int LOADMORE = 6;
    private Handler moveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(pullDownY > 0)
                pullDownY -= moveSpeed;
            else if(pullUpY < 0)
                pullUpY += moveSpeed;
            moveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY - pullUpY)));
            if (!whenRefreshOrLoadmoreDetermineToScroll) {
                if(currentState == REFRESH && pullDownY <= distanceOverToScroll)
                {
                    pullDownY = distanceOverToScroll;
                    mTimer.cancel();
                }else if(currentState == LOADMORE && -pullUpY <= distanceOverToScroll)
                {
                    pullUpY = -distanceOverToScroll;
                    mTimer.cancel();
                }
            }
            if(pullDownY < 0)
            {
                pullDownY = 0;
                if(currentState != REFRESH)
                    changeState(PULL_TO_REFRESH);
                mTimer.cancel();
            }
            if(pullUpY > 0)
            {
                pullUpY = 0;
                if(currentState != LOADMORE)
                    changeState(PULL_TO_LOADMORE);
                mTimer.cancel();
            }
            requestLayout();
        }
    };

    private void changeState(int state)
    {
        currentState = state;
        switch (state)
        {
            case PULL_TO_REFRESH:
                iv_refresh_hint.setVisibility(INVISIBLE);
                iv_refresh_arrow.setVisibility(VISIBLE);
                iv_refresh_arrow.clearAnimation();
                tv_refresh_hint.setText("下拉刷新");
                break;

            case RELEASE_TO_REFRESH:
                iv_refresh_arrow.startAnimation(arrowAnimation);
                tv_refresh_hint.setText("释放刷新");
                break;

            case REFRESH:
                iv_refresh_arrow.clearAnimation();
                iv_refresh_arrow.setVisibility(INVISIBLE);
                iv_refresh_loading.setVisibility(VISIBLE);
                iv_refresh_loading.startAnimation(loadingAnimation);
                tv_refresh_hint.setText("正在刷新");
                if(mOnPullListener != null)
                    mOnPullListener.onRefresh();
                break;

            case PULL_TO_LOADMORE:
                iv_loadmore_hint.setVisibility(INVISIBLE);
                iv_loadmore_arrow.clearAnimation();
                iv_loadmore_arrow.setVisibility(VISIBLE);
                tv_loadmore_hint.setText("上拉加载更多");
                break;

            case RELEASE_TO_LOADMORE:
                iv_loadmore_arrow.startAnimation(arrowAnimation);
                tv_loadmore_hint.setText("释放加载更多");
                break;

            case LOADMORE:
                iv_loadmore_arrow.clearAnimation();
                iv_loadmore_arrow.setVisibility(INVISIBLE);
                iv_loadmore_loading.setVisibility(VISIBLE);
                iv_loadmore_loading.startAnimation(loadingAnimation);
                tv_loadmore_hint.setText("正在加载更多");
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
        private Timer timer;
        private Handler handler;
        private Task mTask;

        public CTimer(Handler handler)
        {
            this.handler = handler;
            timer = new Timer();
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
            timer.schedule(mTask, 0, period);
        }

        private class Task extends TimerTask{
            private Handler handler;
            public Task(Handler handler)
            {
                this.handler = handler;
            }
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }
    }
}
