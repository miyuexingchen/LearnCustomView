package com.wcc.www.customview.customstore;

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
import android.text.TextPaint;
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
 * Created by 王晨晨 on 2017/9/25.
 */

public class C34_FoldViewExercise extends View {

    private Paint mPaint, mTextPaint;
    private SlideHandler handler;

    public C34_FoldViewExercise(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        handler = new SlideHandler();
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL);
    }

    private class SlideHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            C34_FoldViewExercise.this.invalidate();
            C34_FoldViewExercise.this.slide();
        }

        public void sleep(long millis) {
            removeMessages(0);
            sendMessageDelayed(obtainMessage(0), millis);
        }
    }

    private int sp2px(int value)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }
    private void defaultDraw(Canvas canvas)
    {
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTextSize(sp2px(28));
        canvas.drawText("No Bitmap", width / 2, height / 3, mTextPaint);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(sp2px(18));
        canvas.drawText("please set bitmaps data via setBitmaps method.", width / 2, height / 2, mTextPaint);
    }
    private float pointX, pointY;
    private Path mPath;
    private enum Ratio{
        SHORT, LONG
    }
    private Ratio mRatio;
    private float mDegrees;
    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmaps == null || mBitmaps.size() == 0)
        {
            defaultDraw(canvas);
            return;
        }
        mPath.reset();
        mFoldAndNextPath.reset();
        mAddPath.reset();
        mSemiCirclePath.reset();
        if(pointX == 0 && pointY == 0) {
            canvas.drawBitmap(mBitmaps.get(mBitmaps.size() - 1), 0, 0, null);
            return;
        }

        if(!pointRegion.contains((int)pointX, (int)pointY))
        {
            pointY = (float) (height - Math.sqrt(Math.pow(width, 2) - Math.pow(pointX, 2)) + mValueAdded);
        }

        if(pointY > height)
            pointY = height - 0.001f;

        float S = width - pointX;
        float L = height - pointY;
        float powSumOfSAndL = (float) (Math.pow(S, 2) + Math.pow(L, 2));
        float sideShort = powSumOfSAndL / (2 * S);
        float sideLong = powSumOfSAndL / (2 * L);

        if(sideLong > sideShort)
        {
            mRatio = Ratio.LONG;
            float sinvalue = (width - pointX - sideShort) / sideShort;
            mDegrees = (float) (Math.asin(sinvalue) / Math.PI * 180);
        }else
        {
            mRatio = Ratio.SHORT;
            float cosValue = (width - pointX) / sideLong;
            mDegrees = (float) (Math.acos(cosValue) / Math.PI * 180);
        }
//        mPath.moveTo(pointX, pointY);
//        mFoldAndNextPath.moveTo(pointX, pointY);
        float bottomInitX = width - sideShort;
        float rate = 1f / 4f;
        float bezierBottomStartX = bottomInitX - sideShort * rate;
        float bezierBottomStartY = height;

        float bezierBottomEndX = pointX + (bottomInitX - pointX) * (1 - rate);
        float bezierBottomEndY = pointY + (height - pointY) * (1 - rate);

        float bezierBottomCtrlX = bottomInitX;
        float bezierBottomCtrlY = height;

        float bezierBottomPeakX = 0.25f * (bezierBottomStartX + bezierBottomEndX) + 0.5f * bezierBottomCtrlX;
        float bezierBottomPeakY = 0.25f * (bezierBottomStartY + bezierBottomEndY) + 0.5f * bezierBottomCtrlY;
        if(sideLong > height)
        {
            float leftTopWidth = (width - pointX) * (sideLong - height) / (sideLong - (height - pointY));
            float middleTopwidth = (sideLong - height) * sideShort / sideLong;

            float leftTopX = width - leftTopWidth;
            float middleTopX = width - middleTopwidth;

            mPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mPath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mPath.lineTo(pointX, pointY);
            mPath.lineTo(leftTopX, 0);
            mPath.lineTo(middleTopX, 0);

//            mPath.lineTo(leftTopX, 0);
//            mPath.lineTo(middleTopX, 0);
//            mPath.lineTo(bottomInitX, height);

            mSemiCirclePath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mSemiCirclePath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mSemiCirclePath.close();

            mAddPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mAddPath.lineTo(middleTopX, 0);
            mAddPath.lineTo(bezierBottomPeakX, bezierBottomPeakY);
            mAddPath.close();

            mFoldAndNextPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mFoldAndNextPath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mFoldAndNextPath.lineTo(pointX, pointY);
            mFoldAndNextPath.lineTo(leftTopX, 0);
            mFoldAndNextPath.lineTo(width, 0);
            mFoldAndNextPath.lineTo(width, height);

//            mFoldAndNextPath.lineTo(leftTopX, 0);
//            mFoldAndNextPath.lineTo(width, 0);
//            mFoldAndNextPath.lineTo(width, height);
//            mFoldAndNextPath.lineTo(bottomInitX, height);
        }else{
            float rightInitY = height - sideLong;

            float bezierRightStartX = width;
            float bezierRightStartY = rightInitY - sideLong * rate;

            float bezierRightEndX = pointX + (width - pointX) * (1 - rate);
            float bezierRightEndY = pointY - (pointY - rightInitY) * (1 - rate);

            float bezierRightCtrlX = width;
            float bezierRightCtrlY = rightInitY;

            float bezierRightPeakX = 0.25f * (bezierRightStartX + bezierRightEndX) + 0.5f * bezierRightCtrlX;
            float bezierRightPeakY = 0.25f * (bezierRightStartY + bezierRightEndY) + 0.5f * bezierRightCtrlY;

            subWidthStart = Math.round(bottomInitX / subMinWidth) - 1;
            subWidthEnd = Math.round((bottomInitX + rate * sideShort) / subMinWidth) + 1;
            subHeightStart = Math.round(rightInitY / subMinHeight) - 1;
            subHeightEnd = Math.round((rightInitY + rate * sideLong) / subMinHeight) + 1;

            int index = 0;
            float offsetLong = rate / 2f * sideLong;
            float offsetShort = rate / 2f * sideShort;
            float mulOffsetShort = 1.0f;
            float mulOffsetLong = 1.0f;
            for(int y = 0; y < 19; y ++)
            {
                float fy = height * y / 19;
                for(int x = 0; x < 19; x ++)
                {
                    float fx = width * y / 19;
                    if(x == 19)
                    {
                        if(y >= subHeightStart && y <= subHeightEnd)
                        {
                            fx = width * x / 19 + offsetLong * mulOffsetLong;
                            mulOffsetLong /= 1.5f;
                        }
                    }

                    if(y == 19)
                    {
                        if(x >= subWidthStart && x <= subWidthEnd)
                        {
                            fy = height * y / 19 + offsetShort * mulOffsetShort;
                            mulOffsetShort /= 1.5f;
                        }
                    }

                    verts[index * 2 + 0] = fx;
                    verts[index * 2 + 1] = fy;
                    index ++;
                }
            }

            mPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mPath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mPath.lineTo(pointX, pointY);
            mPath.lineTo(bezierRightEndX, bezierRightEndY);
            mPath.quadTo(bezierRightCtrlX, bezierRightCtrlY, bezierRightStartX, bezierRightStartY);

            mSemiCirclePath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mSemiCirclePath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mSemiCirclePath.close();

            Path rightSemiCirclePath = new Path();
            rightSemiCirclePath.moveTo(bezierRightStartX, bezierRightStartY);
            rightSemiCirclePath.quadTo(bezierRightCtrlX, bezierRightCtrlY, bezierRightEndX, bezierRightEndY);
            rightSemiCirclePath.close();

            mSemiCirclePath.op(rightSemiCirclePath, Path.Op.UNION);

            mAddPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mAddPath.lineTo(bezierRightStartX, bezierRightStartY);
            mAddPath.lineTo(bezierRightPeakX, bezierRightPeakY);
            mAddPath.lineTo(bezierBottomPeakX, bezierBottomPeakY);
            mAddPath.close();

            mFoldAndNextPath.moveTo(bezierBottomStartX, bezierBottomStartY);
            mFoldAndNextPath.quadTo(bezierBottomCtrlX, bezierBottomCtrlY, bezierBottomEndX, bezierBottomEndY);
            mFoldAndNextPath.lineTo(pointX, pointY);
            mFoldAndNextPath.lineTo(bezierRightEndX, bezierRightEndY);
            mFoldAndNextPath.quadTo(bezierRightCtrlX, bezierRightCtrlY, bezierRightStartX, bezierRightStartY);
            mFoldAndNextPath.lineTo(width, height);

//            mPath.lineTo(width, rightInitY);
//            mPath.lineTo(bottomInitX, height);

//            mFoldAndNextPath.lineTo(width, rightInitY);
//            mFoldAndNextPath.lineTo(width, height);
//            mFoldAndNextPath.lineTo(bottomInitX, height);
        }
//        mPath.close();
//        mFoldAndNextPath.close();
//        canvas.drawPath(mPath, mPaint);
        drawBitmaps(canvas);

       /* canvas.save();
        canvas.clipRect(0, 0, width, height);
        canvas.drawColor(Color.YELLOW);
        canvas.restore();

        canvas.save();
        canvas.clipPath(mPath);
        canvas.clipPath(mAddPath, Region.Op.UNION);
        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.MAGENTA);
        canvas.restore();

        canvas.save();
        canvas.clipPath(mPath);
        canvas.clipPath(mAddPath, Region.Op.UNION);
        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
        canvas.clipPath(mFoldAndNextPath, Region.Op.REVERSE_DIFFERENCE);
        canvas.drawColor(Color.CYAN);
        canvas.restore();*/
//        canvas.save();
//        canvas.clipRect(0, 0, width, height);
//        canvas.drawColor(Color.RED);
//        canvas.restore();
//
//        canvas.save();
//        canvas.clipPath(mPath);
//        canvas.drawColor(Color.GREEN);
//        canvas.restore();
//
//        canvas.save();
//        canvas.clipPath(mFoldAndNextPath);
//        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
//        canvas.drawColor(Color.BLUE);
//        canvas.restore();
    }

    private boolean isLastPage;
    private int pageIndex;
    private Toast toast;
    private void show(String msg)
    {
        if(toast == null)
            toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }
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
            show("The Last Page.");
            start = 0;
            end = 1;
            isLastPage = true;
        }

        canvas.save();
        canvas.clipRect(0, 0, width, height);
        canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
        canvas.restore();

        canvas.save();
        canvas.clipPath(mPath);
        canvas.clipPath(mAddPath, Region.Op.UNION);
        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
        canvas.translate(pointX, pointY);
        if(mRatio == Ratio.LONG)
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
        canvas.clipPath(mFoldAndNextPath);
        canvas.clipPath(mSemiCirclePath, Region.Op.UNION);
        canvas.clipPath(mAddPath, Region.Op.DIFFERENCE);
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mBitmaps.get(start), 0, 0, null);
        canvas.restore();
    }

    private int width, height;
    private Region pointRegion;
    private float mValueAdded;
    private float mAutoAreaRight, mAutoAreaBottom, mAutoAreaLeft;
    private Path mFoldAndNextPath, mAddPath, mSemiCirclePath;
    private List<Bitmap> mBitmaps;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        mPath = new Path();
        pointRegion = new Region();
        computePointRegion();
        mValueAdded = height * 1.0f / 500f;
        mAutoAreaBottom = 3f * height / 4f;
        mAutoAreaLeft = width / 8f;
        mAutoAreaRight = 7f * width / 8f;
        mFoldAndNextPath = new Path();
        mBitmaps = new ArrayList<>();
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.a));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.b));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.c));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.d));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.e));
        scaleBitmaps();
        mAddPath = new Path();
        mSemiCirclePath = new Path();
        verts = new float[800];
        subMinWidth = width / 19;
        subMinHeight = height / 19;
    }
    private int subMinWidth, subMinHeight;
    private int subWidthStart, subWidthEnd, subHeightStart, subHeightEnd;
    private float[] verts;

    private void scaleBitmaps()
    {
        if(mBitmaps == null || mBitmaps.size() == 0)
            return;
        List<Bitmap> tmp = new ArrayList<>();
        Collections.reverse(mBitmaps);
        for(Bitmap b : mBitmaps)
            tmp.add(Bitmap.createScaledBitmap(b, width, height, true));
        mBitmaps = tmp;
    }

    private void computePointRegion()
    {
        Path path = new Path();
        RectF rectF = new RectF();
        path.addCircle(0, height, width, Path.Direction.CCW);
        path.computeBounds(rectF, true);
        pointRegion.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
    }

    private enum Slide{
        RIGHT, LEFT
    }
    private Slide mSlide;
    private boolean isFirstPage;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                isSlide = false;
                if(x < mAutoAreaLeft)
                {
                    if(pageIndex == 0)
                    {
                        show("The First Page.");
                        isFirstPage = true;
                        return true;
                    }
                    pageIndex --;
                    pointX = x;
                    pointY = y;
                    invalidate();
                }
                downOrMove(event);

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
                startX = event.getX();
                startY = event.getY();
                if(startX > mAutoAreaRight && startY > mAutoAreaBottom)
                {
                    mSlide = Slide.RIGHT;
                    isSlide = true;
                    slide();
                }else if(startX < mAutoAreaLeft)
                {
                    mSlide = Slide.LEFT;
                    isSlide = true;
                    slide();
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

    private float startX, startY;
    private boolean isSlide;
    private void slide()
    {
        if(!isSlide)
            return;
        if(!isLastPage && pointX - 60 < -width)
        {
            pointX = width;
            pointY = height;
            pageIndex ++;
            invalidate();
        }else if(mSlide == Slide.RIGHT && pointX < width)
        {
            pointX += 20;
            pointY = startY + (height - startY) * (pointX - startX) / (width - startX);
            handler.sleep(1);
        }else if(!isLastPage && mSlide == Slide.LEFT && pointX > -width)
        {
            pointX -= 60;
            pointY = startY + (startX - pointX) * (height - startY) / (startX + width);
            handler.sleep(1);
        }
    }
}
