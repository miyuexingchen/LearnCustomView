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
 * Created by 王晨晨 on 2017/9/20.
 */

public class C32_FoldCustomView extends View {
    private Paint mPaint, mPathPaint;
    private SlideHandler mSlideHandler;

    private int sp2px(int sp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
    public C32_FoldCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
        mSlideHandler = new SlideHandler();
        mFoldAndNextPath = new Path();
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(10);
        mPathPaint.setColor(Color.BLACK);
        mSemiCirclePath = new Path();
        mAddPath = new Path();
    }

    private void defaultDraw(Canvas canvas)
    {
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(sp2px(28));
        mPaint.setColor(Color.RED);
        canvas.drawText("NO BITMAP", width / 2, height / 3, mPaint);

        mPaint.setTextSize(sp2px(18));
        mPaint.setColor(Color.BLACK);
        canvas.drawText("please set bitmaps via setBitmaps method.", width / 2 ,height / 2, mPaint);
    }

    private Path mPath, mFoldAndNextPath, mSemiCirclePath, mAddPath;
    private List<Bitmap> mBitmaps;
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
        mSemiCirclePath.reset();
        mAddPath.reset();

        if(pointX == 0 && pointY == 0) {
            canvas.drawBitmap(mBitmaps.get(mBitmaps.size() - 1), 0, 0, null);
            return;
        }

        if(!mShortSideRegion.contains((int)pointX, (int)pointY))
        {
            pointY = (float) (Math.sqrt(Math.pow(width, 2) - Math.pow(pointX, 2)) - height);
            pointY = Math.abs(pointY) + mValueAdded;
        }

//        float area = height - mBufferArea;
//        if(!isSlide && pointY > area) {
//            pointY = area;
//            if(pointX > width - mBufferArea)
//                pointX = width - mBufferArea;
//        }
        float K = width - pointX;
        float L = height - pointY;

        float temp = (float) (Math.pow(K, 2) + Math.pow(L, 2));

        float sideShort = temp / (2f * K);
        float sideLong = temp / (2f * L);

        if(sideShort < sideLong)
        {
            float sinValue = (width - pointX - sideShort) / sideShort;
            mDegrees = (float) (Math.asin(sinValue) / Math.PI * 180);
            mRadio = Radio.SHORT;
        }else
        {
            float cosValue = (width - pointX) / sideLong;
            mDegrees = (float) (Math.acos(cosValue) / Math.PI * 180);
            mRadio = Radio.LONG;
        }

        mPath.moveTo(pointX, pointY);
        mFoldAndNextPath.moveTo(pointX, pointY);
        float leftBottomX = width - sideShort;
        float rate = 1f / 4f;
        if(sideLong > height)
        {
            float deltaHeight = sideLong - height;
            float leftTopX = width - deltaHeight / (sideLong - (height - pointY)) * (width - pointX);
            float middleTopX = width - deltaHeight / sideLong * sideShort;

            float bottomStartX = leftBottomX - rate * sideShort;
            float bottomStartY = height;

            float bottomEndX = pointX + (1 - rate) * (leftBottomX - pointX);
            float bottomEndY = pointY + (1 - rate) * (height - pointY);

            float bottomCtrlX = leftBottomX;
            float bottomCtrlY = height;

            float bottomPeakX = 0.25f * bottomStartX + 0.5f * bottomCtrlX + 0.25f * bottomEndX;
            float bottomPeakY = 0.25f * bottomStartY + 0.5f * bottomCtrlY + 0.25f * bottomEndY;

            mPath.moveTo(bottomStartX, bottomStartY);
            mPath.quadTo(bottomCtrlX, bottomCtrlY, bottomEndX, bottomEndY);
            mPath.lineTo(pointX, pointY);
            mPath.lineTo(leftTopX, 0);
            mPath.lineTo(middleTopX, 0);

            mSemiCirclePath.moveTo(bottomStartX, bottomStartY);
            mSemiCirclePath.quadTo(bottomCtrlX, bottomCtrlY, bottomEndX, bottomEndY);
            mSemiCirclePath.close();

            mAddPath.moveTo(bottomStartX, bottomStartY);
            mAddPath.lineTo(middleTopX, 0);
            mAddPath.lineTo(bottomPeakX, bottomPeakY);
            mAddPath.close();

//            mPath.lineTo(leftTopX, 0);
//            mPath.lineTo(middleTopX, 0);
//            mPath.lineTo(leftBottomX, height);
//            mPath.close();

            mFoldAndNextPath.moveTo(bottomStartX, bottomStartY);
            mFoldAndNextPath.quadTo(bottomCtrlX, bottomCtrlY, bottomEndX, bottomEndY);
            mFoldAndNextPath.lineTo(pointX, pointY);
            mFoldAndNextPath.lineTo(leftTopX, 0);
            mFoldAndNextPath.lineTo(width, 0);
            mFoldAndNextPath.lineTo(width, height);
        }else {
            float rightTopX = height - sideLong;

            float bottomStartX = leftBottomX - rate * sideShort;
            float bottomStartY = height;

            float bottomEndX = pointX + (1 - rate) * (leftBottomX - pointX);
            float bottomEndY = pointY + (1 - rate) * (height - pointY);

            float bottomCtrlX = leftBottomX;
            float bottomCtrlY = height;

            float bottomPeakX = 0.25f * bottomStartX + 0.5f * bottomCtrlX + 0.25f * bottomEndX;
            float bottomPeakY = 0.25f * bottomStartY + 0.5f * bottomCtrlY + 0.25f * bottomEndY;

            mPath.moveTo(bottomStartX, bottomStartY);
            mPath.quadTo(bottomCtrlX, bottomCtrlY, bottomEndX, bottomEndY);
            mPath.lineTo(pointX, pointY);
            float rightStartX = width;
            float rightStartY = rightTopX - rate * sideLong;

            float rightEndX = pointX + (1 - rate) * K;
            float rightEndY = pointY - (1 - rate) * (sideLong - L);

            float rightCtrlX = width;
            float rightCtrlY = rightTopX;

            float rightPeakX = 0.25f * rightStartX + 0.5f * rightCtrlX + 0.25f * rightEndX;
            float rightPeakY = 0.25f * rightStartY + 0.5f * rightCtrlY + 0.25f * rightEndY;

            mPath.lineTo(rightEndX, rightEndY);
            mPath.quadTo(rightCtrlX, rightCtrlY, rightStartX, rightStartY);

            mSemiCirclePath.moveTo(bottomStartX, bottomStartY);
            mSemiCirclePath.quadTo(bottomCtrlX, bottomCtrlY, bottomEndX, bottomEndY);
            mSemiCirclePath.close();

            Path tmp = new Path();
            tmp.moveTo(rightStartX, rightStartY);
            tmp.quadTo(rightCtrlX, rightCtrlY, rightEndX, rightEndY);
            tmp.close();

            mSemiCirclePath.op(tmp, Path.Op.UNION);

            mAddPath.moveTo(bottomStartX, bottomStartY);
            mAddPath.lineTo(rightStartX, rightStartY);
            mAddPath.lineTo(rightPeakX, rightPeakY);
            mAddPath.lineTo(bottomPeakX, bottomPeakY);
            mAddPath.close();

            mFoldAndNextPath.moveTo(bottomStartX, bottomStartY);
            mFoldAndNextPath.quadTo(bottomCtrlX, bottomCtrlY, bottomEndX, bottomEndY);
            mFoldAndNextPath.lineTo(pointX, pointY);
            mFoldAndNextPath.lineTo(rightEndX, rightEndY);
            mFoldAndNextPath.quadTo(rightCtrlX, rightCtrlY, rightStartX, rightStartY);
            mFoldAndNextPath.lineTo(width, height);
        }
        mFoldAndNextPath.close();
//        canvas.drawPath(mPath, mPathPaint);
        drawBitmaps(canvas);
    }

    private Toast toast;
    private void show(String msg)
    {
        if(toast == null)
            toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    private boolean isLastPage;
    private int pageIndex;
    private enum Radio{
        SHORT, LONG,
    }
    private Radio mRadio;
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
//            show("The Last Page.");
            start = 0;
            end = 1;
        }

        canvas.save();
        canvas.clipRect(0, 0, getWidth(), getHeight());
//        canvas.clipPath(mFoldAndNextPath, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
        canvas.restore();

        canvas.save();
        canvas.clipPath(mPath);
        canvas.clipPath(mAddPath, Region.Op.UNION);
        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
        canvas.translate(pointX, pointY);
        if(mRadio == Radio.SHORT)
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
        canvas.clipPath(mPath);
        canvas.clipPath(mAddPath, Region.Op.UNION);
        canvas.clipPath(mSemiCirclePath, Region.Op.DIFFERENCE);
        canvas.clipPath(mFoldAndNextPath, Region.Op.REVERSE_DIFFERENCE);
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mBitmaps.get(start), 0, 0, null);
        canvas.restore();
    }

    private enum Slide{
        LEFT, RIGHT
    }
    private Slide mSlide;
    private float mStartX, mStartY;
    private int i = 0;
    private boolean isFirstPage;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(isSlide)
                    return true;
                if(x < mAutoAreaLeft)
                {
                    if(pageIndex == 0)
                    {
                        isFirstPage = true;
                        show("The First Page.");
                        return true;
                    }
                    pageIndex --;
                    pointX = x;
                    pointY = y;
                    invalidate();
                }
                downOrMove(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isFirstPage || isSlide)
                    return true;
                System.out.println("move");
                downOrMove(event);
                break;
            case MotionEvent.ACTION_UP:

                if(isFirstPage || isSlide)
                {
                    isFirstPage = false;
                    return true;
                }
                if((x > mAutoAreaRight && y > mAutoAreaBottom) || (x > mAutoAreaLeft && x < centerX))
                {
                    mSlide = Slide.RIGHT;
                    slide(x, y);
                }else if(x < mAutoAreaLeft || (x > centerX && x < mAutoAreaRight))
                {
                    mSlide = Slide.LEFT;
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

    private void slide(float x, float y)
    {
        mStartX = x;
        mStartY = y;

        isSlide = true;
        slide();
    }
    private boolean isSlide;
    private void slide()
    {
        if(!isSlide)
            return;

        if(!isLastPage && pointX - 150 <= -width)
        {
            pageIndex ++;
            pointX = width;
            pointY = height;
            invalidate();
        } else
        if(mSlide == Slide.RIGHT && pointX < width)
        {
            pointX += 30;
            pointY = mStartY + (pointX - mStartX) * (height - mStartY) / (width - mStartX);
            mSlideHandler.sleep(1);
        }else if(!isLastPage && mSlide == Slide.LEFT && pointX > -width)
        {
            pointX -= 150;
            pointY = mStartY + (pointX - mStartX) * (height - mStartY) / (width - mStartX);
            mSlideHandler.sleep(1);
        }

        if(pointX >= width || pointX <= -width)
            isSlide = false;
    }

    private class SlideHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            C32_FoldCustomView.this.invalidate();

            C32_FoldCustomView.this.slide();
        }

        public void sleep(long millis)
        {
            removeMessages(0);
            sendMessageDelayed(obtainMessage(0), millis);
        }
    }

    private float pointX, pointY;
    private int width, height;

    private Region mShortSideRegion;
    private static final float VALUE_ADDED = 1F / 500F, BUFFER_AREA = 1F / 50F;
    private float mValueAdded, mBufferArea;
    private float mAutoAreaRight, mAutoAreaBottom, mAutoAreaLeft, centerX;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        mShortSideRegion = new Region();
        computeShortSideRegion();
        mValueAdded = h * VALUE_ADDED;
        mBufferArea = h * BUFFER_AREA;
        mAutoAreaRight = 3f * width / 4f;
        mAutoAreaBottom = 1f * height / 4f;
        mAutoAreaLeft = 1f * width / 4f;
        centerX = width / 2f;
        mBitmaps = new ArrayList<>();
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.a));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.b));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.c));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.d));
        mBitmaps.add(BitmapFactory.decodeResource(getResources(), R.mipmap.e));
        scaleBitmaps();
    }

    private void scaleBitmaps()
    {
        if(mBitmaps == null || mBitmaps.size() == 0)
            return;
        Collections.reverse(mBitmaps);
        List<Bitmap> bitmaps = new ArrayList<>();
        for(Bitmap bitmap : mBitmaps)
        {
            Bitmap tmp = Bitmap.createScaledBitmap(bitmap, width, height, true);
            bitmaps.add(tmp);
        }
        mBitmaps = bitmaps;
    }

    private void computeShortSideRegion()
    {
        Path path = new Path();
        RectF rectF = new RectF();
        path.addCircle(0, height, width, Path.Direction.CCW);
        path.computeBounds(rectF, true);
        mShortSideRegion.setPath(path, new Region((int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom));
    }


}
