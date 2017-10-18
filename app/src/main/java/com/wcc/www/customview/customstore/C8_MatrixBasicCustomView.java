package com.wcc.www.customview.customstore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 王晨晨 on 2017/9/11.
 */

public class C8_MatrixBasicCustomView extends View {
    public C8_MatrixBasicCustomView(Context context) {
        this(context, null);
    }

    public C8_MatrixBasicCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private Paint mPaint, mTextPaint, mValuePaint;
    private void initPaint(){
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(40);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.GRAY);
        mValuePaint = new Paint();
        mValuePaint.setColor(Color.BLUE);
        mValuePaint.setStrokeWidth(3);
        mValuePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        drawPolygon(canvas);
        drawLines(canvas);
        drawText(canvas);
        drawRegion(canvas);
    }

    private float[] mDatas = {98, 37, 35, 66, 99, 1};
    private float maxValue = 100;
    private void drawRegion(Canvas canvas)
    {
        Path path = new Path();
        for(int i = 0; i < count; i ++)
        {
            float per = mDatas[i] / maxValue;
            float a;
            if(count % 2 == 0)
                a = angle * i;
            else
                a = (float) (- Math.PI / 2 + angle * i);
            float x = (float) (centerX + radius * per * Math.cos(a));
            float y = (float) (centerY + radius * per * Math.sin(a));
            if(i == 0)
                path.moveTo(x, y);
            else
                path.lineTo(x, y);
            mValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(x, y, regionVertiesRadius, mValuePaint);
        }
        mValuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mValuePaint);
        mValuePaint.setStyle(Paint.Style.FILL);
        mValuePaint.setAlpha(127);
        canvas.drawPath(path, mValuePaint);
    }

    private String[] titles = {"1", "2", "3", "4", "5", "6"};
    private void drawText(Canvas canvas)
    {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for(int i = 0; i < count; i ++)
        {
            float a;
            if(count % 2 == 0)
                a = angle * i;
            else
                a = (float) (- Math.PI / 2 + angle * i);
            float x = (float) (centerX + Math.cos(a) * (radius + fontHeight / 2));
            float y = (float) (centerY + Math.sin(a) * (radius + fontHeight / 2));
            String title = titles[i];
            if(a >= Math.PI / 2 && a <= 3 * Math.PI / 2) {
                float dis = mTextPaint.measureText(title);
                canvas.drawText(title, x - dis, y, mTextPaint);
            }else
                canvas.drawText(title, x, y, mTextPaint);
        }
    }

    private void drawLines(Canvas canvas)
    {
        Path path = new Path();
        for(int i = 0; i < count; i ++)
        {
            path.reset();
            float a;
            if(count % 2 == 0)
                a = angle * i;
            else
                a = (float) (- Math.PI / 2 + angle * i);
            float x = (float) (centerX + Math.cos(a) * radius);
            float y = (float) (centerY + Math.sin(a) * radius);
            path.moveTo(centerX, centerY);
            path.lineTo(x, y);
            canvas.drawPath(path, mPaint);
        }
    }

    private int count = 6;
    private int layer = count;
    private float angle;
    private void drawPolygon(Canvas canvas)
    {
        angle = (float) (2 * Math.PI / count);
        float r = radius / (layer - 1);
        Path path = new Path();
        for(int i = 1; i < layer; i ++)
        {
            path.reset();
            for(int j = 0; j < count; j ++)
            {
                float currentRadius = r * i;
                float a;
                if(count % 2 == 0)
                    a = angle * j;
                else
                    a = (float) (- Math.PI / 2 + angle * j);
                float x = (float) (centerX + Math.cos(a) * currentRadius);
                float y = (float) (centerY + Math.sin(a) * currentRadius);
                if(j == 0)
                    path.moveTo(x, y);
                else
                    path.lineTo(x, y);
            }
            path.close();
            canvas.drawPath(path, mPaint);
        }
    }

    private int centerX, centerY;
    private float radius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = (float) (Math.min(w, h) / 2 * 0.8);
        centerX = w / 2;
        centerY = h / 2;
    }

    public C8_MatrixBasicCustomView setDatas(float[] datas)
    {
        this.mDatas = datas;
        return this;
    }

    public C8_MatrixBasicCustomView setTitles(String[] titles)
    {
        this.titles = titles;
        return this;
    }

    public C8_MatrixBasicCustomView setCount(int count)
    {
        this.count = count;
        return this;
    }

    public C8_MatrixBasicCustomView setLayer(int layer)
    {
        this.layer = layer;
        return this;
    }

    public C8_MatrixBasicCustomView setTitleTextSize(int size)
    {
        mTextPaint.setTextSize(size);
        return this;
    }

    public C8_MatrixBasicCustomView setTitleColor(int color)
    {
        mTextPaint.setColor(color);
        return this;
    }

    public C8_MatrixBasicCustomView setPaintColor(int color)
    {
        mPaint.setColor(color);
        return this;
    }

    public C8_MatrixBasicCustomView setRegionColor(int color)
    {
        mPaint.setColor(color);
        return this;
    }

    private int regionVertiesRadius = 10;
    public C8_MatrixBasicCustomView setRegionVertiesRadius(int r)
    {
        regionVertiesRadius = r;
        return this;
    }


}
