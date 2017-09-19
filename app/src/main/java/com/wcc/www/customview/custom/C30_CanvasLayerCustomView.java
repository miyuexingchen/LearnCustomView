package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/19.
 */

public class C30_CanvasLayerCustomView extends View {
    public C30_CanvasLayerCustomView(Context context) {
        this(context, null);
    }

    private Paint mPaint;
    private Bitmap mBitmap;
    public C30_CanvasLayerCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tanyan);
    }

    private int width, height;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        mBitmap = Bitmap.createScaledBitmap(mBitmap, w, h, true);
    }

    int i = 0;
    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
//        canvas.scale(1, 1);
//        canvas.scale(0.8f, 0.35f, width, 0);
        Matrix matrix = new Matrix();
        matrix.setScale(0.8f, 0.35f);
        matrix.postTranslate(-100, 100);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.restore();

        /*mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        canvas.drawRect(centerX - 200, centerY - 200, centerX + 200, centerY + 200, mPaint);

        canvas.saveLayer(centerX - 100, centerY - 100, centerX + 100, centerY + 100, null,
                Canvas.ALL_SAVE_FLAG);
//        canvas.save();
        mPaint.setColor(Color.BLUE);
        canvas.rotate(i);
        canvas.drawRect(centerX - 100, centerY - 100, centerX + 100, centerY + 100, mPaint);
        canvas.restore();
        i ++;
        if(i < 30)
            postInvalidateDelayed(100);*/
    }
}
