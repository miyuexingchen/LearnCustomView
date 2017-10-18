package com.wcc.www.customview.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.TextView;

import com.wcc.www.customview.R;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 王晨晨 on 2017/10/17.
 */

public class C64_2RefreshLayoutExercise6 extends RelativeLayout implements View.OnTouchListener {

    private Timer mTimer;
    private final Animation arrowAnimation;
    private final Animation loadingAnimation;
    private View refreshView;
    private View contentView;
    private ImageView iv_refresh_arrow;
    private ImageView iv_refresh_loading;
    private ImageView iv_refresh_hint;
    private TextView tv_refresh_hint;

    public C64_2RefreshLayoutExercise6(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTimer = new Timer();
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
                currentState = PULL_TO_REFRESH;
                hideHead();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    private float mLastY;
    private Task mTask;
    private float ratio  = 3;
    private int currentState;
    public static final int PULL_TO_REFRESH = 0;
    public static final int RELEASE_TO_REFRESH = 1;
    public static final int REFRESH = 2;
    private boolean whenRefreshDetermineToScroll;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                if(mTask != null)
                    mTask.cancel();
                if(y < moveDeltaY)
                    return true;
                break;

            case MotionEvent.ACTION_MOVE:
                if(canPull)
                {
                    moveDeltaY += (y - mLastY) / ratio;
                    if(moveDeltaY < 0)
                        moveDeltaY = 0;
                    if(moveDeltaY > getMeasuredHeight())
                        moveDeltaY = getMeasuredHeight();
                    if(currentState == REFRESH)
                        whenRefreshDetermineToScroll = true;
                }
                mLastY = y;
                ratio = (float) (3 + 3 * Math.tan(Math.PI / 2 / getMeasuredHeight() * moveDeltaY));
                requestLayout();

                if(moveDeltaY <= distanceOverToRefresh && currentState == RELEASE_TO_REFRESH)
                    changeState(PULL_TO_REFRESH);
                if(moveDeltaY >= distanceOverToRefresh && currentState == PULL_TO_REFRESH)
                    changeState(RELEASE_TO_REFRESH);
                if(moveDeltaY > 8)
                    clearContentEvents();
                if(moveDeltaY > 0)
                    return true;
                break;

            case MotionEvent.ACTION_UP:
                if(moveDeltaY > distanceOverToRefresh)
                    whenRefreshDetermineToScroll = false;
                if(currentState == RELEASE_TO_REFRESH)
                    changeState(REFRESH);
                if(canPull)
                    hideHead();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideHead()
    {
        if(mTask != null)
        {
            mTask.cancel();
            mTask = null;
        }
        mTask = new Task(moveHandler);
        mTimer.schedule(mTask, 0, 5);
    }

    private float moveSpeed = 8;
    private Handler moveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            moveDeltaY -= moveSpeed;
            moveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * moveDeltaY));
            if(currentState == REFRESH && moveDeltaY <= distanceOverToRefresh && !whenRefreshDetermineToScroll)
            {
                moveDeltaY = distanceOverToRefresh;
                mTask.cancel();
            }
            if(moveDeltaY <= 0)
            {
                moveDeltaY = 0;
                if(currentState != REFRESH)
                    changeState(PULL_TO_REFRESH);
                mTask.cancel();
            }
            requestLayout();
        }
    };

    private void clearContentEvents()
    {
        try {
            Field[] fs = AbsListView.class.getDeclaredFields();
            for (int i = 0; i < fs.length; i++)
            {
                if(fs[i].getName().equals("mPendingCheckForLongPress"))
                {
                    System.out.println("llllllll LongClick");
                    fs[i].setAccessible(true);
                    contentView.getHandler().removeCallbacks((Runnable) fs[i].get(contentView));
                }else if(fs[i].getName().equals("mTouchMode"))
                {
                    System.out.println("ssssssss Click");
                    fs[i].setAccessible(true);
                    fs[i].set(contentView, -1);
                }
            }
            ((AbsListView) contentView).getSelector().setState(new int[]{0});
        }catch (Exception e){}
    }

    private OnRefreshListener mOnRefreshListener;
    private void changeState(int state)
    {
        currentState = state;
        switch (state)
        {
            case PULL_TO_REFRESH:
                iv_refresh_arrow.clearAnimation();
                iv_refresh_arrow.setVisibility(VISIBLE);
                iv_refresh_hint.setVisibility(INVISIBLE);
                tv_refresh_hint.setText("pull down to refresh");
                break;

            case RELEASE_TO_REFRESH:
                iv_refresh_arrow.startAnimation(arrowAnimation);
                tv_refresh_hint.setText("release to refresh");
                break;

            case REFRESH:
                iv_refresh_arrow.clearAnimation();
                iv_refresh_arrow.setVisibility(INVISIBLE);
                iv_refresh_loading.setVisibility(VISIBLE);
                iv_refresh_loading.startAnimation(loadingAnimation);
                tv_refresh_hint.setText("refreshing");
                if(mOnRefreshListener != null)
                    mOnRefreshListener.onRefresh();
                break;
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener)
    {
        mOnRefreshListener = listener;
    }
    public interface OnRefreshListener{
        void onRefresh();
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

    private boolean isAlreadyLayout;
    private float moveDeltaY;
    private int distanceOverToRefresh;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!isAlreadyLayout)
        {
            isAlreadyLayout = true;
            refreshView = getChildAt(0);
            contentView = getChildAt(1);
            contentView.setOnTouchListener(this);
            initRefreshView();
            distanceOverToRefresh = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight();
        }
        if(canPull)
        {
            refreshView.layout(0, (int) moveDeltaY - refreshView.getMeasuredHeight(), refreshView.getMeasuredWidth(), (int) moveDeltaY);
            contentView.layout(0, (int) moveDeltaY, contentView.getMeasuredWidth(), (int) moveDeltaY + contentView.getMeasuredHeight());
        }else
            super.onLayout(changed, l, t, r, b);
    }

    private void initRefreshView() {
        iv_refresh_arrow = (ImageView) refreshView.findViewById(R.id.iv_refresh_arrow);
        iv_refresh_loading = (ImageView) refreshView.findViewById(R.id.iv_refresh_loading);
        iv_refresh_hint = (ImageView) refreshView.findViewById(R.id.iv_refresh_hint);
        tv_refresh_hint = (TextView) refreshView.findViewById(R.id.tv_refresh_hint);
    }

    private boolean canPull = true;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AbsListView absListView;
        try{
            absListView = (AbsListView) contentView;
        }catch (Exception e)
        {
            return false;
        }
        if(absListView.getChildCount() == 0 || (absListView.getFirstVisiblePosition() == 0 && absListView.getChildAt(0).getTop() >= 0))
            canPull = true;
        else
            canPull = false;
        return false;
    }
}
