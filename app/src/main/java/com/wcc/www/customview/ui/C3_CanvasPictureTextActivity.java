package com.wcc.www.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C3_CanvasPictureTextCustomView;

public class C3_CanvasPictureTextActivity extends AppCompatActivity {

    private C3_CanvasPictureTextCustomView c3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c3__canvas_picture_text);

        c3 = (C3_CanvasPictureTextCustomView) findViewById(R.id.c3);
    }

    public void check(View view)
    {
        c3.check();
    }

    public void uncheck(View view)
    {
        c3.uncheck();
    }
}
