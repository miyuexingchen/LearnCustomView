package com.wcc.www.customview.custom;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 王晨晨 on 2017/9/13.
 */

public class C15_FoldSlidingPaneLayout extends SlidingPaneLayout {

    public C15_FoldSlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        View view = getChildAt(0);
        if(view != null)
        {
            /*removeView(view);
//            final C14_MatrixMethodCustomView layout = new C14_MatrixMethodCustomView(getContext());
            final C17_CustomViewExercise layout = new C17_CustomViewExercise(getContext());
            layout.addView(view);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            addView(layout, 0, layoutParams);

            setPanelSlideListener(new PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
//                    layout.setFoldPercentage(slideOffset);
                    layout.setPercentage(slideOffset);
                }

                @Override
                public void onPanelOpened(View panel) {

                }

                @Override
                public void onPanelClosed(View panel) {

                }
            });*/
        }
    }
}
