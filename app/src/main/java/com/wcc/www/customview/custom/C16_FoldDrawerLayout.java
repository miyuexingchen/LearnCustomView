package com.wcc.www.customview.custom;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 王晨晨 on 2017/9/13.
 */

public class C16_FoldDrawerLayout extends DrawerLayout {
    public C16_FoldDrawerLayout(Context context) {
        this(context, null);
    }

    public C16_FoldDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final int childCount = getChildCount();

        for(int i = 0; i < childCount; i ++)
        {
            View view = getChildAt(i);
            if(isDrawerView2(view))
            {
                C14_MatrixMethodCustomView layout = new C14_MatrixMethodCustomView(getContext());
                layout.setAnchor(1);
                removeView(view);
                layout.addView(view);
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                addView(layout, i, layoutParams);
            }
        }

        setDrawerListener(new DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(drawerView instanceof C14_MatrixMethodCustomView)
                {
                    C14_MatrixMethodCustomView layout = (C14_MatrixMethodCustomView) drawerView;
                    layout.setFoldPercentage(slideOffset);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private boolean isDrawerView2(View view)
    {
        int gravity = ((LayoutParams) view.getLayoutParams()).gravity;
        int absGravity = GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(view));
        return (absGravity & (Gravity.LEFT | Gravity.RIGHT)) != 0;
    }
}
