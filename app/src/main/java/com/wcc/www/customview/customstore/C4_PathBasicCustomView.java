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
 * Created by 王晨晨 on 2017/9/8.
 */

public class C4_PathBasicCustomView extends View {

    public C4_PathBasicCustomView(Context context) {
        this(context, null);
    }

    public C4_PathBasicCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private Paint mainPaint, textPaint, valuePaint;
    private void initPaint()
    {
        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStrokeWidth(3);
        mainPaint.setColor(Color.GRAY);
        mainPaint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(40);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        valuePaint = new Paint();
        valuePaint.setColor(Color.YELLOW);
        valuePaint.setAntiAlias(true);
        valuePaint.setStrokeWidth(3);
        valuePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPolygon(canvas);
        drawLines(canvas);
        drawText(canvas);
        drawRegion(canvas);
    }

    private String[] titles = {"1", "2", "3", "4", "5", "6"};
    private float datas[] = {81, 52, 93, 34, 75, 16};
    private float maxValue = 100;

    private void drawRegion(Canvas canvas)
    {
        Path path = new Path();
        for(int i = 0; i < count; i ++)
        {
            float value = datas[i];
            float percent = value / maxValue;
            float a = angle * i;
            float x = (float) (centerX + radius * percent * Math.cos(a));
            float y = (float) (centerY + radius * percent * Math.sin(a));
            valuePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x, y, 10, valuePaint);
            if(i == 0)
                path.moveTo(x, y);
            else
                path.lineTo(x, y);
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        valuePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, valuePaint);
    }


    private void drawText(Canvas canvas)
    {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float height = fontMetrics.descent - fontMetrics.ascent;
        for(int i = 0; i < count; i ++)
        {
            float a = angle * i;
            float x = (float) (centerX + (radius + height / 2) * Math.cos(a));
            float y = (float) (centerY + (radius + height / 2) * Math.sin(a));
            String title = titles[i];
            if(a >= Math.PI / 2 && a <= Math.PI * 3 / 2) {
                float dis = textPaint.measureText(title);
                canvas.drawText(title, x - dis, y, textPaint);
            }
            else
                canvas.drawText(title, x, y, textPaint);
        }
    }

    private void drawLines(Canvas canvas)
    {
        Path path = new Path();
        for(int i = 0; i < count; i ++)
        {
            path.reset();
            path.moveTo(centerX, centerY);
            float a = angle * i;
            float x = (float) (centerX + Math.cos(a) * radius);
            float y = (float) (centerY + Math.sin(a) * radius);
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    private int count = 6;
    private float angle;
    private int centerX,centerY;
    private int radius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = (int) (Math.min(w, h) / 2 * 0.9);
        centerX = w / 2;
        centerY = h / 2;
    }

    private void drawPolygon(Canvas canvas)
    {
        angle = (float) (2 * Math.PI / count);
        float r = radius / (count - 1);
        Path path = new Path();
        for(int i = 1; i < count; i ++)
        {
            float currentRadius = r * i;
            path.reset();
            for(int j = 0; j < count; j ++)
            {
                float a = angle * j;
                float x = (float) (centerX + Math.cos(a) * currentRadius);
                float y = (float) (centerY + Math.sin(a) * currentRadius);
                if(j == 0)
                    path.moveTo(x, y);
                else
                    path.lineTo(x, y);
            }
            path.close();
            canvas.drawPath(path, mainPaint);
        }
    }
}
