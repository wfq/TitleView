package com.wangfangqi.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wangfangqi.widget.statusbar.StatusBarUtils;
import com.wangfangqi.widget.style.ITitleViewStyle;
import com.wangfangqi.widget.style.DefaultTitleViewLightStyle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.ActionMenuView;

/**
 * 自定义TitleView, 以替代{@link android.widget.Toolbar}
 *
 * <p>本控件不处理设置沉浸式状态栏功能，但是可兼容沉浸式状态栏功能，防止标题栏与状态栏重叠
 * <li>支持通过设置 paddingTop 的方法添加状态栏高度</li>
 * <li>使用属性 {@link #isImmersion} 或方法 {@link #setImmersion(boolean)} 可新增View填充
 * 状态栏，默认系统版本大于{@link Build.VERSION_CODES#KITKAT}时生效 </li>
 * </p>
 *
 * @author wangfangqi
 * created at 2018/11/21
 */
public class TitleView extends ViewGroup implements View.OnClickListener {

    private static ITitleViewStyle mDefaultStyle = new DefaultTitleViewLightStyle();

    @Override
    public void onClick(View v) {
        if (mOnChildClickListener != null) {
            if (v.getId() == R.id.wfq_left_view) {
                mOnChildClickListener.onLeftClick();
            } else if (v.getId() == R.id.wfq_right_view) {
                mOnChildClickListener.onRightClick();
            } else if (v.getId() == R.id.wfq_title || v.getId() == R.id.wfq_sub_title) {
                mOnChildClickListener.onTitleClick();
            }
        }
    }

    /**
     * 左侧布局类型枚举
     */
    @IntDef({TYPE_LEFT_NONE, TYPE_LEFT_TEXT_VIEW, TYPE_LEFT_CUSTOM_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    @interface LeftTypeMode {
    }

    /**
     * 右侧布局类型枚举
     */
    @IntDef({TYPE_RIGHT_NONE, TYPE_RIGHT_TEXT_VIEW, TYPE_RIGHT_CUSTOM_VIEW, TYPE_RIGHT_MENU_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    @interface RightTypeMode {
    }

    /**
     * 中间布局类型枚举
     */
    @IntDef({TYPE_CENTER_TEXT_VIEW, TYPE_CENTER_CUSTOM_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    @interface CenterTypeMode {
    }


    @IntDef({TYPE_TITLE_LEFT, TYPE_TITLE_CENTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface TitleGravity {
    }

    /**
     * 不显示左侧布局
     */
    public static final int TYPE_LEFT_NONE = 0;
    /**
     * 左侧布局为{@link TextView}
     */
    public static final int TYPE_LEFT_TEXT_VIEW = 1;
    /**
     * 左侧为自定义布局
     */
    public static final int TYPE_LEFT_CUSTOM_VIEW = 2;

    /**
     * 不显示右布局
     */
    public static final int TYPE_RIGHT_NONE = 0;
    /**
     * 右侧为{@link TextView}
     */
    public static final int TYPE_RIGHT_TEXT_VIEW = 1;
    /**
     * 右侧为自定义布局
     */
    public static final int TYPE_RIGHT_CUSTOM_VIEW = 2;
    /**
     * 右侧为{@link ActionMenuView}
     */
    public static final int TYPE_RIGHT_MENU_VIEW = 3;


    public static final int TYPE_CENTER_TEXT_VIEW = 0;
    public static final int TYPE_CENTER_CUSTOM_VIEW = 1;

    /**
     * centerView居左
     */
    public static final int TYPE_TITLE_LEFT = 0;
    /**
     * centerView居中
     */
    public static final int TYPE_TITLE_CENTER = 1;

    // 是否沉浸式
    private boolean isImmersion;

    private View mStatusBar;
    private int mStatusBarColor;
    private int mStatusBarHeight = 0;

    private TextView mLeftTextView;
    private TextView mRightTextView;
    private LinearLayout mCenterView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;


    private int mLeftCustomViewRes;
    private int mCenterCustomViewRes;
    private int mRightCustomViewRes;
    private View viewCustomLeft;
    private View viewCustomCenter;
    private View viewCustomRight;

    private int mRightMenuViewRes;
    private ActionMenuView mRightMenuView;
    private MenuInflater menuInflater;

    private int mLeftType;
    private CharSequence mLeftText;
    private int mLeftTextColor;
    private float mLeftTextSize;
    private int mLeftIcon;

    private int mRightType;
    private CharSequence mRightText;
    private int mRightTextColor;
    private float mRightTextSize;

    private int mCenterType;
    private int mTitleGravity;
    private CharSequence mTitleText;
    private int mTitleColor;
    private float mTitleSize;
    private CharSequence mSubTitleText;
    private int mSubTitleColor;
    private float mSubTitleSize;

    private int mHeight;
    private int bgPressDrawable;

    private int HORIZONTAL_PADDING;

    private OnChildClickListener mOnChildClickListener;

    private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener =
            new ActionMenuView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (mOnChildClickListener != null) {
                        return mOnChildClickListener.onMenuItemClick(item);
                    }
                    return false;
                }
            };

    public TitleView(@NonNull Context context) {
        this(context, null);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleView);

        isImmersion = a.getBoolean(R.styleable.TitleView_isImmersion, false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        mStatusBarColor = a.getColor(R.styleable.TitleView_statusBarColor, 0);

        mLeftType = a.getInt(R.styleable.TitleView_leftType, TYPE_LEFT_NONE);
        mLeftText = a.getString(R.styleable.TitleView_leftText);
        mLeftTextColor = a.getColor(R.styleable.TitleView_leftTextColor, mDefaultStyle.getLeftTextColor());
        mLeftTextSize = a.getDimensionPixelSize(R.styleable.TitleView_leftTextSize, sp2px(context, mDefaultStyle.getLeftTextSize()));
        mLeftIcon = a.getResourceId(R.styleable.TitleView_leftIcon, 0);
        mLeftCustomViewRes = a.getResourceId(R.styleable.TitleView_leftCustomView, 0);

        mRightType = a.getInt(R.styleable.TitleView_rightType, TYPE_RIGHT_NONE);
        mRightText = a.getString(R.styleable.TitleView_rightText);
        mRightTextColor = a.getColor(R.styleable.TitleView_rightTextColor, mDefaultStyle.getRightTextColor());
        mRightTextSize = a.getDimensionPixelSize(R.styleable.TitleView_rightTextSize, sp2px(context, mDefaultStyle.getRightTextSize()));
        mRightMenuViewRes = a.getResourceId(R.styleable.TitleView_rightMenu, 0);
        mRightCustomViewRes = a.getResourceId(R.styleable.TitleView_rightCustomView, 0);

        mCenterType = a.getInt(R.styleable.TitleView_centerType, TYPE_CENTER_TEXT_VIEW);
        mTitleGravity = a.getInt(R.styleable.TitleView_titleGravity, TYPE_TITLE_CENTER);
        mTitleText = a.getString(R.styleable.TitleView_title);
        mSubTitleText = a.getString(R.styleable.TitleView_subTitle);
        mTitleColor = a.getColor(R.styleable.TitleView_titleColor, mDefaultStyle.getTitleTextColor());
        mSubTitleColor = a.getColor(R.styleable.TitleView_subTitleColor, mDefaultStyle.getSubTitleTextColor());
        mTitleSize = a.getDimensionPixelSize(R.styleable.TitleView_titleSize, sp2px(context, mDefaultStyle.getTitleTextSize()));
        mSubTitleSize = a.getDimensionPixelSize(R.styleable.TitleView_subTitleSize, sp2px(context, mDefaultStyle.getSubTitleTextSize()));
        mCenterCustomViewRes = a.getResourceId(R.styleable.TitleView_centerCustomView, 0);

//        a.getInt(R.styleable.Toolbar_barStyle, 0);
        a.recycle();
        initView(context);
    }

    private void initView(Context context) {
        mHeight = dip2px(context, 48);
        HORIZONTAL_PADDING = dip2px(context, 12);
        // 初始化水波纹点击效果背景
        int[] attribute = new int[]{android.R.attr.selectableItemBackground};
        TypedArray a = context.getTheme().obtainStyledAttributes(attribute);
        // Drawable held by attribute 'selectableItemBackground' is at index '0'
        bgPressDrawable = a.getResourceId(0, android.R.drawable.list_selector_background);
        a.recycle();

        initStatusBar();
        initLeftView();
        initCenterView();
        initRightView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width, height;
        width = getMeasuredWidth();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            height = mHeight + mStatusBarHeight + getPaddingTop();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        } else {
            height = getMeasuredHeight() + mStatusBarHeight;
        }

        int leftMeasureWidth, rightMeasureWidth;
        // 测量左侧布局
        if (getLeftView() != null) {
            measureChild(getLeftView(), widthMeasureSpec, heightMeasureSpec);
            leftMeasureWidth = getLeftView().getMeasuredWidth();
        } else {
            leftMeasureWidth = HORIZONTAL_PADDING;
        }
        // 测量右侧布局
        if (getRightView() != null) {
            measureChild(getRightView(), widthMeasureSpec, heightMeasureSpec);
            rightMeasureWidth = getRightView().getMeasuredWidth();
        } else {
            rightMeasureWidth = HORIZONTAL_PADDING;
        }
        // 测量中间布局
        if (mTitleGravity == TYPE_TITLE_CENTER) {
            if (leftMeasureWidth > rightMeasureWidth) {
                measureChild(getCenterView(),
                        MeasureSpec.makeMeasureSpec(width - 2 * leftMeasureWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            } else {
                measureChild(getCenterView(),
                        MeasureSpec.makeMeasureSpec(width - 2 * rightMeasureWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        } else {
            measureChild(getCenterView(),
                    MeasureSpec.makeMeasureSpec(width - leftMeasureWidth - rightMeasureWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isImmersion) {
            mStatusBar.layout(0, 0, getMeasuredWidth(), mStatusBarHeight);
        }
        int leftMeasureWidth, rightMeasureWidth;

        if (getLeftView() != null) {
            leftMeasureWidth = getLeftView().getMeasuredWidth();
            getLeftView().layout(0, mStatusBarHeight + getPaddingTop(),
                    leftMeasureWidth, getMeasuredHeight());
        } else {
            leftMeasureWidth = HORIZONTAL_PADDING;
        }

        if (getRightView() != null) {
            rightMeasureWidth = getRightView().getMeasuredWidth();
            getRightView().layout(getMeasuredWidth() - rightMeasureWidth, mStatusBarHeight + getPaddingTop(),
                    getMeasuredWidth(), getMeasuredHeight());
        } else {
            rightMeasureWidth = HORIZONTAL_PADDING;
        }

        if (mTitleGravity == TYPE_TITLE_CENTER) {
            if (leftMeasureWidth > rightMeasureWidth) {
                getCenterView().layout(leftMeasureWidth, mStatusBarHeight + getPaddingTop(),
                        getMeasuredWidth() - leftMeasureWidth, getMeasuredHeight());
            } else {
                getCenterView().layout(rightMeasureWidth, mStatusBarHeight + getPaddingTop(),
                        getMeasuredWidth() - rightMeasureWidth, getMeasuredHeight());
            }
        } else {
            getCenterView().layout(leftMeasureWidth, mStatusBarHeight + getPaddingTop(),
                    getMeasuredWidth() - rightMeasureWidth, getMeasuredHeight());
        }
    }

    private void initStatusBar() {
        if (isImmersion) {
            mStatusBarHeight = StatusBarUtils.getStatusBarHeight(getContext());
            mStatusBar = new View(getContext());
            mStatusBar.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mStatusBarHeight));
            mStatusBar.setBackgroundColor(mStatusBarColor);
            addView(mStatusBar);
        }
    }

    private void initLeftView() {
        switch (mLeftType) {
            case TYPE_LEFT_TEXT_VIEW:
                mLeftTextView = new TextView(getContext());
                mLeftTextView.setId(R.id.wfq_left_view);
                mLeftTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                if (!TextUtils.isEmpty(mLeftText)) {
                    mLeftTextView.setText(mLeftText);
                }
                mLeftTextView.setGravity(Gravity.CENTER);
                mLeftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftTextSize);
                mLeftTextView.setTextColor(mLeftTextColor);
                mLeftTextView.setBackgroundResource(bgPressDrawable);
                if (mLeftIcon != 0) {
//                    mLeftTextView.setCompoundDrawablePadding();
                    mLeftTextView.setCompoundDrawablesWithIntrinsicBounds(mLeftIcon, 0, 0, 0);
                }
                mLeftTextView.setPadding(HORIZONTAL_PADDING, 0, HORIZONTAL_PADDING, 0);
                mLeftTextView.setOnClickListener(this);
                addView(mLeftTextView);
                break;
            case TYPE_LEFT_CUSTOM_VIEW:
                if (mLeftCustomViewRes != 0) {
                    viewCustomLeft = LayoutInflater.from(getContext()).inflate(mLeftCustomViewRes, this, false);
                    addView(viewCustomLeft);
                }
                break;
        }
    }

    private void initCenterView() {
        switch (mCenterType) {
            case TYPE_CENTER_TEXT_VIEW:
                mCenterView = new LinearLayout(getContext());
                mTitleTextView = new TextView(getContext());
                mSubTitleTextView = new TextView(getContext());
                mCenterView.setId(R.id.wfq_center_view);
                mTitleTextView.setId(R.id.wfq_title);
                mSubTitleTextView.setId(R.id.wfq_sub_title);

                mCenterView.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams subTitleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mTitleTextView.setLayoutParams(titleLayoutParams);
                mSubTitleTextView.setLayoutParams(subTitleLayoutParams);

                setTitleGravity(mTitleGravity);

                if (null == mTitleText && getContext() instanceof Activity) {
                    // 如果当前上下文对象是Activity，就获取Activity的标题
                    setTitleText(getActivityLabel((Activity) getContext()));
                } else {
                    setTitleText(mTitleText);
                }
                mTitleTextView.setTextColor(mTitleColor);
                mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
                // 设置粗体
                mTitleTextView.getPaint().setFakeBoldText(true);
                mTitleTextView.setSingleLine();
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);

                setSubTitleText(mSubTitleText);
                mSubTitleTextView.setTextColor(mSubTitleColor);
                mSubTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSubTitleSize);
                mSubTitleTextView.setSingleLine();

                mTitleTextView.setOnClickListener(this);
                mSubTitleTextView.setOnClickListener(this);
                mCenterView.addView(mTitleTextView);
                mCenterView.addView(mSubTitleTextView);
                addView(mCenterView);
                break;
            case TYPE_CENTER_CUSTOM_VIEW:
                if (mCenterCustomViewRes != 0) {
                    viewCustomCenter = LayoutInflater.from(getContext()).inflate(mCenterCustomViewRes, this, false);
                    addView(viewCustomCenter);
                }
                break;
        }
    }

    private void initRightView() {
        switch (mRightType) {
            case TYPE_RIGHT_TEXT_VIEW:
                mRightTextView = new TextView(getContext());
                mRightTextView.setId(R.id.wfq_right_view);
                mRightTextView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                if (!TextUtils.isEmpty(mRightText)) {
                    mRightTextView.setText(mRightText);
                }
                mRightTextView.setGravity(Gravity.CENTER);
                mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);
                mRightTextView.setTextColor(mRightTextColor);
                mRightTextView.setBackgroundResource(bgPressDrawable);
//                if (a.hasValue(R.styleable.TitleView_leftIcon)) {
//                    mLeftTextView.setCompoundDrawablesWithIntrinsicBounds(a.getResourceId(R.styleable.TitleView_leftIcon, 0), 0, 0, 0);
//                }
                mRightTextView.setPadding(HORIZONTAL_PADDING, 0, HORIZONTAL_PADDING, 0);
                mRightTextView.setOnClickListener(this);
                addView(mRightTextView);
                break;
            case TYPE_RIGHT_CUSTOM_VIEW:
                if (mRightCustomViewRes != 0) {
                    viewCustomRight = LayoutInflater.from(getContext()).inflate(mRightCustomViewRes, this, false);
                    addView(viewCustomRight);
                }
                break;
            case TYPE_RIGHT_MENU_VIEW:
                mRightMenuView = new ActionMenuView(getContext());
                mRightMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                mRightMenuView.setOnMenuItemClickListener(mMenuViewItemClickListener);
                inflateMenu();
                addView(mRightMenuView);
                break;
        }
    }

    public void setOnChildClickListener(OnChildClickListener listener) {
        mOnChildClickListener = listener;
    }

    /**
     * 设置是否添加状态栏高度
     *
     * @param immersion
     */
    public void setImmersion(boolean immersion) {
        if (isImmersion == immersion) return;
        if (immersion) {
            isImmersion = true;
            initStatusBar();
        } else {
            mStatusBarHeight = 0;
            removeView(mStatusBar);
            isImmersion = false;
        }
    }

    @Nullable
    private View getLeftView() {
        switch (mLeftType) {
            case TYPE_LEFT_TEXT_VIEW:
                return mLeftTextView;
            case TYPE_LEFT_CUSTOM_VIEW:
                return viewCustomLeft;
            default:
                return null;
        }
    }

    @Nullable
    private View getCenterView() {
        switch (mCenterType) {
            case TYPE_CENTER_TEXT_VIEW:
                return mCenterView;
            case TYPE_CENTER_CUSTOM_VIEW:
                return viewCustomCenter;
            default:
                return null;
        }
    }

    @Nullable
    private View getRightView() {
        switch (mRightType) {
            case TYPE_RIGHT_CUSTOM_VIEW:
                return viewCustomRight;
            case TYPE_RIGHT_MENU_VIEW:
                return mRightMenuView;
            default:
                return mRightTextView;
        }
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        Window window = getWindow();
//        if (window == null) return;
//
////        StatusBarUtils.transparentStatusBar(window);
//    }
//
//    private Window getWindow() {
//        Context context = getContext();
//        Activity activity;
//        if (context instanceof Activity) {
//            activity = (Activity) context;
//        } else {
//            if (isInEditMode()) return null;
//            activity = (Activity) ((ContextWrapper) context).getBaseContext();
//        }
//        if (activity != null) {
//            return activity.getWindow();
//        }
//        return null;
//    }

    private MenuInflater getMenuInflater() {
        if (menuInflater == null) {
            menuInflater = new MenuInflater(getContext());
        }
        return menuInflater;
    }

    /**
     * AppCompatActivity#getMenuInflater() 的默认实现是 SupportMenuInflater 类，由于该类是隐藏的，
     * 为了兼容menu.xml文件中的 app 命名空间属性，请手动设置 AppCompatActivity 中的 MenuInflater。
     *
     * rightView类型为 {@link #TYPE_RIGHT_MENU_VIEW} 时，才需要设置此方法。
     *
     * @param inflater
     */
    public void setMenuInflater(@NonNull MenuInflater inflater) {
        this.menuInflater = inflater;
        inflateMenu();
    }

    private void inflateMenu() {
        if (mRightType == TYPE_RIGHT_MENU_VIEW && mRightMenuViewRes != 0) {
            mRightMenuView.getMenu().clear();
            getMenuInflater().inflate(mRightMenuViewRes, mRightMenuView.getMenu());
        }
    }

    /**
     * 设置标题
     */
    public void setTitleText(@StringRes int stringId) {
        setTitleText(getResources().getString(stringId));
    }

    public void setTitleText(CharSequence text) {
        mTitleText = text;
        if (mTitleTextView != null) {
            mTitleTextView.setText(mTitleText);
        }
    }

    /**
     * 设置副标题
     */
    public void setSubTitleText(@StringRes int stringId) {
        setSubTitleText(getResources().getString(stringId));
    }

    public void setSubTitleText(CharSequence text) {
        mSubTitleText = text;
        if (mSubTitleTextView != null) {
            if (TextUtils.isEmpty(mSubTitleText)) {
                mSubTitleTextView.setVisibility(GONE);
            } else {
                mSubTitleTextView.setText(mSubTitleText);
                mSubTitleTextView.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 设置左侧文本
     * 布局类型需为 {@link LeftTypeMode#TYPE_LEFT_TEXT_VIEW}
     *
     * @param stringId 文本资源
     */
    public void setLeftText(@StringRes int stringId) {
        setLeftText(getResources().getString(stringId));
    }

    public void setLeftText(CharSequence text) {
        mLeftText = text;
        if (mLeftTextView != null) {
            mLeftTextView.setText(mLeftText);
        }
    }

    /**
     * 设置右侧文本
     * 布局类型需为 {@link CenterTypeMode#TYPE_RIGHT_TEXT_VIEW }
     *
     * @param stringId 文本资源
     */
    public void setRightText(@StringRes int stringId) {
        setRightText(getResources().getString(stringId));
    }

    public void setRightText(CharSequence text) {
        mRightText = text;
        if (mRightTextView != null) {
            mRightTextView.setText(mRightText);
        }
    }

    /**
     * 修改左布局类型
     *
     * @param type {@link LeftTypeMode}
     */
    public void setLeftViewType(@LeftTypeMode int type) {
        if (mLeftType == type) return;

        if (getLeftView() != null) {
            removeView(getLeftView());
        }
        mLeftType = type;
        initLeftView();
    }

    /**
     * 修改居中布局类型
     *
     * @param type {@link CenterTypeMode}
     */
    public void setCenterViewType(@CenterTypeMode int type) {
        if (type == mCenterType) return;
        if (getCenterView() != null) {
            removeView(getCenterView());
        }
        mCenterType = type;
        initCenterView();
    }

    /**
     * 修改右布局类型
     *
     * @param type {@link RightTypeMode}
     */
    public void setRightViewType(@RightTypeMode int type) {
        if (type == mRightType) return;

        if (getRightView() != null) {
            removeView(getRightView());
        }
        mRightType = type;
        initRightView();
    }

    /**
     * 设置标题偏向，居左或居中
     *
     * @param gravity {@link TitleGravity}
     */
    public void setTitleGravity(@TitleGravity int gravity) {
        mTitleGravity = gravity;
        if (mCenterType == TYPE_CENTER_TEXT_VIEW) {
            if (mTitleGravity == TYPE_TITLE_CENTER) {
                mCenterView.setGravity(Gravity.CENTER);
                ((LinearLayout.LayoutParams) mTitleTextView.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
                ((LinearLayout.LayoutParams) mSubTitleTextView.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
            } else {
                mCenterView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                ((LinearLayout.LayoutParams) mTitleTextView.getLayoutParams()).gravity = Gravity.START;
                ((LinearLayout.LayoutParams) mSubTitleTextView.getLayoutParams()).gravity = Gravity.START;
            }
        }
    }

//    public abstract class OnSimpleChildClickListener implements ExpandableListView.OnChildClickListener {
//
//    }

    public interface OnChildClickListener {

        void onLeftClick();

        void onRightClick();

        void onTitleClick();

        boolean onMenuItemClick(MenuItem item);
    }

    /**
     * 获取 Activity 的Label属性值
     */
    static CharSequence getActivityLabel(Activity activity) {
        //获取清单文件中的label属性值
        CharSequence label = activity.getTitle();
        //如果Activity没有设置label属性，则默认会返回APP名称，需要过滤掉
        if (label != null && !label.toString().equals("")) {

            try {
                PackageManager packageManager = activity.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);

                if (!label.toString().equals(packageInfo.applicationInfo.loadLabel(packageManager).toString())) {
                    return label;
                }
            } catch (PackageManager.NameNotFoundException ignored) {}
        }
        return null;
    }

    /**
     * sp转px
     */
    static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    static int dip2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
