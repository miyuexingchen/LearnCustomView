package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.SumPathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/15.
 */

public class C22_PaintPathEffectCustomView extends View {
    public C22_PaintPathEffectCustomView(Context context) {
        this(context, null);
    }

    public C22_PaintPathEffectCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Paint mTextPaint, mPathPaint;
    private Path mPath;
    private PathEffect[] mPathEffect = new PathEffect[7];
    private String[] mPathEffectName = {
            "PathEffect", "CornerPathEffect", "DashPathEffect",
            "PathDashPathEffect", "SumPathEffect", "DiscretePathEffect",
            "ComposePathEffect",
    };
    private void init()
    {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(40f);

        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStrokeWidth(10);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setColor(getResources().getColor(R.color.colorAccent));

        mPath = new Path();
        mPath.moveTo(3, 3);
        for(int i = 1; i < 37; i ++)
            mPath.lineTo(i * 30, (float) (Math.random() * 100));

        mPathEffect[0] = new PathEffect();
        mPathEffect[1] = new CornerPathEffect(10f);
        mPathEffect[2] = new DashPathEffect(new float[]{10, 5, 20, 15}, 10);
        mPathEffect[3] = new PathDashPathEffect(new Path(), 10, 10, PathDashPathEffect.Style.ROTATE);
        mPathEffect[4] = new SumPathEffect(mPathEffect[1], mPathEffect[3]);
        mPathEffect[5] = new DiscretePathEffect(3, 3);
        mPathEffect[6] = new ComposePathEffect(mPathEffect[4], mPathEffect[5]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.colorPrimary));
        canvas.translate(0, 120);
        for(int i = 0; i < 7; i ++)
        {
            canvas.drawText(mPathEffectName[i], 10, 30, mTextPaint);
            canvas.translate(0, 60);
            mPathPaint.setPathEffect(mPathEffect[i]);
            canvas.drawPath(mPath, mPathPaint);
            canvas.translate(0, 150);
        }
    }
}
