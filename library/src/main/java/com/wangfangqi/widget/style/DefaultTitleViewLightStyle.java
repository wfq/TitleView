package com.wangfangqi.widget.style;

import com.wangfangqi.widget.R;

public class DefaultTitleViewLightStyle implements ITitleViewStyle {

    @Override
    public int getNavigationIcon() {
        return R.drawable.wfq_ic_black_back;
    }

    @Override
    public int getLeftTextColor() {
        return 0xFF666666;
    }

    @Override
    public int getLeftTextSize() {
        return 14;
    }

    @Override
    public int getRightTextColor() {
        return 0xFFA4A4A4;
    }

    @Override
    public int getRightTextSize() {
        return 14;
    }

    @Override
    public int getTitleTextColor() {
        return 0xFF000000;
    }

    @Override
    public int getTitleTextSize() {
        return 20;
    }

    @Override
    public int getSubTitleTextColor() {
        return 0xFF666666;
    }

    @Override
    public int getSubTitleTextSize() {
        return 10;
    }
}
