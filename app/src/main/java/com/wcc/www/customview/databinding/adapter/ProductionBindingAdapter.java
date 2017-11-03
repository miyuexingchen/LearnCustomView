package com.wcc.www.customview.databinding.adapter;

import android.databinding.adapters.TextViewBindingAdapter;
import android.widget.TextView;

/**
 * Created by 王晨晨 on 2017/11/2.
 */

public class ProductionBindingAdapter extends MyBindingAdapter {
    @Override
    public void setText(TextView view, String value) {
        TextViewBindingAdapter.setText(view, value);
    }

    @Override
    public void setTextColor(TextView view, int value) {
        view.setTextColor(value);
    }
}
