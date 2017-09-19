package com.wcc.www.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C4_PathBasicCustomView;

public class C4_PathBasicActivity extends AppCompatActivity {

    private C4_PathBasicCustomView c4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c4__path_basic);

        c4 = (C4_PathBasicCustomView) findViewById(R.id.c4);
    }
}
