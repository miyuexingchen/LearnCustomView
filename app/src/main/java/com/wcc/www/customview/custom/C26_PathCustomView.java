package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.R.attr.x;

/**
 * Created by 王晨晨 on 2017/9/18.
 */

public class C26_PathCustomView extends View {

    public synchronized void setData(List<PointF> pointFs, String signX, String signY) {
        /*
         * 数据为空直接GG
         */
        if (null == pointFs || pointFs.size() == 0)
            throw new IllegalArgumentException("No data to display !");

        /*
         * 控制数据长度不超过10个
         * 对于折线图来说数据太多就没必要用折线图表示了而是使用散点图
         */
        if (pointFs.size() > 10)
            throw new IllegalArgumentException("The data is too long to display !");

        // 设置数据并重绘视图
        this.pts = pointFs;
        this.signX = signX;
        this.signY = signY;
        invalidate();
    }
    private static final float LEFT = 1 / 16F, TOP = 1 / 16F, RIGHT = 15 / 16F, BOTTOM = 7 / 8F;
    private static final float TIME_X = 3 / 32F, TIME_Y = 1 / 16F, MONEY_X = 31 / 32F, MONEY_Y = 15 / 16F;
    private static final float TEXT_SIGN = 1 / 32F;
    private static final float THICK_LINE_WIDTH = 1 / 128F, THIN_LINE_WIDTH = 1 / 512F;
    private int viewsize;
    private float textX_X, textX_Y, textY_X, textY_Y;
    private float textSize;
    private float left, top, right, bottom;
    private float thickLineWidth, thinLineWidth;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewsize = w;

        textX_X = viewsize * MONEY_X;
        textX_Y = viewsize * MONEY_Y;
        textY_X = viewsize * TIME_X;
        textY_Y = viewsize * TIME_Y;

        textSize = viewsize * TEXT_SIGN;

        left = viewsize * LEFT;
        top = viewsize * TOP;
        right = viewsize * RIGHT;
        bottom = viewsize * BOTTOM;

        thickLineWidth = viewsize * THICK_LINE_WIDTH;
        thinLineWidth = viewsize * THIN_LINE_WIDTH;
    }

    public C26_PathCustomView(Context context) {
        this(context, null);
    }

    private Paint mTextPaint, mLinePaint, mPointPaint;
    private Path mPath;
    private Canvas mCanvas;
    public C26_PathCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTextPaint.setColor(Color.WHITE);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(Color.WHITE);

        mPath = new Path();

        mCanvas = new Canvas();

        initData();
    }

    private List<PointF> pts;
    private void initData()
    {
        Random random = new Random();
        pts = new ArrayList<>();
        for(int i = 0; i < 20; i ++)
        {
            PointF p = new PointF();
            p.x = random.nextInt(100) * i;
            p.y = random.nextInt(100) * i;
            pts.add(p);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(0xff9596c4);
        drawSign(canvas);
        drawGrid(canvas);
        drawPolyLine(canvas);
    }

    private void drawPolyLine(Canvas canvas)
    {
// 生成一个Bitmap对象大小和我们的网格大小一致
        Bitmap mBitmap = Bitmap.createBitmap((int) (viewsize * (RIGHT - LEFT) - spaceX), (int) (viewsize * (BOTTOM - TOP) - spaceY), Bitmap.Config.ARGB_8888);

        // 将Bitmap注入Canvas
        mCanvas.setBitmap(mBitmap);

        // 为画布填充一个半透明的红色
        mCanvas.drawARGB(75, 255, 0, 0);

        // 重置曲线
        mPath.reset();

        /*
         * 生成Path和绘制Point
         */
        for (int i = 0; i < pts.size(); i++) {
            // 计算x坐标
            float x = mCanvas.getWidth() / maxX * pts.get(i).x;

            // 计算y坐标
            float y = mCanvas.getHeight() / maxY * pts.get(i).y;
            y = mCanvas.getHeight() - y;

            // 绘制小点点
//            mCanvas.drawCircle(x, y, thickLineWidth, mPointPaint);

            /*
             * 如果是第一个点则将其设置为Path的起点
             */
            if (i == 0) {
                mPath.moveTo(x, y);
            }

            // 连接各点
            mPath.lineTo(x, y);
        }

        // 设置PathEffect
        mLinePaint.setPathEffect(new CornerPathEffect(120));

        // 重置线条宽度
        mLinePaint.setStrokeWidth(thickLineWidth);

        // 将Path绘制到我们自定的Canvas上
        mCanvas.drawPath(mPath, mLinePaint);

        // 将mBitmap绘制到原来的canvas
        canvas.drawBitmap(mBitmap, left, top + spaceY, null);
    }

    private float[] rulerX, rulerY;
    private float maxX, maxY;
    private float spaceX, spaceY;
    private void drawLines(Canvas canvas)
    {
        float rulerTextSize = textSize / 2f;
        mTextPaint.setTextSize(rulerTextSize);

        mLinePaint.setStrokeWidth(thinLineWidth);

        int count = pts.size();
        int divisor = count - 1;
        maxX = 0;
        for(int i = 0; i < count; i ++)
        {
            if(pts.get(i).x > maxX)
                maxX = pts.get(i).x;
        }
        int remainderX = ((int) maxX) % divisor;
        maxX = remainderX == 0 ? (int) maxX : divisor - remainderX + (int) maxX;

        maxY = 0;
        for(int i = 0; i < count; i ++)
        {
            if(pts.get(i).y > maxY)
                maxY = pts.get(i).y;
        }
        int remainderY = ((int) maxY) % divisor;
        maxY = remainderY == 0 ? (int) maxY : divisor - remainderY + (int) maxY;

        rulerX = new float[count];
        for(int i = 0; i < count; i ++)
            rulerX[i] = i * maxX / divisor;

        rulerY = new float[count];
        for(int i = 0; i < count; i ++)
            rulerY[i] = i * maxY / divisor;

        spaceX = (right - left) / count;
        spaceY = (bottom - top) / count;

        int layerId = canvas.saveLayerAlpha(0, 0, canvas.getWidth(), canvas.getHeight(), 75, Canvas.ALL_SAVE_FLAG);
        for(float y = bottom - spaceY; y > top; y -= spaceY)
        {
            for(float x = left; x < right; x += spaceX)
            {
                if (y == viewsize * TOP + spaceY) {
                    canvas.drawLine(x, y, x, y + spaceY * (count - 1), mLinePaint);
                }

                if (x == viewsize * RIGHT - spaceX) {
                    canvas.drawLine(x, y, x - spaceX * (count - 1), y, mLinePaint);
                }
            }
        }
        canvas.restoreToCount(layerId);

        int index_x = 0, index_y = 1;
        for (float y = viewsize * BOTTOM - spaceY; y > viewsize * TOP; y -= spaceY) {
            for (float x = viewsize * LEFT; x < viewsize * RIGHT; x += spaceX) {  
                /* 
                 * 绘制横轴刻度数值 
                 */
                if (y == viewsize * BOTTOM - spaceY) {
                    canvas.drawText(String.valueOf(rulerX[index_x]), x, y + textSize + spaceY, mTextPaint);
                }  
  
                /* 
                 * 绘制纵轴刻度数值 
                 */
                if (x == viewsize * LEFT) {
                    canvas.drawText(String.valueOf(rulerY[index_y]), x - thickLineWidth, y + rulerTextSize, mTextPaint);
                }

                index_x++;
            }
            index_y++;
        }
    }

    private void drawGrid(Canvas canvas)
    {
        canvas.save();

        mLinePaint.setStrokeWidth(thickLineWidth);
        mPath.moveTo(left, top);
        mPath.lineTo(left, bottom);
        mPath.lineTo(right, bottom);
        canvas.drawPath(mPath, mLinePaint);

        drawLines(canvas);

        canvas.restore();
    }

    private String signX, signY;
    private void drawSign(Canvas canvas)
    {
        canvas.save();
        mTextPaint.setTextSize(textSize);

        mTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(null == signY ? "y" : signY, textY_X, textY_Y, mTextPaint);

        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(null == signX ? "x" : signX, textX_X, textX_Y, mTextPaint);
        canvas.restore();
    }
}
