package com.wcc.www.customview.databinding.component;

import android.databinding.DataBindingComponent;

import com.wcc.www.customview.databinding.adapter.MyBindingAdapter;
import com.wcc.www.customview.databinding.adapter.ProductionBindingAdapter;

/**
 * Created by 王晨晨 on 2017/11/2.
 */

public class ProductionComponent implements android.databinding.DataBindingComponent {

    private ProductionBindingAdapter adapter = new ProductionBindingAdapter();
    @Override
    public MyBindingAdapter getMyBindingAdapter() {
        return adapter;
    }
}
