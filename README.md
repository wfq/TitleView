# TitleView

[![](https://jitpack.io/v/wfq/TitleView.svg)](https://jitpack.io/#wfq/TitleView)

自定义标题栏控件，标题支持居左、居中两种模式，可自定义左、中、右多种布局类型。

| 属性 | 类型 |
|---|---|
| isImmersion  | bool  |
|statusBarColor| color |
|barStyle|enum|
|leftType|enum|
|leftText|string|
|leftTextColor|color|
|leftTextSize|dimension|
|leftIcon|reference|
|leftCustomView|reference|
|rightType|enum|
|rightText|string|
|rightTextColor|color|
|rightTextSize|dimension|
|rightIcon|reference|
|rightCustomView|reference|
|rightMenu|reference|
|centerType|enum|
|titleGravity|enum|
|title|string|
|titleColor|color|
|titleSize|dimension|
|subTitle|string|
|subTitleColor|color|
|subTitleSize|dimension|
|centerCustomView|reference|

支持配置全局默认Style属性`titleView_def_style`：
```
<resources>

    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>

        <item name="titleView_def_style">@style/titleStyle</item>
    </style>

    <style name="titleStyle">
        <item name="title">默认的</item>
    </style>

</resources>
```