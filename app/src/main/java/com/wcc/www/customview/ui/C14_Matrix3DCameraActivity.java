package com.wcc.www.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C19_Rotate3DAnimation;

public class C14_Matrix3DCameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c14__matrix3_dcamera);

        ImageView iv = (ImageView) findViewById(R.id.c19);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float centerX = v.getWidth() / 2f;
                float centerY = v.getHeight() / 2f;
                C19_Rotate3DAnimation rAnimation = new C19_Rotate3DAnimation(C14_Matrix3DCameraActivity.this, -360, 360, centerX, centerY, 0f, true);
                rAnimation.setDuration(2000);
                rAnimation.setFillAfter(true);
                rAnimation.setInterpolator(new LinearInterpolator());
                v.startAnimation(rAnimation);
            }
        });
    }
}
