package com.wcc.www.customview.databinding.adapter;

import android.databinding.BindingAdapter;
import android.widget.TextView;

/**
 * Created by 王晨晨 on 2017/11/2.
 */

public abstract class MyBindingAdapter {

    @BindingAdapter("android:text")
    public abstract void setText(TextView view, String value);

    @BindingAdapter("android:textColor")
    public abstract void setTextColor(TextView view, int value);
}
