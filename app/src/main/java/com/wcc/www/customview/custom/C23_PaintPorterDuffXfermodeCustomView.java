package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 王晨晨 on 2017/9/15.
 */

public class C23_PaintPorterDuffXfermodeCustomView extends View {
    public C23_PaintPorterDuffXfermodeCustomView(Context context) {
        this(context, null);
    }

    private Paint mPaint, mTextPaint, mRectPaint;
    public C23_PaintPorterDuffXfermodeCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeWidth(1);

        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(Color.BLACK);
        mRectPaint.setTextAlign(Paint.Align.CENTER);
        mRectPaint.setStrokeWidth(1);
    }

    private Bitmap makeDst(int l)
    {
        Bitmap bitmap = Bitmap.createBitmap(l, l, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int r = l / 2;
        Paint p = new Paint();
        p.setColor(0xFFFFCC44);
        canvas.drawCircle(r, r, r, p);
        return bitmap;
    }

    private Bitmap makeSrc(int l)
    {
        Bitmap bitmap = Bitmap.createBitmap(l, l, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setColor(0xFF66AAFF);
        canvas.drawRect(0, 0, l, l, p);
        return bitmap;
    }

    private PorterDuff.Mode[] modes = {
            PorterDuff.Mode.CLEAR, PorterDuff.Mode.SRC, PorterDuff.Mode.DST, PorterDuff.Mode.SRC_OVER,
            PorterDuff.Mode.DST_OVER, PorterDuff.Mode.SRC_IN, PorterDuff.Mode.DST_IN, PorterDuff.Mode.SRC_OUT,
            PorterDuff.Mode.DST_OUT, PorterDuff.Mode.SRC_ATOP, PorterDuff.Mode.DST_ATOP, PorterDuff.Mode.XOR,
            PorterDuff.Mode.DARKEN, PorterDuff.Mode.LIGHTEN, PorterDuff.Mode.MULTIPLY, PorterDuff.Mode.SCREEN,
            PorterDuff.Mode.ADD, PorterDuff.Mode.OVERLAY,
    };

    private String[] modenames = {
            "CLEAR", "SRC", "DST", "SRC_OVER",
            "DST_OVER", "SRC_IN", "DST_IN", "SRC_OUT",
            "DST_OUT", "SRC_ATOP", "DST_ATOP", "XOR",
            "DARKEN", "LIGHTEN", "MULTIPLY", "SCREEN",
            "ADD", "OVERLAY",
    };
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(255, 139, 197, 186);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int rectWidth = (canvasWidth - 180) / 4;
        int rowspace = 50;
        int colspace = 60;

        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        canvas.translate(0, rowspace);
        for(int i = 0; i < modenames.length; i ++)
        {
            int mod = i % 4;
            if(mod == 0)
                canvas.translate(0, rowspace);
            int increment = mod * (colspace + rectWidth);
            int textCenterX = rectWidth / 2 + increment;
            int textCenterY;
            if(mod == 0)
                textCenterY = (int) (rowspace / 2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2) - rowspace;
            else
                textCenterY = (int) (rowspace / 2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2) - rowspace - rectWidth;

            canvas.drawText(modenames[i], textCenterX, textCenterY, mTextPaint);
            if(mod == 0)
                canvas.translate(0, rectWidth);
            RectF rectF = new RectF(increment, -rectWidth, increment + rectWidth, 0);
            canvas.drawRect(rectF, mRectPaint);
            int l = rectWidth * 2 / 3;
            canvas.drawBitmap(makeDst(l), increment + 5, -rectWidth + 5, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(modes[i]));
            canvas.drawBitmap(makeSrc((int) (l * 0.9)), increment + l / 2 + 5, -rectWidth + l / 2 + 5, mPaint);
            mPaint.setXfermode(null);
        }
        canvas.restoreToCount(layerId);

        /*int canvasWidth = canvas.getWidth();
        int width = getWidth();
        int canvasHeight = canvas.getHeight();
        int rectWidth = (canvasWidth - 180) / 4;
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        System.out.println("canvasWidth "+canvasWidth+", width "+width);
        System.out.println(canvasHeight);
        int r = canvasWidth / 3;
        mPaint.setColor(0xFFFFCC44);
        canvas.drawCircle(r, r, r, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setColor(0xFF66AAFF);
        canvas.drawRect(r, r, r * 2.7f, r * 2.7f, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(layerId);*/
    }
}
