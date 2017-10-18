package com.wcc.www.customview.customstore;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by 王晨晨 on 2017/10/12.
 */

public class C50_PullableImageView extends ImageView implements C50_Strengthen2RefreshLayout.Pullable {

    public C50_PullableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canPullDown() {
        return true;
    }

    @Override
    public boolean canPullUp() {
        return true;
    }
}
