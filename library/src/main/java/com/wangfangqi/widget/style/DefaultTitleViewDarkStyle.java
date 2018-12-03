package com.wangfangqi.widget.style;

import com.wangfangqi.widget.R;

/**
 * @author wangfangqi
 * created at 2018/11/29
 */
public class DefaultTitleViewDarkStyle extends DefaultTitleViewLightStyle {

    @Override
    public int getNavigationIcon() {
        return R.drawable.wfq_ic_white_back;
    }

    @Override
    public int getLeftTextColor() {
        return 0xCCFFFFFF;
    }

    @Override
    public int getRightTextColor() {
        return 0xCCFFFFFF;
    }

    @Override
    public int getTitleTextColor() {
        return 0xEEFFFFFF;
    }

    @Override
    public int getSubTitleTextColor() {
        return super.getSubTitleTextColor();
    }
}
