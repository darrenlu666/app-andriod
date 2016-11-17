package com.codyy.erpsportal.homework.widgets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 普通的确认框
 * Created by ldh on 15-8-26.
 * 提交弹出框
 * ldh
 * 2016/03/09
 */
public class MySubmitDialog extends DialogFragment {

    private static final String TAG = MySubmitDialog.class.getSimpleName();
    public static final String ARG_CONTENT = "content";
    public static final String ARG_DIALOG_TYPE = "dialog.type";
    public static final String ARG_LEFT_VALUE = "ARG_LEFT_VALUE";//左边按钮的值
    public static final String ARG_RIGHT_VALUE = "ARG_RIGHT_VALUE";//右边按钮的值
    /**
     * 默认第一种类型
     * ----------------------------------------
     * |         取消发言  ?                   |
     * ----------------------------------------
     * |     取消         |       确定         |
     * ----------------------------------------
     */
    public static final String DIALOG_STYLE_TYPE_0 = "dialog.style.first";

    /**
     * 第二类dialog
     * ----------------------------------------
     * |         您已经取消发言!                |
     * ----------------------------------------
     */
    public static final String DIALOG_STYLE_TYPE_1 = "dialog.style.second";
    /**
     * 第三类dialog
     * ----------------------------------------
     * |         您已经取消发言!                |
     * ----------------------------------------
     * |                 确定                  |
     * ----------------------------------------
     */
    public static final String DIALOG_STYLE_TYPE_2 = "dialog.style.third";
    /**
     * 默认第一种类型
     * ----------------------------------------
     * |         取消发言  ?                   |
     * ----------------------------------------
     * |     否         |       是        |
     * ----------------------------------------
     */
    public static final String DIALOG_STYLE_TYPE_3 = "dialog.style.four";

    private View mRootView;
    @Bind(R.id.tv_dialog_content)
    TextView mTextView;
    @Bind(R.id.btn_dialog_not)
    Button mCancelButton;
    @Bind(R.id.btn_dialog_sure)
    Button mSureButton;
    @Bind(R.id.divider)
    View mVerticalDivider;
    @Bind(R.id.line_bottom)
    LinearLayout mBottomLinearLayout;

    @StringDef(value = {DIALOG_STYLE_TYPE_0, DIALOG_STYLE_TYPE_1, DIALOG_STYLE_TYPE_2, DIALOG_STYLE_TYPE_3})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {
    }

    private OnclickListener mOnclickListener;

    private String mTitle = "";
    private String mStyle = DIALOG_STYLE_TYPE_0;


    /**
     * 获取实例 ，不需要点击
     *
     * @param content
     * @param dialogType
     * @return
     */
    public static MySubmitDialog newInstance(String content, @TYPE String dialogType) {
        MySubmitDialog dialog = new MySubmitDialog();
        Bundle bd = new Bundle();
        bd.putString(ARG_CONTENT, content);
        bd.putString(ARG_DIALOG_TYPE, dialogType);
        dialog.setArguments(bd);
        return dialog;
    }

    /**
     * 获取实例
     *
     * @param title 标题
     * @return
     */
    public static MySubmitDialog newInstance(String title, @TYPE String dialogType, MySubmitDialog.OnclickListener onclickListener) {
        MySubmitDialog dialog = new MySubmitDialog();
        Bundle bd = new Bundle();
        bd.putString(ARG_CONTENT, title);
        bd.putString(ARG_DIALOG_TYPE, dialogType);
        dialog.setArguments(bd);
        dialog.setOnclickListener(onclickListener);

        return dialog;
    }

    /**
     * 获取实例
     *
     * @param title 标题
     * @return
     */
    public static MySubmitDialog newInstance(String title, String confirm, @TYPE String dialogType, MySubmitDialog.OnclickListener onclickListener) {
        MySubmitDialog dialog = new MySubmitDialog();
        Bundle bd = new Bundle();
        bd.putString(ARG_CONTENT, title);
        bd.putString(ARG_RIGHT_VALUE, confirm);
        bd.putString(ARG_DIALOG_TYPE, dialogType);
        dialog.setArguments(bd);
        dialog.setOnclickListener(onclickListener);

        return dialog;
    }

    /**
     * 获取实例
     *
     * @param title 标题
     * @return
     */
    public static MySubmitDialog newInstance(String title, String confirm, String no, @TYPE String dialogType, MySubmitDialog.OnclickListener onclickListener) {
        MySubmitDialog dialog = new MySubmitDialog();
        Bundle bd = new Bundle();
        bd.putString(ARG_CONTENT, title);
        bd.putString(ARG_RIGHT_VALUE, confirm);
        bd.putString(ARG_LEFT_VALUE, no);
        bd.putString(ARG_DIALOG_TYPE, dialogType);
        dialog.setArguments(bd);
        dialog.setOnclickListener(onclickListener);

        return dialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mTitle = getArguments().getString(ARG_CONTENT);
            mStyle = getArguments().getString(ARG_DIALOG_TYPE);
        }
        //禁止点击外面取消
//        this.setCancelable(false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Cog.i(TAG, "onDismiss () ~~");
        super.onDismiss(dialog);

        if (DIALOG_STYLE_TYPE_1.equals(mStyle)) {//不能用==
            if (null != mOnclickListener) {
                mOnclickListener.dismiss();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //no title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (null == mRootView || mTextView == null) {
            mRootView = inflater.inflate(R.layout.fragment_dialog_sure_not, container, false);
            ButterKnife.bind(this, mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //do something . data set .
        mTextView.setText(mTitle);

        switch (mStyle) {
            case DIALOG_STYLE_TYPE_0://default
                //do nothing .
                this.setCancelable(false);
                break;
            case DIALOG_STYLE_TYPE_1:
                mBottomLinearLayout.setVisibility(View.GONE);
                this.setCancelable(true);
                break;
            case DIALOG_STYLE_TYPE_2:
                mCancelButton.setVisibility(View.GONE);
                mVerticalDivider.setVisibility(View.GONE);
                this.setCancelable(false);
                if (!TextUtils.isEmpty(getArguments().getString(ARG_RIGHT_VALUE))) {
                    mSureButton.setText(getArguments().getString(ARG_RIGHT_VALUE));
                }
                break;
            case DIALOG_STYLE_TYPE_3:
                this.setCancelable(false);
                if (!TextUtils.isEmpty(getArguments().getString(ARG_RIGHT_VALUE))) {
                    mSureButton.setText(getArguments().getString(ARG_RIGHT_VALUE));
                }
                if (!TextUtils.isEmpty(getArguments().getString(ARG_LEFT_VALUE))) {
                    mCancelButton.setText(getArguments().getString(ARG_LEFT_VALUE));
                }
                break;
            default:
                break;
        }
    }

    public void setOnclickListener(OnclickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    @OnClick(R.id.btn_dialog_sure)
    void makeSureClick() {
        if (null != mOnclickListener) mOnclickListener.rightClick(this);
    }

    @OnClick(R.id.btn_dialog_not)
    void cancelClick() {
        if (null != mOnclickListener) mOnclickListener.leftClick(this);
    }


    public interface OnclickListener {
        /**
         * 左面的按钮 click
         */
        void leftClick(MySubmitDialog myDialog);

        /**
         * 右面的按钮 click
         */
        void rightClick(MySubmitDialog myDialog);

        /**
         * 隐藏消失dialog .(style 1 时调用)
         */
        void dismiss();
    }

}
