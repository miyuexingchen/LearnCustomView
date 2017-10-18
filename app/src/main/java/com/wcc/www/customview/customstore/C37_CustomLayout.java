package com.wcc.www.customview.customstore;

import android.content.Context;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 王晨晨 on 2017/9/26.
 */

public class C37_CustomLayout extends ViewGroup {

    public C37_CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentDesireWidth = 0, parentDesireHeight = 0;

        int childCount = getChildCount();
        if(childCount > 0)
        {
            for(int i = 0; i < childCount; i ++)
            {
                View child = getChildAt(i);
                CustomLayoutParams clp = (CustomLayoutParams) child.getLayoutParams();
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                parentDesireWidth += child.getMeasuredWidth() + clp.leftMargin + clp.rightMargin;
                parentDesireHeight += child.getMeasuredHeight() + clp.topMargin + clp.bottomMargin;
            }

            parentDesireWidth += getPaddingLeft() + getPaddingRight();
            parentDesireHeight += getPaddingBottom() + getPaddingTop();

            parentDesireWidth = Math.max(parentDesireWidth, getSuggestedMinimumWidth());
            parentDesireHeight = Math.max(parentDesireHeight, getSuggestedMinimumHeight());
        }

        setMeasuredDimension(resolveSize(parentDesireWidth, widthMeasureSpec),
                resolveSize(parentDesireHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int parentPaddingLeft = getPaddingLeft();
        int parentPaddingTop = getPaddingTop();

        int childCount = getChildCount();
        if(childCount > 0)
        {
            int multiHeight = 0;
            for(int i = 0; i < childCount; i ++)
            {
                View view = getChildAt(i);
                view.layout(parentPaddingLeft, multiHeight + parentPaddingTop, view.getMeasuredWidth() + parentPaddingLeft, view.getMeasuredHeight() + multiHeight + parentPaddingTop);
                multiHeight += view.getMeasuredHeight();
            }
        }
    }

    public static class CustomLayoutParams extends MarginLayoutParams{

        public CustomLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public CustomLayoutParams(@Px int width, @Px int height) {
            super(width, height);
        }

        public CustomLayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public CustomLayoutParams(LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new CustomLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new CustomLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CustomLayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof CustomLayoutParams;
    }
}
