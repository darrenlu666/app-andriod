package com.codyy.erpsportal.resource.controllers.others;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.PopupWindowUtils;
import com.codyy.erpsportal.commons.models.entities.UserInfo;

/**
 * 筛选弹出控制
 * Created by gujiajia on 2016/9/8.
 */
public class FilterTypeMenuController {

    private Activity mActivity;

    private PopupWindow mPopupWindow;

    private UserInfo mUserInfo;

    private View mTitleBar;

    private OnMenuClickListener mOnMenuClickListener;

    private FilterTypeMenuController() { }

    public void onFilterBtnClick() {
        if (mPopupWindow == null) {
            View contentView = LayoutInflater.from(mActivity).inflate(R.layout.pw_menu_filter_by, null);
            mPopupWindow = new PopupWindow( contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button byPropertiesBtn = (Button) contentView.findViewById(R.id.btn_filter_by_properties);
            Button byKnowledgeBtn = (Button) contentView.findViewById(R.id.btn_filter_by_knowledge);
            byPropertiesBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuClickListener != null) mOnMenuClickListener.onPropertiesBtnClick();
                    mPopupWindow.dismiss();
                }
            });
            byKnowledgeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuClickListener != null) mOnMenuClickListener.onKnowledgeBtnClick();
                    mPopupWindow.dismiss();
                }
            });
        }
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            Rect frame = new Rect();
            mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            mPopupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.END |Gravity.TOP, 0, mTitleBar.getBottom() + frame.top);
            mPopupWindow.update();
            PopupWindowUtils.dimBehind(mPopupWindow);
        }
    }

    public interface OnMenuClickListener{
        void onPropertiesBtnClick();
        void onKnowledgeBtnClick();
    }

    public static class Builder {
        private Activity activity;

        private UserInfo userInfo;

        private View titleBar;

        private OnMenuClickListener onMenuClickListener;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
            return this;
        }

        public Builder setTitleBar(View titleBar) {
            this.titleBar = titleBar;
            return this;
        }

        public Builder setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
            this.onMenuClickListener = onMenuClickListener;
            return this;
        }

        public FilterTypeMenuController build() {
            if (activity == null || userInfo == null || titleBar == null) return null;
            FilterTypeMenuController filterTypeMenuController = new FilterTypeMenuController();
            filterTypeMenuController.mActivity = activity;
            filterTypeMenuController.mUserInfo = userInfo;
            filterTypeMenuController.mTitleBar = titleBar;
            filterTypeMenuController.mOnMenuClickListener = onMenuClickListener;
            return filterTypeMenuController;
        }
    }
}
