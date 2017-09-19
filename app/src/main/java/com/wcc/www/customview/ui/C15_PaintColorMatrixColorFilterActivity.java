package com.wcc.www.customview.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.wcc.www.customview.R;

public class C15_PaintColorMatrixColorFilterActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private ImageView c20;
    private SeekBar seek_rotate;
    private SeekBar seek_saturation;
    private SeekBar seek_scale;
    private Bitmap mBitmap;
    private float hue, saturation, lum, value = 100f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c15__paint_color_matrix_color_filter);

        c20 = (ImageView) findViewById(R.id.c20);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.p1);
        c20.setImageBitmap(mBitmap);
        seek_rotate = (SeekBar) findViewById(R.id.seek_rotate);
        seek_saturation = (SeekBar) findViewById(R.id.seek_saturation);
        seek_scale = (SeekBar) findViewById(R.id.seek_scale);

        seek_rotate.setOnSeekBarChangeListener(this);
        seek_saturation.setOnSeekBarChangeListener(this);
        seek_scale.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId())
        {
            case R.id.seek_rotate:
                hue = progress * 1.0f / value * 360f;
                break;

            case R.id.seek_saturation:
                saturation = progress * 1.0f / value;
                break;

            case R.id.seek_scale:
                lum = progress * 1.0f / value;
                break;
        }

        System.out.println(progress);
        System.out.println(hue+", "+saturation+", "+lum);
        c20.setImageBitmap(createBitmap(mBitmap, hue, saturation, lum));
    }

    private Bitmap createBitmap(Bitmap mBitmap, float hue, float saturation, float lum) {
        Bitmap b = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Paint p = new Paint();

        ColorMatrix rotateMatrix = new ColorMatrix();
        rotateMatrix.setRotate(0, hue);
        rotateMatrix.setRotate(1, hue);
        rotateMatrix.setRotate(2, hue);

        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        ColorMatrix scaleMatrix = new ColorMatrix();
        scaleMatrix.setScale(lum, lum, lum, 1);

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.postConcat(rotateMatrix);
        colorMatrix.postConcat(saturationMatrix);
        colorMatrix.postConcat(scaleMatrix);

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        p.setColorFilter(colorFilter);
        c.drawBitmap(mBitmap, 0, 0, p);

        return b;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
