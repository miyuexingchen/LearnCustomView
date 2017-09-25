package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
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

/**
 * Created by 王晨晨 on 2017/9/21.
 */

public class C33_FoldView extends View {

    private Paint mPaint, mPathPaint;
    private SlideHandler mSlideHandler;
    public C33_FoldView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
        mSlideHandler = new SlideHandler();
        mFoldAndNextPath = new Path();
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setColor(Color.BLACK);
        mPathPaint.setStrokeWidth(10);
        mSemiCirclePath = new Path();
        mBottomSemiCirclePath = new Path();
        mRightSemiCirclePath = new Path();
        mAddPath = new Path();
    }

    private List<Bitmap> mBitmaps;
    private int sp2px(int sp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
    private void defaultDraw(Canvas canvas)
    {
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(sp2px(28));
        canvas.drawText("No Bitmap", centerX, height / 3, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(sp2px(18));
        canvas.drawText("please set bitmaps via setBitmaps method.", centerX, centerY, mPaint);
    }

    private float pointX, pointY;
    private Path mPath, mFoldAndNextPath, mSemiCirclePath, mBottomSemiCirclePath, mRightSemiCirclePath, mAddPath;
    private enum Ratio{
        SHORT, LONG
    }
    private Ratio mRatio;
    private float mDegrees;
    @Override
    protected void onDraw(Canvas canvas) {
        if(null == mBitmaps || mBitmaps.size() == 0)
        {
            defaultDraw(canvas);
            return;
        }
        mPath.reset();
        mFoldAndNextPath.reset();
        mSemiCirclePath.reset();
        mBottomSemiCirclePath.reset();
        mRightSemiCirclePath.reset();
        mAddPath.reset();

        if(pointX == 0 && pointY == 0) {
            canvas.drawBitmap(mBitmaps.get(mBitmaps.size() - 1), 0, 0, null);
            return;
        }

        if(!mPointYRegion.contains((int) pointX, (int) pointY))
            pointY = (float) (height - Math.sqrt(Math.pow(width, 2) - Math.pow(pointX, 2)) + height / 500f);

        if(pointY > height)
            pointY = height - 0.0009f;

        float s = width - pointX;
        float l = height - pointY;

        float powSum = (float) (Math.pow(s, 2) + Math.pow(l, 2));
        float sideShort = powSum / (2f * s);
        float sideLong = powSum / (2f * l);

        if(sideShort < sideLong)
        {
            mRatio = Ratio.SHORT;
            float sinValue = (width - sideShort - pointX) / sideShort;
            mDegrees = (float) (Math.asin(sinValue) / Math.PI * 180);
        }else
        {
            mRatio = Ratio.LONG;
            float cosValue = (width - pointX) / sideLong;
            mDegrees = (float) (Math.acos(cosValue) / Math.PI * 180);
        }

//        mPath.moveTo(pointX, pointY);
//        mFoldAndNextPath.moveTo(pointX, pointY);
        float rate = 1f / 4f;
        if(sideLong > height)
        {
            float x = (sideLong - height) * sideShort / sideLong;
            float y = (sideLong - height) * (width - pointX) / (sideLong - height + pointY);
//            mPath.lineTo(width - y, 0);
//            mPath.lineTo(width - x, 0);
//            mPath.lineTo(width - sideShort, height);

            float deltaBottom = width - sideShort;
            float bezierBottomStartX = deltaBottom - rate * sideShort;
            float bezierBottomStartY = height;

            float bezierBottomEndX = pointX + (1 - rate) * (deltaBottom - pointX);
            float bezierBottomEndY = pointY + (1 - rate) * l;

            float bezierBottomCtrlX = deltaBottom;
            float bezierBottomCtrlY = height;

            float bezierBottomPeakX = 0.25f * bezierBottomStartX + 0.5f * bezierBottomCtrlX + 0.25f * bezierBottomEndX;
            float bezierBottomPeakY = 0.25f * bezierBottomStartY + 0.5f * bezierBottomCtrlY + 0.25f * bezierBottomEndY;

            mPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mPath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mPath.lineTo(pointX, pointY);
            mPath.lineTo(width - y, 0);
            mPath.lineTo(width - x, 0);

            mFoldAndNextPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mFoldAndNextPath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mFoldAndNextPath.lineTo(pointX, pointY);
            mFoldAndNextPath.lineTo(width - y, 0);
            mFoldAndNextPath.lineTo(width, 0);
            mFoldAndNextPath.lineTo(width, height);

            mSemiCirclePath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mSemiCirclePath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mSemiCirclePath.close();

            mAddPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mAddPath.lineTo(width - x, 0);
            mAddPath.lineTo(bezierBottomPeakX, bezierBottomPeakY);
            mAddPath.close();

//            mFoldAndNextPath.lineTo(width - y, 0);
//            mFoldAndNextPath.lineTo(width, 0);
//            mFoldAndNextPath.lineTo(width, height);
//            mFoldAndNextPath.lineTo(width - sideShort, height);
        }else {
//            mPath.lineTo(width, height - sideLong);
//            mPath.lineTo(width - sideShort, height);
            float deltaBottom = width - sideShort;
            float bezierBottomStartX = deltaBottom - rate * sideShort;
            float bezierBottomStartY = height;

            float bezierBottomEndX = pointX + (1 - rate) * (deltaBottom - pointX);
            float bezierBottomEndY = pointY + (1 - rate) * l;

            float bezierBottomCtrlX = deltaBottom;
            float bezierBottomCtrlY = height;

            float bezierBottomPeakX = 0.25f * bezierBottomStartX + 0.5f * bezierBottomCtrlX + 0.25f * bezierBottomEndX;
            float bezierBottomPeakY = 0.25f * bezierBottomStartY + 0.5f * bezierBottomCtrlY + 0.25f * bezierBottomEndY;

            float deltaRight = height - sideLong;
            float bezierRightStartX = width;
            float bezierRightStartY = deltaRight - rate * sideLong;

            float bezierRightEndX = pointX + (1 - rate) * s;
            float bezierRightEndY = pointY - (1 - rate) * (sideLong - l);

            float bezierRightCtrlX = width;
            float bezierRightCtrlY = deltaRight;

            float bezierRightPeakX = 0.25f * bezierRightStartX + 0.5f * bezierRightCtrlX + 0.25f * bezierRightEndX;
            float bezierRightPeakY = 0.25f * bezierRightStartY + 0.5f * bezierRightCtrlY + 0.25f * bezierRightEndY;

    /*        *//*
     * 限制右侧曲线起点
     *//*
            if (bezierRightStartX <= 0) {
                bezierRightStartX = 0;
            }

    *//*
     * 限制底部左侧曲线起点
     *//*
            if (bezierBottomStartX <= 0) {
                bezierBottomStartX = 0;
            }

    *//*
     * 根据底部左侧限制点重新计算贝塞尔曲线顶点坐标
     *//*
            float partOfShortLength = rate * sideShort;
            float mValueAdded = height / 500f;
            if (deltaBottom >= -mValueAdded && deltaBottom <= partOfShortLength - mValueAdded) {
                float f = deltaBottom / partOfShortLength;
                float t = 0.5F * f;

                float bezierPeakTemp = 1 - t;
                float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
                float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
                float bezierPeakTemp3 = t * t;

                bezierBottomPeakX = bezierPeakTemp1 * bezierBottomStartX + bezierPeakTemp2 * bezierBottomCtrlX + bezierPeakTemp3 * bezierBottomEndX;
                bezierBottomPeakY = bezierPeakTemp1 * bezierBottomPeakY + bezierPeakTemp2 * bezierBottomCtrlY + bezierPeakTemp3 * bezierBottomEndY;
            }

    *//*
     * 根据右侧限制点重新计算贝塞尔曲线顶点坐标
     *//*
            float partOfLongLength = rate * sideLong;
            if (deltaRight >= -mValueAdded && deltaRight <= partOfLongLength - mValueAdded) {
                float f = deltaRight / partOfLongLength;
                float t = 0.5F * f;

                float bezierPeakTemp = 1 - t;
                float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
                float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
                float bezierPeakTemp3 = t * t;

                bezierRightPeakX = bezierPeakTemp1 * bezierRightStartX + bezierPeakTemp2 * bezierRightCtrlX + bezierPeakTemp3 * bezierRightEndX;
                bezierRightPeakY = bezierPeakTemp1 * bezierRightStartY + bezierPeakTemp2 * bezierRightCtrlY + bezierPeakTemp3 * bezierRightEndY;
            }*/

            mPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mPath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mPath.lineTo(pointX, pointY);
            mPath.lineTo(bezierRightEndX, bezierRightEndY);
            mPath.quadTo(bezierRightCtrlX, bezierRightCtrlY, bezierRightStartX, bezierRightStartY);

            mFoldAndNextPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mFoldAndNextPath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mFoldAndNextPath.lineTo(pointX, pointY);
            mFoldAndNextPath.lineTo(bezierRightEndX, bezierRightEndY);
            mFoldAndNextPath.quadTo(bezierRightCtrlX, bezierRightCtrlY, bezierRightStartX, bezierRightStartY);
            mFoldAndNextPath.lineTo(width, height);

            mBottomSemiCirclePath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mBottomSemiCirclePath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mBottomSemiCirclePath.close();

            mRightSemiCirclePath.moveTo(bezierRightStartX, bezierRightStartY);
            mRightSemiCirclePath.quadTo(bezierRightCtrlX, bezierRightCtrlY, bezierRightEndX, bezierRightEndY);
            mRightSemiCirclePath.close();

            mSemiCirclePath.set(mBottomSemiCirclePath);
            mSemiCirclePath.op(mRightSemiCirclePath, Path.Op.UNION);

            mAddPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mAddPath.lineTo(bezierRightStartX, bezierRightStartY);
            mAddPath.lineTo(bezierRightPeakX, bezierRightPeakY);
            mAddPath.lineTo(bezierBottomPeakX, bezierBottomPeakY);
            mAddPath.close();

//            mFoldAndNextPath.lineTo(width, height - sideLong);
//            mFoldAndNextPath.lineTo(width, height);
//            mFoldAndNextPath.lineTo(width - sideShort, height);
        }
        mFoldAndNextPath.close();
//        mPath.close();
//        mPath.op(mAddPath, Path.Op.UNION);
//        mPath.op(mSemiCirclePath, Path.Op.DIFFERENCE);

//        canvas.save();
//        canvas.clipRect(0, 0, width, height);
//        canvas.clipPath(mFoldAndNextPath, Region.Op.DIFFERENCE);
//        canvas.drawColor(Color.RED);
//        canvas.restore();
//
//        canvas.save();
//        canvas.clipPath(mPath);
//        canvas.clipPath(mAddPath, Region.Op.UNION);
//        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
//        canvas.drawColor(Color.GREEN);
//        canvas.restore();
//
//        canvas.save();
////        canvas.clipPath(mPath);
////        canvas.clipPath(mAddPath, Region.Op.UNION);
////        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
//        canvas.clipPath(mFoldAndNextPath);
//        canvas.clipPath(mSemiCirclePath, Region.Op.UNION);
//        canvas.clipPath(mAddPath, Region.Op.DIFFERENCE);
//        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
//        canvas.drawColor(Color.BLUE);
//        canvas.restore();

//        canvas.drawPath(mPath, mPathPaint);
        drawBitmaps(canvas);
    }

    private int pageIndex;
    private boolean isLastPage;
    private void drawBitmaps(Canvas canvas)
    {
        isLastPage = false;
        int size = mBitmaps.size();
        pageIndex = pageIndex < 0 ? 0 : pageIndex;
        pageIndex = pageIndex > size ? size : pageIndex;

        int start = size - pageIndex - 2;
        int end = size - pageIndex;
        if(start < 0)
        {
            isLastPage = true;
            start = 0;
            end = 1;
        }
//        canvas.drawPath(mPath, mPaint);

        canvas.save();
        canvas.clipRect(0, 0, width, height);
//        canvas.clipPath(mFoldAndNextPath, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
        canvas.restore();

        canvas.save();
        canvas.clipPath(mPath);
        canvas.clipPath(mAddPath, Region.Op.UNION);
        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
        canvas.translate(pointX, pointY);
        if(mRatio == Ratio.SHORT)
        {
            canvas.rotate(90 - mDegrees);
            canvas.scale(-1, 1);
            canvas.translate(-width, -height);
        }else
        {
            canvas.rotate(mDegrees - 90);
            canvas.scale(1, -1);
            canvas.translate(-width, -height);
        }
        canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
        canvas.restore();

        canvas.save();
//        canvas.clipPath(mPath);
//        canvas.clipPath(mAddPath, Region.Op.UNION);
//        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
        canvas.clipPath(mFoldAndNextPath);
        canvas.clipPath(mSemiCirclePath, Region.Op.UNION);
        canvas.clipPath(mAddPath, Region.Op.DIFFERENCE);
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mBitmaps.get(start), 0, 0, null);
        canvas.restore();

//        canvas.save();
//        canvas.clipRect(0, 0, width, height);
//        canvas.clipPath(mFoldAndNextPath, Region.Op.DIFFERENCE);
////        canvas.drawColor(Color.RED);
//        canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
//        canvas.restore();
//
//        canvas.save();
//        canvas.clipPath(mPath);
//        canvas.translate(pointX, pointY);
//        if(mRatio == Ratio.SHORT)
//        {
//            canvas.rotate(90 - mDegrees);
//            canvas.scale(-1, 1);
//            canvas.translate(-width, -height);
//        }else
//        {
//            canvas.rotate(mDegrees - 90);
//            canvas.scale(1, -1);
//            canvas.translate(-width, -height);
//        }
//        canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
////        canvas.drawColor(Color.GREEN);
//        canvas.restore();
//
//        canvas.save();
//        canvas.clipPath(mFoldAndNextPath);
//        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
////        canvas.drawColor(Color.BLUE);
//        canvas.drawBitmap(mBitmaps.get(start), 0, 0, null);
//        canvas.restore();
    }

    private Toast toast;
    private void show(String msg)
    {
        if(toast == null)
            toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    private int width, height, centerX, centerY;
    private Region mPointYRegion;
    private float mAutoAreaLeft, mAutoAreaBottom, mAutoAreaRight;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w; height = h; centerX = w / 2; centerY = h / 2;
        mPointYRegion = new Region();
        mPointYRegion.set(computePointYRegion());
        mAutoAreaLeft = 1f * width / 4f;
        mAutoAreaBottom = 1f * height / 4f;
        mAutoAreaRight = 3f * width / 4f;
        mBitmaps = new ArrayList<>();
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_a));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_b));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_c));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_d));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_e));

        scaleBitmaps();
    }

    private void scaleBitmaps()
    {
        if(null == mBitmaps || mBitmaps.size() == 0)
            return;
        Collections.reverse(mBitmaps);
        List<Bitmap> bitmaps = new ArrayList<>();
        for(Bitmap bitmap : mBitmaps)
            bitmaps.add(Bitmap.createScaledBitmap(bitmap, width, height, true));
        mBitmaps = bitmaps;
    }

    private Region computePointYRegion()
    {
        Region r = new Region();
        RectF rectF = new RectF();
        Path path = new Path();
        path.addCircle(0, height, width, Path.Direction.CCW);
        path.computeBounds(rectF, true);
        r.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        return r;
    }

    private Slide mSlide = Slide.LEFT;
    private boolean isSlide, isFirstPage;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println(isSlide+"haha");
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                isSlide = false;
                if(x < mAutoAreaLeft || (x > centerX && x < mAutoAreaRight))
                {
                    if(pageIndex == 0)
                    {
                        isFirstPage = true;
                        show("The First Page.");
                        return true;
                    }
                    pointX = x;
                    pointY = y;
                    pageIndex --;
                    invalidate();
                }
                downOrMove(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isFirstPage)
                    return true;
                downOrMove(event);
                break;
            case MotionEvent.ACTION_UP:
                if(isFirstPage)
                {
                    isFirstPage = false;
                    return true;
                }
                if((x > mAutoAreaRight && y > mAutoAreaBottom) || (x > mAutoAreaLeft && x < centerX)) {
                    mSlide = Slide.LEFT;
                    slide(x, y);
                }else if(x < mAutoAreaLeft || (x > centerX && x < mAutoAreaRight))
                {
                    mSlide = Slide.RIGHT;
                    slide(x, y);
                }
                break;
        }
        return true;
    }

    private void downOrMove(MotionEvent event) {
        if(!isLastPage)
        {
            pointX = event.getX();
            pointY = event.getY();
            invalidate();
        }else
            show("The Last Page.");
    }

    private enum Slide{
        LEFT, RIGHT
    }

    private void slide(float x, float y)
    {
        startX = x;
        startY = y;
        isSlide = true;
        slide();
    }

    private float startX, startY;
    private void slide()
    {
        if(!isSlide)
            return;

        if(pointX >= width || pointX <= - width)
            isSlide = false;
        if(!isLastPage && pointX - 150 < -width)
        {
            isSlide = false;
            pointX = width;
            pointY = height;
            pageIndex ++;
            if(pageIndex == mBitmaps.size())
                isLastPage = true;
            invalidate();
        }else
        if(mSlide == Slide.LEFT && pointX < width)
        {
            pointX += 160;
            pointY = height - (width - pointX) * (height - startY) / (width - startX);
            mSlideHandler.sleep(1);
        }else if(!isLastPage && mSlide == Slide.RIGHT && pointX > -width)
        {
            pointX -= 180;
            pointY = height - (width + pointX) * (height - startY) / (width + startX);
            mSlideHandler.sleep(1);
        }


    }

    private class SlideHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            C33_FoldView.this.invalidate();

            C33_FoldView.this.slide();
        }

        public void sleep(long millis)
        {
            removeMessages(0);
            sendMessageDelayed(obtainMessage(0), millis);
        }
    }
}
