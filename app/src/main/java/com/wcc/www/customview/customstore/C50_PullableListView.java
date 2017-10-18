package com.wcc.www.customview.customstore;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by 王晨晨 on 2017/10/13.
 */

public class C50_PullableListView extends ListView implements C50_Strengthen2RefreshLayout.Pullable{

    public C50_PullableListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean canPullDown()
    {
//        if (getCount() == 0)
//        {
//            // 没有item的时候也可以下拉刷新
//            return true;
//        } else if (getFirstVisiblePosition() == 0
//                && getChildAt(0).getTop() >= 0)
//        {
//            // 滑到ListView的顶部了
//            return true;
//        } else
//            return false;
        return !ViewCompat.canScrollVertically(this, -1);
    }

    @Override
    public boolean canPullUp()
    {
//        if (getCount() == 0)
//        {
//            // 没有item的时候也可以上拉加载
//            return true;
//        } else if (getLastVisiblePosition() == (getCount() - 1))
//        {
//            // 滑到底部了
//            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
//                    && getChildAt(
//                    getLastVisiblePosition()
//                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
//                return true;
//        }
//        return false;
        return !ViewCompat.canScrollVertically(this, 1);
    }
}
