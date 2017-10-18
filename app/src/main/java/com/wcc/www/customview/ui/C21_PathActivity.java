package com.wcc.www.customview.ui;

import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.customstore.C26_PathCustomView;

import java.util.ArrayList;
import java.util.List;

public class C21_PathActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c21__path);

        C26_PathCustomView mPolylineView = (C26_PathCustomView) findViewById(R.id.c26);

        List<PointF> pointFs = new ArrayList<PointF>();
        pointFs.add(new PointF(0.3F, 0.5F));
        pointFs.add(new PointF(1F, 2.7F));
        pointFs.add(new PointF(2F, 3.5F));
        pointFs.add(new PointF(3F, 3.2F));
        pointFs.add(new PointF(4F, 1.8F));
        pointFs.add(new PointF(5F, 1.5F));
        pointFs.add(new PointF(6F, 2.2F));
        pointFs.add(new PointF(7F, 5.5F));
        pointFs.add(new PointF(8F, 7F));
        pointFs.add(new PointF(8.6F, 5.7F));

        mPolylineView.setData(pointFs, "Money", "Time");
    }
}
