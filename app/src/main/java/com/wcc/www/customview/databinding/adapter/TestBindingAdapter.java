package com.wcc.www.customview.databinding.adapter;

import android.graphics.Color;
import android.widget.TextView;

/**
 * Created by 王晨晨 on 2017/11/2.
 */

public class TestBindingAdapter extends MyBindingAdapter {
    @Override
    public void setText(TextView view, String value) {
        view.setText(value + " test");
    }

    @Override
    public void setTextColor(TextView view, int value) {
        if(value == Color.parseColor("#ff0000"))
            view.setTextColor(Color.parseColor("#00ff00"));
    }
}
