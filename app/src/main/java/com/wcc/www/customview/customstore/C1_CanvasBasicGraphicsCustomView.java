package com.wcc.www.customview.customstore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by 王晨晨 on 2017/9/8.
 */

public class C1_CanvasBasicGraphicsCustomView extends View {

    public C1_CanvasBasicGraphicsCustomView(Context context) {
        this(context, null);
    }

    public C1_CanvasBasicGraphicsCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private Paint mPaint;
    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    public static class PieData{
        public float value;
        public float percent;
        public float angle;
        public int color;

        public PieData(float value)
        {
            this.value = value;
        }
    }
    private ArrayList<PieData> mDatas;
    private int centerX, centerY;
    private float pieRadius;
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};
    public void setStartAngle(float angle)
    {
        mStartAngle = angle;
    }

    public void setDatas(ArrayList<PieData> datas)
    {
        mDatas = datas;
        initDatas(mDatas);
    }

    private void initDatas(ArrayList<PieData> datas)
    {
        if(datas == null || datas.size() == 0)
            return;

        float sumValue = 0;
        int size = datas.size();
        for(int i = 0; i < size; i ++)
        {
            PieData pieData = datas.get(i);
            sumValue += pieData.value;
            int j = i % mColors.length;
            pieData.color = mColors[j];
        }

        for(int i = 0; i < size; i ++)
        {
            PieData pieData = datas.get(i);
            pieData.percent = pieData.value / sumValue;
            pieData.angle = 360 * pieData.percent;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        pieRadius = (float) (Math.min(w, h) / 2 * 0.8);
    }

    private float mCurrentAngle, mStartAngle;
    @Override
    protected void onDraw(Canvas canvas) {
        if(mDatas == null)
            return;

        mCurrentAngle = mStartAngle;
        int size = mDatas.size();
        canvas.translate(centerX, centerY);
        RectF rectF = new RectF(-pieRadius, -pieRadius, pieRadius, pieRadius);
        for(int i = 0; i < size; i ++)
        {
            PieData pieData = mDatas.get(i);
            mPaint.setColor(pieData.color);
            canvas.drawArc(rectF, mCurrentAngle, pieData.angle, true, mPaint);
            mCurrentAngle += pieData.angle;
        }

    }
}
