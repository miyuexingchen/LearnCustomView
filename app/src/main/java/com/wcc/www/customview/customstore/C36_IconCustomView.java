package com.wcc.www.customview.customstore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/26.
 */

public class C36_IconCustomView extends View {

    private Bitmap bitmap;
    private TextPaint mTextPaint;
    private String text;
    private float textSize;
    private enum Ratio{
        WIDTH, HEIGHT
    }
    public C36_IconCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        calcArgs(context);

        init();
    }

    private void calcArgs(Context ctx)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        textSize = metrics.widthPixels / 10f;
    }

    private void init()
    {
        if(null == bitmap)
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.p1);
        if(TextUtils.isEmpty(text))
            text = "YZZQSNSD";

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTextPaint.setColor(Color.LTGRAY);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasuredSize(widthMeasureSpec, Ratio.WIDTH),
                getMeasuredSize(heightMeasureSpec, Ratio.HEIGHT));
    }

    private int getMeasuredSize(int spec, Ratio ratio)
    {
        int resultSize = 0;

        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);

        switch (specMode)
        {
            case MeasureSpec.EXACTLY:
                System.out.println("exactly");
                resultSize = spec;
                break;

            default:
                if(ratio == Ratio.WIDTH)
                {
                    float textWidth = mTextPaint.measureText(text);
                    resultSize = (int) ((textWidth > bitmap.getWidth() ? textWidth : bitmap.getWidth()) + getPaddingLeft() + getPaddingRight());
                }else
                    resultSize = (int) (mTextPaint.descent() - mTextPaint.ascent() + bitmap.getHeight() + getPaddingBottom() + getPaddingTop());

                if(specMode == MeasureSpec.AT_MOST) {
                    System.out.println("at_most");
                    resultSize = Math.min(resultSize, specSize);
                }else
                    System.out.println("unspecified");
                break;
        }

        return resultSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2, (getHeight() - bitmap.getHeight()) / 2, null);
        canvas.drawText(text, getWidth() / 2, (getHeight() + bitmap.getHeight()) / 2 - mTextPaint.ascent() , mTextPaint);
    }
}
