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
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wcc.www.customview.R;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 王晨晨 on 2017/10/20.
 */

public class C77_2RefreshLayoutExercise10 extends RelativeLayout {

    private Timer mTimer;
    private Task mTask;
    private View refreshView;
    private View contentView;

    public C77_2RefreshLayoutExercise10(Context context, AttributeSet attrs) {
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
        postDelayed(new Runnable() {
            @Override
            public void run() {
                currentState = DOWN;
                hideHead();
            }
        }, 500);
    }

    private float mLasy;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLasy = y;
                if(mTask != null)
                    mTask.cancel();
                if(y < moveDeltaY)
                    return true;
                break;

            case MotionEvent.ACTION_MOVE:
                if(canPullDown())
                {
                    moveDeltaY += (y - mLasy) / ratio;
                    if(moveDeltaY < 0)
                        moveDeltaY = 0;
                    if(moveDeltaY > getMeasuredHeight())
                        moveDeltaY = getMeasuredHeight();
                    if(currentState == DOWN_RELEASE)
                        whenRefreshDetermineToScroll = true;
                }
                mLasy = y;
                ratio = (float) (3 + 3 * Math.tan(Math.PI / 2 / getMeasuredHeight() * moveDeltaY));
                requestLayout();
                if(moveDeltaY <= distanceOverToScroll && currentState == DOWN_RELEASABLE)
                    changeState(DOWN);
                if(moveDeltaY >= distanceOverToScroll && currentState == DOWN)
                    changeState(DOWN_RELEASABLE);
                if(moveDeltaY > 8)
                    clearContentViewEvents();
                if(moveDeltaY > 0)
                    return true;
                break;

            case MotionEvent.ACTION_UP:
                if(moveDeltaY > distanceOverToScroll)
                    whenRefreshDetermineToScroll = false;
                if(currentState == DOWN_RELEASABLE)
                    changeState(DOWN_RELEASE);
                if(canPullDown())
                    hideHead();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void clearContentViewEvents()
    {
        try{
            Field[] fs = AbsListView.class.getDeclaredFields();
            for(int i = 0; i < fs.length; i ++)
            {
                if(fs[i].getName().equals("mPendingCheckForLongPress"))
                {
                    fs[i].setAccessible(true);
                    contentView.getHandler().removeCallbacks((Runnable) fs[i].get(contentView));
                }else if(fs[i].getName().equals("mTouchMode"))
                {
                    fs[i].setAccessible(true);
                    fs[i].set(contentView, -1);
                }
            }
            ((AbsListView) contentView).getSelector().setState(new int[]{0});
        }catch (Exception e){}
    }

    private boolean isAlreadyLayout;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!isAlreadyLayout)
        {
            isAlreadyLayout = true;
            refreshView = getChildAt(0);
            contentView = getChildAt(1);
            initRefreshView();
            distanceOverToScroll = ((ViewGroup) refreshView).getChildAt(0).getMeasuredHeight();
        }
        if(canPullDown())
        {
            refreshView.layout(0, (int) moveDeltaY - refreshView.getMeasuredHeight(), refreshView.getMeasuredWidth(), (int) moveDeltaY);
            contentView.layout(0, (int) moveDeltaY, contentView.getMeasuredWidth(), contentView.getMeasuredHeight() + (int)  moveDeltaY);
        }else
            super.onLayout(changed, l, t, r, b);
    }

    private void initRefreshView()
    {
        iv_refresh_arrow = (ImageView) refreshView.findViewById(R.id.iv_refresh_arrow);
        iv_refresh_loading = (ImageView) refreshView.findViewById(R.id.iv_refresh_loading);
        iv_refresh_hint = (ImageView) refreshView.findViewById(R.id.iv_refresh_hint);
        tv_refresh_hint = (TextView) refreshView.findViewById(R.id.tv_refresh_hint);
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

    private float moveDeltaY, ratio = 3, moveSpeed = 8;
    private int currentState;
    public static final int DOWN = 0;
    public static final int DOWN_RELEASABLE = 1;
    public static final int DOWN_RELEASE = 2;
    private boolean whenRefreshDetermineToScroll;
    private int distanceOverToScroll;
    private Handler moveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            moveDeltaY -= moveSpeed;
            moveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * moveDeltaY));
            if(currentState == DOWN_RELEASE && moveDeltaY <= distanceOverToScroll && !whenRefreshDetermineToScroll)
            {
                moveDeltaY = distanceOverToScroll;
                mTask.cancel();
            }
            if(moveDeltaY <= 0)
            {
                moveDeltaY = 0;
                if(currentState != DOWN_RELEASE)
                    changeState(DOWN);
                mTask.cancel();
            }
            requestLayout();
        }
    };

    private ImageView iv_refresh_arrow;
    private ImageView iv_refresh_loading;
    private ImageView iv_refresh_hint;
    private TextView tv_refresh_hint;
    private Animation arrowAnimation, loadingAnimation;
    private void changeState(int state)
    {
        currentState = state;
        switch (state)
        {
            case DOWN:
                iv_refresh_arrow.clearAnimation();
                iv_refresh_arrow.setVisibility(VISIBLE);
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
                if(mOnRefreshListener != null)
                    mOnRefreshListener.onRefresh();
                break;
        }
    }

    public interface OnRefreshListener{
        void onRefresh();
    }
    private OnRefreshListener mOnRefreshListener;
    public void setOnRefreshListener(OnRefreshListener listener)
    {
        mOnRefreshListener = listener;
    }

    private boolean canPullDown()
    {
        return !ViewCompat.canScrollVertically(contentView, -1);
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
