package com.wcc.www.customview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.customstore.C1_CanvasBasicGraphicsCustomView;

import java.util.ArrayList;

public class C1_CanvasBasicGraphicsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c1__canvas_basic_graphics);

        C1_CanvasBasicGraphicsCustomView c1 = (C1_CanvasBasicGraphicsCustomView) findViewById(R.id.c1);
        ArrayList<C1_CanvasBasicGraphicsCustomView.PieData> datas = new ArrayList<>();
        datas.add(new C1_CanvasBasicGraphicsCustomView.PieData(1));
        datas.add(new C1_CanvasBasicGraphicsCustomView.PieData(2));
        datas.add(new C1_CanvasBasicGraphicsCustomView.PieData(3));
        datas.add(new C1_CanvasBasicGraphicsCustomView.PieData(6));
        datas.add(new C1_CanvasBasicGraphicsCustomView.PieData(3));
        datas.add(new C1_CanvasBasicGraphicsCustomView.PieData(4));
        datas.add(new C1_CanvasBasicGraphicsCustomView.PieData(6));
        datas.add(new C1_CanvasBasicGraphicsCustomView.PieData(9));
        c1.setDatas(datas);
    }
}
