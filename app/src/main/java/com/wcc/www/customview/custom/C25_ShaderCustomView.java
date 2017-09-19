package com.wcc.www.customview.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wcc.www.customview.R;

/**
 * Created by 王晨晨 on 2017/9/15.
 */

public class C25_ShaderCustomView extends View {
    public C25_ShaderCustomView(Context context) {
        this(context, null);
    }

    private Paint mPaint;
    public C25_ShaderCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.p1);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.REPEAT);
        mPaint.setShader(shader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float x = getWidth() / 2;
        float y = getHeight() / 2;
//        float radius = Math.min(x, y);
//        canvas.drawCircle(350, 250, 200, mPaint);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }
}
