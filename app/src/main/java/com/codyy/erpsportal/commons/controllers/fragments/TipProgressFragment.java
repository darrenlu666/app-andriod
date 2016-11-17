package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * 点击状态后，弹出的提示框
 * Created by ldh on 2015/9/24.
 */
public class TipProgressFragment extends DialogFragment {

    public final static String ARG_TIP_STATUS_TYPE = "tipstatus";//消息提示类型
    public static final String UNSTART_STATUS_TIP = "dialog.content.one";//未开时会议
    public static final String END_STATUS_TIP = "dialog.content.two";//已结束会议
    public static final String OUT_STATUS_TIP = "dialog.content.three";//被踢出会议
    public static final String LOADED_STATUS_TIP = "dialog.content.four";//在其他地方登录，被挤下线
    private String mTipStatus;

    private View mView;
    private ImageView mImageView;
    private TextView mTextView;

    public static TipProgressFragment newInstance(String status) {

        Bundle args = new Bundle();
        args.putString(ARG_TIP_STATUS_TYPE, status);
        TipProgressFragment fragment = new TipProgressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            mTipStatus = getArguments().getString(ARG_TIP_STATUS_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(mView == null){
            mView = inflater.inflate(R.layout.fragment_progress,null,false);
            mImageView = (ImageView)mView.findViewById(R.id.iv_status_pic);
            mTextView = (TextView)mView.findViewById(R.id.tv_status_info);

            switch (mTipStatus){
                case UNSTART_STATUS_TIP:
                    mImageView.setBackgroundResource(R.drawable.ic_coffee_unstart);
                    mTextView.setText(R.string.unstart_meeting_status);
                    break;
                case  END_STATUS_TIP:
                    mImageView.setBackgroundResource(R.drawable.ic_time_end);
                    mTextView.setText(R.string.end_meeting_status);
                    break;

                case OUT_STATUS_TIP:
                    mImageView.setBackgroundResource(R.drawable.ic_warn_out);
                    mTextView.setText(R.string.remove_meeting_status);
                    break;

                case LOADED_STATUS_TIP:
                    mImageView.setBackgroundResource(R.drawable.ic_local_loaded);
                    mTextView.setText(R.string.error_meeting_status);
                    break;

                default:
                    break;
            }
        }
        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int windowWidth = wm.getDefaultDisplay().getWidth();

        getDialog().getWindow().setLayout(windowWidth * 9 / 10, LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
