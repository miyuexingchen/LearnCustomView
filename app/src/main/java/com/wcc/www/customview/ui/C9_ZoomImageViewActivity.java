package com.wcc.www.customview.ui;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wcc.www.customview.R;
import com.wcc.www.customview.custom.C9_ZoomImageCustomView;

public class C9_ZoomImageViewActivity extends AppCompatActivity {

    private ViewPager viewPager;


    private int[] ids = {
            R.mipmap.p1,
            R.mipmap.p2,
            R.mipmap.p3,
    };

    private ImageView[] ivs = new ImageView[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c9__zoom_image_view);

        viewPager = (ViewPager) findViewById(R.id.vp);

        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                C9_ZoomImageCustomView iv = new C9_ZoomImageCustomView(getApplicationContext());
                iv.setImageResource(ids[position]);
                container.addView(iv);
                ivs[position] = iv;
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
        });
    }
}
