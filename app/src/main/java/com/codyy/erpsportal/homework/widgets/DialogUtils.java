package com.codyy.erpsportal.homework.widgets;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.codyy.erpsportal.commons.utils.DialogUtil;

/**
 * 对话框弹出 工具类
 * Created by ldh on 2016/7/25.
 */
public class DialogUtils {
    private String mTitle;
    private FragmentManager mContext;

    public DialogUtils(String title, FragmentManager context) {
        this.mTitle = title;
        mContext = context;
        dialogShow(title);
    }

    private void dialogShow(String dialogTitle) {
        MySubmitDialog dialog = MySubmitDialog.newInstance(dialogTitle, MySubmitDialog.DIALOG_STYLE_TYPE_0, new MySubmitDialog.OnclickListener() {
            @Override
            public void leftClick(MySubmitDialog myDialog) {
                myDialog.dismiss();
            }

            @Override
            public void rightClick(MySubmitDialog myDialog) {
                if (mListener != null) {
                    mListener.onRightClick();
                }
                myDialog.dismiss();
            }

            @Override
            public void dismiss() {

            }
        });
        dialog.show(mContext, "submitReadInfos");
    }

    public interface OnRightClickListener {
        void onRightClick();
    }

    private OnRightClickListener mListener;

    public void setOnRightClickListener(OnRightClickListener listener) {
        this.mListener = listener;
    }


}
