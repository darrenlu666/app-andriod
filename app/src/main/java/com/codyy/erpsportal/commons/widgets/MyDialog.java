package com.codyy.erpsportal.commons.widgets;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
 * Created by poe on 15-8-26.
 */
public class MyDialog extends DialogFragment {

    private static final String TAG = MyDialog.class.getSimpleName();
    public static final String ARG_EXTRA_CONTENT = "content";
    public static final String ARG_EXTRA_DIALOG_TYPE = "dialog.type";
    public static final String ARG_EXTRA_LEFT_BTN_TEXT = "text.left";
    public static final String ARG_EXTRA_RIGHT_BTN_TEXT = "text.right";
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

    private View mRootView;
    @Bind(R.id.tv_dialog_content)TextView mTextView;
    @Bind(R.id.btn_dialog_not)Button mCancelButton;
    @Bind(R.id.btn_dialog_sure)Button mSubmitButton;
    @Bind(R.id.divider)View mVerticalDivider;
    @Bind(R.id.line_bottom)LinearLayout mBottomLinearLayout;

    @StringDef(value = {DIALOG_STYLE_TYPE_0,DIALOG_STYLE_TYPE_1,DIALOG_STYLE_TYPE_2})
    @Retention(RetentionPolicy.SOURCE)
    public @interface  TYPE{}

    private OnclickListener mOnclickListener;

    private String mTitle = "";
    private String mStyle = DIALOG_STYLE_TYPE_0 ;
    private String mCancelStr = "取消";
    private String mSubmitStr = "确定";


    /**
     * 获取实例 ，不需要点击
     * @param content
     * @param dialogType
     * @return
     */
    public static MyDialog newInstance(String content ,@TYPE String dialogType){
        MyDialog dialog = new MyDialog();
        Bundle bd = new Bundle();
        bd.putString(ARG_EXTRA_CONTENT, content);
        bd.putString(ARG_EXTRA_DIALOG_TYPE, dialogType);
        dialog.setArguments(bd);
        return dialog;
    }

    public static MyDialog newInstance(String content ,@TYPE String dialogType ,String leftText , String rightText ,MyDialog.OnclickListener onclickListener){
        MyDialog dialog = new MyDialog();
        Bundle bd = new Bundle();
        bd.putString(ARG_EXTRA_CONTENT, content);
        bd.putString(ARG_EXTRA_DIALOG_TYPE, dialogType);
        bd.putString(ARG_EXTRA_LEFT_BTN_TEXT, leftText);
        bd.putString(ARG_EXTRA_RIGHT_BTN_TEXT, rightText);
        dialog.setArguments(bd);
        dialog.setOnclickListener(onclickListener);
        return dialog;
    }

    /**
     * 获取实例
     * @param content
     * @return
     */
    public static MyDialog newInstance(String content ,@TYPE String dialogType , MyDialog.OnclickListener onclickListener){
        MyDialog dialog = new MyDialog();
        Bundle bd = new Bundle();
        bd.putString(ARG_EXTRA_CONTENT, content);
        bd.putString(ARG_EXTRA_DIALOG_TYPE, dialogType);
        dialog.setArguments(bd);
        dialog.setOnclickListener(onclickListener);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getArguments()){
            mTitle = getArguments().getString(ARG_EXTRA_CONTENT);
            mStyle  =   getArguments().getString(ARG_EXTRA_DIALOG_TYPE);
            if(!TextUtils.isEmpty(getArguments().getString(ARG_EXTRA_LEFT_BTN_TEXT))){
                mCancelStr  =  getArguments().getString(ARG_EXTRA_LEFT_BTN_TEXT) ;
            }
            if(!TextUtils.isEmpty(getArguments().getString(ARG_EXTRA_RIGHT_BTN_TEXT))){
                mSubmitStr  =   getArguments().getString(ARG_EXTRA_RIGHT_BTN_TEXT);
            }
        }
    }

    /**
     * 显示fragmentDialog
     * @param fragmentManager
     * @param TAG
     */
    public void showAllowStateLoss(FragmentManager fragmentManager , String TAG){
        Cog.i(this.TAG , "showAllowStateLoss () ~~"+TAG);
        if (fragmentManager == null) return;
        FragmentTransaction ft = fragmentManager.beginTransaction() ;
        if (ft == null) return;
        ft.add(this ,TAG);
        ft.commitAllowingStateLoss();
    }



    @Override
    public void onDismiss(DialogInterface dialog) {
        Cog.i(TAG , "onDismiss () ~~");
        super.onDismiss(dialog);
        if(mStyle .equals( DIALOG_STYLE_TYPE_1)){
            if(null != mOnclickListener){
                mOnclickListener.dismiss();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //no title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(null == mRootView || mTextView == null){
            mRootView = inflater.inflate(R.layout.fragment_dialog_sure_not, container, false);
            ButterKnife.bind(this,mRootView);
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //do something . data set .
        mTextView.setText(mTitle);
        mCancelButton.setText(mCancelStr);
        mSubmitButton.setText(mSubmitStr);
        switch (mStyle){
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
                break;
            default:
                break;
        }
    }

    public void setTitle(String title){
        if(null != title) mTitle = title;
        mTextView.setText(mTitle);
    }

    public void setOnclickListener(OnclickListener mOnclickListener) {
        this.mOnclickListener = mOnclickListener;
    }

    @OnClick(R.id.btn_dialog_sure)
    void makeSureClick(){
        if(null != mOnclickListener) mOnclickListener.rightClick(this);
    }

    @OnClick(R.id.btn_dialog_not)
    void cancelClick(){
        if(null != mOnclickListener) mOnclickListener.leftClick(this);
    }


    public interface  OnclickListener{
        /**
         * 左面的按钮 click
         */
        void leftClick(MyDialog myDialog);

        /**
         * 右面的按钮 click
         */
        void rightClick(MyDialog myDialog);

        /**
         * 隐藏消失dialog .(style 1 时调用)
         */
        void dismiss();
    }

}
