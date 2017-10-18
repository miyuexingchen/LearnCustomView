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
 * Created by 王晨晨 on 2017/10/12.
 */

public class C49_2RefreshLayoutExercise1 extends RelativeLayout implements View.OnTouchListener {


    private Animation arrowAnimation, loadingAnimation;
    private Timer mTimer;
    private View header;
    private View contentView;
    private ImageView iv_refresh_arrow;
    private ImageView iv_refresh_loading;
    private TextView tv_refresh_hint;
    private ImageView iv_refresh_hint;

    public C49_2RefreshLayoutExercise1(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public static final int REFRESH_SUCCESS = 0;
    public static final int REFRESH_FAILURE = 1;
    public void refreshFinish(int state)
    {
        iv_refresh_loading.clearAnimation();
        iv_refresh_loading.setVisibility(INVISIBLE);
        switch (state)
        {
            case REFRESH_SUCCESS:
                iv_refresh_hint.setVisibility(VISIBLE);
                iv_refresh_hint.setImageResource(R.drawable.refresh_succeed);
                tv_refresh_hint.setText("刷新成功");
                break;

            case REFRESH_FAILURE:
                iv_refresh_hint.setVisibility(VISIBLE);
                iv_refresh_hint.setImageResource(R.drawable.refresh_failed);
                tv_refresh_hint.setText("刷新失败");
                break;
        }
        new Handler(){
            @Override
            public void handleMessage(Message msg) {
                mState = PULL_TO_REFRESH;
                hideHead();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    private float mLastY;
    private Task mTask;
    private boolean whenRefreshingDetermineToHideHead = false;
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
                    if(mState == REFRESHING)
                        whenRefreshingDetermineToHideHead = true;
                }
                mLastY = y;
                requestLayout();
                ratio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * moveDeltaY));
                if(moveDeltaY <= distanceOverToRefresh && mState == RELEASE_TO_REFRESH)
                    changeState(PULL_TO_REFRESH);
                if(moveDeltaY >= distanceOverToRefresh && mState == PULL_TO_REFRESH)
                    changeState(RELEASE_TO_REFRESH);
                if(moveDeltaY > 8)
                    clearChildAbsListViewCallbacks();
                if(moveDeltaY > 0)
                    return true;
                break;

            case MotionEvent.ACTION_UP:
                if(moveDeltaY > distanceOverToRefresh)
                    whenRefreshingDetermineToHideHead = false;
                if(mState == RELEASE_TO_REFRESH)
                {
                    changeState(REFRESHING);
                    if(mOnRefreshListener != null)
                        mOnRefreshListener.onRefresh();
                }
                if(canPull)
                    hideHead();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }
    private float moveSpeed = 8;
    private Handler moveHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            moveDeltaY -= moveSpeed;
            moveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * moveDeltaY));
            if(mState == REFRESHING && moveDeltaY <= distanceOverToRefresh && !whenRefreshingDetermineToHideHead)
            {
                moveDeltaY = distanceOverToRefresh;
                mTask.cancel();
            }
            if(moveDeltaY <= 0)
            {
                moveDeltaY = 0;
                if(mState != REFRESHING)
                    changeState(PULL_TO_REFRESH);
                mTask.cancel();
            }
            requestLayout();
        }
    };
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

    private void clearChildAbsListViewCallbacks()
    {
        try {
            Field[] declaredFields = AbsListView.class.getDeclaredFields();
            for(int i = 0; i < declaredFields.length; i ++)
            {
                if(declaredFields[i].getName().equals("mPendingCheckForLongPress"))
                {
                    declaredFields[i].setAccessible(true);
                    contentView.getHandler().removeCallbacks((Runnable) declaredFields[i].get(contentView));
                }else if(declaredFields[i].getName().equals("mTouchMode"))
                {
                    declaredFields[i].setAccessible(true);
                    declaredFields[i].set(contentView, -1);
                }
            }
            ((AbsListView) contentView).getSelector().setState(new int[]{0});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void changeState(int state)
    {
        mState = state;
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
                tv_refresh_hint.setText("释放刷新");
                break;

            case REFRESHING:
                iv_refresh_arrow.clearAnimation();
                iv_refresh_arrow.setVisibility(INVISIBLE);
                iv_refresh_loading.setVisibility(VISIBLE);
                iv_refresh_loading.startAnimation(loadingAnimation);
                tv_refresh_hint.setText("正在刷新...");
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

    private float ratio = 2;

    private int mState;
    private static final int PULL_TO_REFRESH = 0;
    private static final int RELEASE_TO_REFRESH = 1;
    private static final int REFRESHING = 2;
    private class Task extends TimerTask{

        Handler handler;
        public Task(Handler handler)
        {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }
    }

    private void initView(Context ctx)
    {
        mTimer = new Timer();
        arrowAnimation = AnimationUtils.loadAnimation(ctx, R.anim.anim_arrow);
        loadingAnimation = AnimationUtils.loadAnimation(ctx, R.anim.anim_loading);
        LinearInterpolator li = new LinearInterpolator();
        arrowAnimation.setInterpolator(li);
        loadingAnimation.setInterpolator(li);
    }

    private boolean isAlreadyLayout = false;
    private int distanceOverToRefresh;
    private float moveDeltaY = 0;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!isAlreadyLayout)
        {
            isAlreadyLayout = true;
            header = getChildAt(0);
            contentView = getChildAt(1);
            contentView.setOnTouchListener(this);
            initHeaderView();
            distanceOverToRefresh = ((ViewGroup) header).getChildAt(0).getMeasuredHeight();
        }
        if(canPull)
        {
            header.layout(0, (int) moveDeltaY - header.getMeasuredHeight(), header.getMeasuredWidth(), (int) moveDeltaY);
            contentView.layout(0, (int) moveDeltaY, contentView.getMeasuredWidth(), (int) moveDeltaY + contentView.getMeasuredHeight());
        }else
            super.onLayout(changed, l, t, r, b);
    }

    private void initHeaderView()
    {
        iv_refresh_arrow = (ImageView) header.findViewById(R.id.iv_refresh_arrow);
        iv_refresh_loading = (ImageView) header.findViewById(R.id.iv_refresh_loading);
        tv_refresh_hint = (TextView) header.findViewById(R.id.tv_refresh_hint);
        iv_refresh_hint = (ImageView) header.findViewById(R.id.iv_refresh_hint);
    }

    private boolean canPull = true;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AbsListView absListView;
        try {
            absListView = (AbsListView) contentView;
        }catch (Exception e){
            return false;
        }
        if(absListView.getCount() == 0 || (absListView.getFirstVisiblePosition() == 0 && absListView.getChildAt(0).getTop() >= 0))
            canPull = true;
        else
            canPull = false;
        return false;
    }
}
