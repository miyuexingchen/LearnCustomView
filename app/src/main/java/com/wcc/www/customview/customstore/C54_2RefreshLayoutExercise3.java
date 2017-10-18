package com.wcc.www.customview.customstore;

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
 * Created by 王晨晨 on 2017/10/13.
 */

public class C54_2RefreshLayoutExercise3 extends RelativeLayout implements View.OnTouchListener {

    private final Animation arrowAnimation;
    private final Animation loadingAnimation;

    private Timer mTimer;
    private boolean isAlreadyLayout;
    private View header;
    private View contentView;
    private int distanceOverToRefresh;
    private boolean canPull = true;
    private float moveDeltaY;
    private float ratio = 2;
    private boolean whenRefreshingDetermineToHideHead;
    private ImageView iv_refresh_arrow;
    private ImageView iv_refresh_loading;
    private ImageView iv_refresh_hint;
    private TextView tv_refresh_hint;

    public C54_2RefreshLayoutExercise3(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTimer = new Timer();
        arrowAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_arrow);
        loadingAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_loading);
        LinearInterpolator li = new LinearInterpolator();
        arrowAnimation.setInterpolator(li);
        loadingAnimation.setInterpolator(li);
    }

    private float mLasyY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLasyY = y;
                if(mTask != null)
                    mTask.cancel();
                if(y < moveDeltaY)
                    return true;
                break;

            case MotionEvent.ACTION_MOVE:
                if(canPull)
                {
                    moveDeltaY += (y - mLasyY) / ratio;
                    if(moveDeltaY < 0)
                        moveDeltaY = 0;
                    if(moveDeltaY > getMeasuredHeight())
                        moveDeltaY = getMeasuredHeight();
                    if(currentState == REFRESHING)
                        whenRefreshingDetermineToHideHead = true;
                }
                requestLayout();
                mLasyY = y;
                ratio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * moveDeltaY));

                if(moveDeltaY <= distanceOverToRefresh && currentState == RELEASE_TO_REFRESH)
                    changeState(PULL_TO_REFRESH);
                if(moveDeltaY >= distanceOverToRefresh && currentState == PULL_TO_REFRESH)
                    changeState(RELEASE_TO_REFRESH);
                if(moveDeltaY > 8)
                    clearContentViewEvents();
                if(moveDeltaY > 0)
                    return true;
                break;

            case MotionEvent.ACTION_UP:
                if(moveDeltaY > distanceOverToRefresh)
                    whenRefreshingDetermineToHideHead = false;
                if(currentState == RELEASE_TO_REFRESH)
                    changeState(REFRESHING);
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
            if(currentState == REFRESHING && moveDeltaY <= distanceOverToRefresh && !whenRefreshingDetermineToHideHead)
            {
                moveDeltaY = distanceOverToRefresh;
                mTask.cancel();
            }
            if(moveDeltaY <= 0)
            {
                moveDeltaY = 0;
//                if(currentState != REFRESHING)
                    changeState(PULL_TO_REFRESH);
                mTask.cancel();
            }
            requestLayout();
        }
    };

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
                hideHead();
            }
        }.sendEmptyMessageDelayed(0, 1000);
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

    private void changeState(int state)
    {
        currentState = state;
        switch (state)
        {
            case PULL_TO_REFRESH:
                iv_refresh_arrow.setVisibility(VISIBLE);
                iv_refresh_arrow.clearAnimation();
                iv_refresh_hint.setVisibility(INVISIBLE);
                tv_refresh_hint.setText("下拉刷新");
                break;

            case RELEASE_TO_REFRESH:
                iv_refresh_arrow.startAnimation(arrowAnimation);
                tv_refresh_hint.setText("释放立即刷新");
                break;

            case REFRESHING:
                iv_refresh_arrow.clearAnimation();
                iv_refresh_arrow.setVisibility(INVISIBLE);
                iv_refresh_loading.setVisibility(VISIBLE);
                iv_refresh_loading.startAnimation(loadingAnimation);
                tv_refresh_hint.setText("正在刷新...");
                if(mOnRefreshListener != null)
                    mOnRefreshListener.onRefresh();
                break;
        }
    }

    private OnRefreshListener mOnRefreshListener;
    public void setOnRefreshListener(OnRefreshListener listener)
    {
        mOnRefreshListener = listener;
    }
    public interface OnRefreshListener{
        void onRefresh();
    }

    public static final int PULL_TO_REFRESH = 0;
    public static final int RELEASE_TO_REFRESH = 1;
    public static final int REFRESHING = 2;
    private int currentState;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!isAlreadyLayout)
        {
            isAlreadyLayout = true;
            header = getChildAt(0);
            iv_refresh_arrow = (ImageView) header.findViewById(R.id.iv_refresh_arrow);
            iv_refresh_loading = (ImageView) header.findViewById(R.id.iv_refresh_loading);
            iv_refresh_hint = (ImageView) header.findViewById(R.id.iv_refresh_hint);
            tv_refresh_hint = (TextView) header.findViewById(R.id.tv_refresh_hint);
            contentView = getChildAt(1);
            contentView.setOnTouchListener(this);
            distanceOverToRefresh = ((ViewGroup) header).getChildAt(0).getMeasuredHeight();
        }
        if(canPull)
        {
            header.layout(0, (int) moveDeltaY - header.getMeasuredHeight(), header.getMeasuredWidth(), (int) moveDeltaY);
            contentView.layout(0, (int) moveDeltaY, contentView.getMeasuredWidth(), (int) moveDeltaY + contentView.getMeasuredHeight());
        }else
            super.onLayout(changed, l, t, r, b);
    }
    private Task mTask;
    private class Task extends TimerTask{

        private Handler handler;
        public Task(Handler handler)
        {
            this.handler = handler;
        }
        @Override
        public void run() {
            handler.obtainMessage().sendToTarget();
        }
    }

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
