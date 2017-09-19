package com.wcc.www.customview.ui;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C10_ZoomImageViewExeciseCustomView;

public class C10_ZoomImageViewExeciseActivity extends AppCompatActivity {

    private ViewPager vp;
    private int[] ids = {R.mipmap.p1, R.mipmap.p2, R.mipmap.p3,};
    private ImageView[] ivs = new ImageView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c10__zoom_image_view_execise);



        /*vp = (ViewPager) findViewById(R.id.c10);
        vp.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                C10_ZoomImageViewExeciseCustomView iv = new C10_ZoomImageViewExeciseCustomView(getApplicationContext());
                iv.setImageResource(ids[position]);
                ivs[position] = iv;
                container.addView(iv);
                return iv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(ivs[position]);
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });*/
    }
}
