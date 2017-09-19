package com.wcc.www.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C8_MatrixBasicCustomView;

public class C8_MatrixBasicActivity extends AppCompatActivity {

    private C8_MatrixBasicCustomView c8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c8__matrix_basic);

        c8 = (C8_MatrixBasicCustomView) findViewById(R.id.c8);
        c8.setCount(5).setLayer(5);
    }
}
