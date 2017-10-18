package com.wcc.www.customview.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.customstore.C35_MeasureImageCustomView;

public class C30_MeasureImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c30__measure_image);

        C35_MeasureImageCustomView measureImageCustomView = (C35_MeasureImageCustomView) findViewById(R.id.c35);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.a);
        measureImageCustomView.setBitmap(bitmap);
    }
}
