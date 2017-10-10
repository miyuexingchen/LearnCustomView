package com.wcc.www.customview.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 王晨晨 on 2017/9/26.
 */

public class C38_SquareLayout extends ViewGroup {
    private static final int ORIENTATION_HORIZONTAL = 0, ORIENTATION_VERTICAL = 1;
    private static final int MAX_ROW = Integer.MAX_VALUE, MAX_COL = Integer.MIN_VALUE;
    private int mMaxRow = MAX_ROW, mMaxCol = MAX_COL, mOrientation = ORIENTATION_HORIZONTAL;
    public C38_SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mMaxRow = mMaxCol = 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentDesireWidth = 0, parentDesireHeight = 0;
//        int childMeasureState = 0;

        int childCount = getChildCount();
        if(childCount > 0)
        {
            int[] childWidths = new int[childCount];
            int[] childHeights = new int[childCount];
            for(int i = 0; i < childCount; i ++)
            {
                View child = getChildAt(i);
                if(child.getVisibility() != GONE)
                {
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    int childMeasureSize = Math.max(child.getMeasuredWidth(), child.getMeasuredHeight());
                    int childMeasureSpec = MeasureSpec.makeMeasureSpec(childMeasureSize, MeasureSpec.EXACTLY);

                    child.measure(childMeasureSpec, childMeasureSpec);
                    MarginLayoutParams clp = (MarginLayoutParams) child.getLayoutParams();
                    childWidths[i] = child.getMeasuredWidth() + clp.leftMargin + clp.rightMargin;
                    childHeights[i] = child.getMeasuredHeight() + clp.topMargin + clp.bottomMargin;
//                    int childActualWidth = child.getMeasuredWidth() + clp.leftMargin + clp.rightMargin;
//                    int childActualHeight = child.getMeasuredHeight() + clp.topMargin + clp.bottomMargin;
//
//                    if(mOrientation == ORIENTATION_HORIZONTAL)
//                    {
//                        parentDesireWidth += childActualWidth;
//                        parentDesireHeight = Math.max(parentDesireHeight, childActualHeight);
//                    }else {
//                        parentDesireHeight += childActualHeight;
//                        parentDesireWidth = Math.max(parentDesireWidth, childActualWidth);
//                    }

//                    childMeasureState = combineMeasuredStates(childMeasureState, child.getMeasuredState());
                }
            }

            int indexMultiWidth = 0, indexMultiHeight = 0;
            if(mOrientation == ORIENTATION_HORIZONTAL)
            {
                if(childCount > mMaxCol)
                {
                    int row = childCount / mMaxCol;
                    int remainder = childCount % mMaxCol;
                    int index = 0;
                    for(int x = 0; x < row; x ++)
                    {
                        for(int y = 0; y < mMaxCol; y ++)
                        {
                            indexMultiWidth += childWidths[index];
                            indexMultiHeight = Math.max(indexMultiHeight, childHeights[index ++]);
                        }

                        parentDesireWidth = Math.max(indexMultiWidth, parentDesireWidth);
                        parentDesireHeight += indexMultiHeight;
                        indexMultiWidth = indexMultiHeight = 0;
                    }

                    if(remainder != 0)
                    {
                        for(int i = childCount - remainder; i < childCount; i ++)
                        {
                            indexMultiWidth += childWidths[index];
                            indexMultiHeight = Math.max(indexMultiHeight, childHeights[index ++]);
                        }

                        parentDesireWidth = Math.max(indexMultiWidth, parentDesireWidth);
                        parentDesireHeight += indexMultiHeight;
                    }
                }else{
                    for(int i = 0; i < childCount; i ++)
                    {
                        indexMultiWidth += childWidths[i];
                        indexMultiHeight = Math.max(indexMultiHeight, childHeights[i]);
                    }

                    parentDesireWidth = indexMultiWidth;
                    parentDesireHeight = indexMultiHeight;
                }
            }else{
                if(childCount > mMaxRow)
                {
                    int col = childCount / mMaxRow;
                    int remainder = childCount % mMaxRow;
                    int index = 0;
                    for(int x = 0; x < col; x ++)
                    {
                        for(int y = 0; y < mMaxRow; y ++)
                        {
                            indexMultiWidth = Math.max(indexMultiWidth, childWidths[index]);
                            indexMultiHeight += childHeights[index ++];
                        }

                        parentDesireHeight = Math.max(parentDesireHeight, indexMultiHeight);
                        parentDesireWidth += indexMultiWidth;
                        indexMultiWidth = indexMultiHeight = 0;
                    }

                    if(remainder != 0)
                    {
                        for(int i = childCount - remainder; i < childCount; i ++)
                        {
                            indexMultiHeight += childHeights[index];
                            indexMultiWidth = Math.max(indexMultiWidth, childWidths[index ++]);
                        }

                        parentDesireHeight = Math.max(indexMultiHeight, parentDesireHeight);
                        parentDesireWidth += indexMultiHeight;
                    }
                }else{
                    for(int i = 0; i < childCount; i ++)
                    {
                        indexMultiHeight += childHeights[i];
                        indexMultiWidth = Math.max(indexMultiWidth, childWidths[i]);
                    }

                    parentDesireHeight = indexMultiHeight;
                    parentDesireWidth = indexMultiWidth;
                }
            }

            parentDesireWidth += getPaddingLeft() + getPaddingRight();
            parentDesireHeight += getPaddingBottom() + getPaddingTop();

            parentDesireWidth = Math.max(parentDesireWidth, getSuggestedMinimumWidth());
            parentDesireHeight = Math.max(parentDesireHeight, getSuggestedMinimumHeight());
        }

        setMeasuredDimension(resolveSize(parentDesireWidth, widthMeasureSpec),
                resolveSize(parentDesireHeight, heightMeasureSpec));
//        setMeasuredDimension(resolveSizeAndState(parentDesireWidth, widthMeasureSpec, childMeasureState),
//                resolveSizeAndState(parentDesireHeight, heightMeasureSpec, childMeasureState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        int sum = 0;
        for(int i = 0; i < childCount; i ++)
        {
            if(getChildAt(i).getVisibility() != GONE)
                sum ++;
        }
        if(sum > 0)
        {
            int multi = 0;
            int indexMulti = 1, indexMultiWidth = 0, indexMultiHeight = 0, tempWidth = 0, tempHeight = 0,
                    index = -1;
            for(int i = 0; i < childCount; i ++) {
                View child = getChildAt(i);
                if (child.getVisibility() != GONE)
                {
                    ++ index;
                    MarginLayoutParams clp = (MarginLayoutParams) child.getLayoutParams();
                    int childActualSize = child.getMeasuredHeight();

                    if(mOrientation == ORIENTATION_HORIZONTAL)
                    {
                        if(sum <= mMaxCol)
                        {
                            child.layout(getPaddingLeft() + multi + clp.leftMargin,
                                    getPaddingTop() + clp.topMargin,
                                    getPaddingLeft() + multi + clp.leftMargin + childActualSize,
                                    getPaddingTop() + clp.topMargin + childActualSize);
                            multi += childActualSize + clp.leftMargin + clp.rightMargin;
                        }else{
                            /*
                             * 根据当前子元素进行布局
                             */
                            if (index < mMaxCol * indexMulti) {
                                child.layout(getPaddingLeft() + clp.leftMargin + indexMultiWidth, getPaddingTop() + clp.topMargin + indexMultiHeight,
                                        childActualSize + getPaddingLeft() + clp.leftMargin + indexMultiWidth, childActualSize + getPaddingTop()
                                                + clp.topMargin + indexMultiHeight);
                                indexMultiWidth += childActualSize + clp.leftMargin + clp.rightMargin;
                                tempHeight = Math.max(tempHeight, childActualSize + clp.topMargin + clp.bottomMargin);

                                /*
                                 * 如果下一次遍历到的子元素下标值大于限定值
                                 */
                                if (index + 1 >= mMaxCol * indexMulti) {
                                    // 那么累加高度到高度倍增值
                                    indexMultiHeight += tempHeight;

                                    // 重置宽度倍增值
                                    indexMultiWidth = 0;

                                    // 增加指数倍增值
                                    indexMulti++;
                                }
                            }
                        }
                    } else {
                        if(sum <= mMaxRow)
                        {
                            child.layout(getPaddingLeft() + clp.leftMargin,
                                    getPaddingTop() + clp.topMargin + multi,
                                    getPaddingLeft() + clp.leftMargin + childActualSize,
                                    getPaddingTop() + clp.topMargin + multi + childActualSize);
                            multi += childActualSize + clp.topMargin + clp.bottomMargin;
                        }else{
                            /*
                             * 根据当前子元素进行布局
                             */
                            if (index < mMaxRow * indexMulti) {
                                child.layout(getPaddingLeft() + clp.leftMargin + indexMultiWidth, getPaddingTop() + clp.topMargin + indexMultiHeight,
                                        childActualSize + getPaddingLeft() + clp.leftMargin + indexMultiWidth, childActualSize + getPaddingTop()
                                                + clp.topMargin + indexMultiHeight);
                                indexMultiHeight += childActualSize + clp.topMargin + clp.bottomMargin;
                                tempWidth = Math.max(tempWidth, childActualSize + clp.leftMargin + clp.rightMargin);

                                /*
                                 * 如果下一次遍历到的子元素下标值大于限定值
                                 */
                                if (index + 1 >= mMaxRow * indexMulti) {
                                    indexMultiWidth += tempWidth;

                                    indexMultiHeight = 0;

                                    indexMulti++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }
}
