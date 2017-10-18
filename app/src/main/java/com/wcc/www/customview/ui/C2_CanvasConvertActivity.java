package com.wcc.www.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.customstore.C2_CanvasConvertCustomView;

public class C2_CanvasConvertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c2__canvas_convert);

        C2_CanvasConvertCustomView c2 = (C2_CanvasConvertCustomView) findViewById(R.id.c2);
    }
}
