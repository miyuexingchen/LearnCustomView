package com.wcc.www.customview.databinding.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wcc.www.customview.R;
import com.wcc.www.customview.databinding.ActivityD4ComponentBinding;
import com.wcc.www.customview.databinding.component.ProductionComponent;
import com.wcc.www.customview.databinding.component.TestComponent;

public class D4_ComponentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setDefaultComponent(new TestComponent());
        ActivityD4ComponentBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_d4__component);
    }
}
