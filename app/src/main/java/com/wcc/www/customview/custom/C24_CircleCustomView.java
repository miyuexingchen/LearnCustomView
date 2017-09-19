package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by 王晨晨 on 2017/9/15.
 */

public class C24_CircleCustomView extends ImageView {

    private Paint mPaint;

    public C24_CircleCustomView(Context context) {
        this(context, null);
    }

    public C24_CircleCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        BitmapDrawable d = (BitmapDrawable) getDrawable();
        if(d != null)
        {
            Bitmap bitmap = d.getBitmap();
            int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            float x = getWidth() / 2;
            float y = getHeight() / 2;
            float r = Math.min(x, y);
            canvas.drawCircle(x, y, r, mPaint);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            float rx = (getWidth() - bitmap.getWidth()) / 2;
            float ry = (getHeight() - bitmap.getHeight()) / 2;
            canvas.drawBitmap(bitmap, rx, ry, mPaint);
            mPaint.setXfermode(null);
            canvas.restoreToCount(layerId);
        }
    }
}
