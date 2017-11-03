package com.wcc.www.customview.databinding.component;

import android.databinding.DataBindingComponent;

import com.wcc.www.customview.databinding.adapter.MyBindingAdapter;
import com.wcc.www.customview.databinding.adapter.TestBindingAdapter;

/**
 * Created by 王晨晨 on 2017/11/2.
 */

public class TestComponent implements android.databinding.DataBindingComponent {

    private TestBindingAdapter adapter = new TestBindingAdapter();
    @Override
    public MyBindingAdapter getMyBindingAdapter() {
        return adapter;
    }
}
