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
 * Created by 王晨晨 on 2017/10/20.
 */

public class C78_Strenthen2RefreshLayoutExercise5 extends RelativeLayout {

    private CTimer mTimer;
    private final Animation loadingAnimation;
    private final Animation arrowAnimation;
    private View refreshView;
    private View contentView;
    private View loadmoreView;
    private int distanceOverToLoad;
    private ImageView iv_refresh_arrow;
    private ImageView iv_refresh_loading;
    private ImageView iv_refresh_hint;
    private TextView tv_refresh_hint;
    private ImageView iv_loadmore_arrow;
    private ImageView iv_loadmore_loading;
    private ImageView iv_loadmore_hint;
    private TextView tv_loadmore_hint;

    public C78_Strenthen2RefreshLayoutExercise5(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTimer = new CTimer(moveHandler);
        loadingAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_loading);
        arrowAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_arrow);
        LinearInterpolator li = new LinearInterpolator();
        loadingAnimation.setInterpolator(li);
        arrowAnimation.setInterpolator(li);
    }

    public void refreshFinish()
    {
        iv_refresh_loading.clearAnimation();
        iv_refresh_loading.setVisibility(INVISIBLE);
        iv_refresh_hint.setVisibility(VISIBLE);
        iv_refresh_hint.setImageResource(R.drawable.refresh_succeed);
        tv_refresh_hint.setText("刷新成功");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                currentState = PULL_TO_REFRESH;
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
        tv_loadmore_hint.setText("加载更多成功");
        postDelayed(new Runnable() {
            @Override
            public void run() {
                currentState = PULL_TO_LOADMORE;
                hide();
            }
        }, 500);
    }

    private float mLastY;
    private boolean canPullDown = true, canPullUp = true, whenRefreshOrLoadmoreDetermineToScroll;
    private int currentState;
    public static final int PULL_TO_REFRESH = 1;
    public static final int RELEASE_TO_REFRESH = 2;
    public static final int REFRESH = 76;
    public static final int PULL_TO_LOADMORE = 88;
    public static final int RELEASE_TO_LOADMORE = 99;
    public static final int LOADMORE = 100;
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
                if(pullDownY > 0 || (canPullDown && !ViewCompat.canScrollVertically(contentView, -1) && currentState != LOADMORE))
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
                }else if(pullUpY < 0 || (canPullUp && !ViewCompat.canScrollVertically(contentView, 1) && currentState != REFRESH))
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
                mLastY = y;
                ratio = (float) (3 + 3 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY - pullUpY)));
                requestLayout();
                if(pullDownY > 0)
                {
                    if(pullDownY <= distanceOverToLoad && currentState == RELEASE_TO_REFRESH)
                        changeState(PULL_TO_REFRESH);
                    if(pullDownY >= distanceOverToLoad && currentState != RELEASE_TO_REFRESH)
                        changeState(RELEASE_TO_REFRESH);
                }else if(pullUpY < 0)
                {
                    if(-pullUpY <= distanceOverToLoad && currentState == RELEASE_TO_LOADMORE)
                        changeState(PULL_TO_LOADMORE);
                    if(-pullUpY >= distanceOverToLoad && currentState != RELEASE_TO_LOADMORE)
                        changeState(RELEASE_TO_LOADMORE);
                }
                if(pullDownY - pullUpY > 8)
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                break;

            case MotionEvent.ACTION_UP:
                if(pullDownY > distanceOverToLoad || -pullUpY > distanceOverToLoad)
                    whenRefreshOrLoadmoreDetermineToScroll = false;
                if(currentState == RELEASE_TO_LOADMORE)
                    changeState(LOADMORE);
                else if(currentState == RELEASE_TO_REFRESH)
                    changeState(REFRESH);
                hide();
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    private void changeState(int state)
    {
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
                iv_loadmore_arrow.clearAnimation();
                iv_loadmore_arrow.setVisibility(VISIBLE);
                iv_loadmore_hint.setVisibility(INVISIBLE);
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

        currentState = state;
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

    public void cancel()
    {
        mTimer.cancel();
    }

    public void hide()
    {
        mTimer.schedule(5);
    }

    private void releasePull()
    {
        canPullDown = true;
        canPullUp = true;
    }

    private float pullDownY, pullUpY;
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

    private void initRefreshViewAndLoadmoreView() {
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
    private Handler moveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(pullDownY > 0) pullDownY -= moveSpeed;
            else if(pullUpY < 0) pullUpY += moveSpeed;
            moveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (pullDownY - pullUpY)));
            if(!whenRefreshOrLoadmoreDetermineToScroll)
            {
                if(currentState == REFRESH && pullDownY <= distanceOverToLoad)
                {
                    pullDownY = distanceOverToLoad;
                    cancel();
                }else if(currentState == LOADMORE && -pullUpY <= distanceOverToLoad)
                {
                    pullUpY = -distanceOverToLoad;
                    cancel();
                }
            }
            if(pullDownY < 0)
            {
                pullDownY = 0;
                if(currentState != REFRESH)
                    changeState(PULL_TO_REFRESH);
                cancel();
            }else if(pullUpY > 0)
            {
                pullUpY = 0;
                if(currentState != LOADMORE)
                    changeState(PULL_TO_LOADMORE);
                cancel();
            }
            requestLayout();
        }
    };

    private class CTimer{
        private Handler handler;
        private Timer mTimer;
        private Task mTask;

        public CTimer(Handler handler) {
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
