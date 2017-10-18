package com.wcc.www.customview.ui;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.wcc.www.customview.R;
import com.wcc.www.customview.customstore.C11_MatrixMethodCustomView;

public class C11_MatrixMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c11__matrix_method);

        final C11_MatrixMethodCustomView c11 = (C11_MatrixMethodCustomView) findViewById(R.id.c11);
        RadioGroup group = (RadioGroup) findViewById(R.id.group);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (group.getCheckedRadioButtonId())
                {
                    case R.id.r1: c11.setPointCount(0);break;
                    case R.id.r2: c11.setPointCount(1);break;
                    case R.id.r3: c11.setPointCount(2);break;
                    case R.id.r4: c11.setPointCount(3);break;
                    case R.id.r5: c11.setPointCount(4);break;
                }
            }
        });
    }
}
