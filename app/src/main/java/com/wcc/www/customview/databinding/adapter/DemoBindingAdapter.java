package com.wcc.www.customview.databinding.adapter;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.URL;

/**
 * Created by 王晨晨 on 2017/11/1.
 */

public class DemoBindingAdapter {

    @BindingAdapter({"app:imageUrl", "app:placeholder"})
    public static void loadImageFromUrl(ImageView imageView, String url, Drawable drawable)
    {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
        System.out.println("do "+url);
        System.out.println(imageView);
    }
}
