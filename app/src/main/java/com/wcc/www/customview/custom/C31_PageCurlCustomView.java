package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.wcc.www.customview.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.DITHER_FLAG;
import static android.graphics.Paint.LINEAR_TEXT_FLAG;

/**
 * Created by 王晨晨 on 2017/9/19.
 */
public class C31_PageCurlCustomView extends View{

    public C31_PageCurlCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(ANTI_ALIAS_FLAG | DITHER_FLAG | LINEAR_TEXT_FLAG);
    }

    private List<Bitmap> mBitmaps;
    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("onDraw "+mClipX);
        if(null == mBitmaps || mBitmaps.size() == 0)
        {
            defaultDraw(canvas);
            return;
        }
        drawBitmaps(canvas);
    }

    private float mClipX;
    private int pageIndex;
    private boolean isLastPage;
    private void drawBitmaps(Canvas canvas) {
        int size = mBitmaps.size();
        isLastPage = false;
        pageIndex = pageIndex < 0 ? 0 : pageIndex;
        pageIndex = pageIndex > size ? size : pageIndex;

        int start = size - pageIndex - 2;
        int end = size - pageIndex;

        if(start < 0)
        {
            isLastPage = true;
            start = 0;
            end = 1;
            show("The Last Page.");
        }

        for(int i = start; i < end; i ++)
        {
            canvas.save();
            if(!isLastPage && i == end - 1)
                canvas.clipRect(0, 0, mClipX, height);
            canvas.drawBitmap(mBitmaps.get(i), 0, 0, null);
            canvas.restore();
        }
    }

    private Toast toast;
    private void show(String msg)
    {
        if(toast == null)
            toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    private boolean isFirstPage;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null == mBitmaps || mBitmaps.size() == 0)
            return true;
        float mCurrentPositionX = 0;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mCurrentPositionX = event.getX();
                if(mCurrentPositionX < autoAreaLeft)
                {
                    if(pageIndex == 0)
                    {
                        show("The First Page.");
                        isFirstPage = true;
                        return true;
                    }
                    mClipX = mCurrentPositionX;
                    pageIndex --;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(isFirstPage)
                    return true;
                float dis = event.getX() - mCurrentPositionX;
                if(Math.abs(dis) > mMoveValid)
                {
                    mClipX = event.getX();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(isFirstPage)
                {
                    isFirstPage = false;
                    return true;
                }
                judgeAutoSlide();

                if(!isLastPage && mClipX <= 0)
                {
                    pageIndex ++;
                    mClipX = width;
                    invalidate();
                }
                break;
        }
        return true;
    }

    private void judgeAutoSlide() {
        if (mClipX <= autoAreaLeft) {
            if(mClipX > 0)
            {
//                mClipX --;
                mClipX = 0;
                invalidate();
            }
        } else if(mClipX > autoAreaRight){
            if(mClipX < width)
            {
//                mClipX ++;
                mClipX = width;
                invalidate();
            }
        }
    }

    private void initBitmaps()
    {
        if(null == mBitmaps || mBitmaps.size() == 0)
            return;
        List<Bitmap> bitmaps = new ArrayList<>();
        Collections.reverse(mBitmaps);
        for(Bitmap bitmap : mBitmaps)
        {
            Bitmap b = Bitmap.createScaledBitmap(bitmap, width, height, true);
            bitmaps.add(b);
        }
        mBitmaps = bitmaps;
    }

    private int width, height, centerX, centerY;
    private Paint mPaint;

    private int autoAreaLeft, autoAreaRight;
    private float mMoveValid;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w; height = h; centerX = w / 2; centerY = h / 2;
        mBitmaps = new ArrayList<>();
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_a));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_b));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_c));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_d));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_e));
        initBitmaps();
        mClipX = width;
        autoAreaLeft = autoAreaRight = centerX;
        mMoveValid = w * 1.0f / 100f;
    }

    private void defaultDraw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);

        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(sp2px(28));
        canvas.drawText("NO BITMAP", centerX, height / 3, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(sp2px(18));
        canvas.drawText("please set bitmaps data via setBitmaps method.", centerX, centerY, mPaint);

    }

    private int sp2px(int sp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}