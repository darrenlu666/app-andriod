package com.codyy.erpsportal.commons.controllers.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * Created by kmdai on 16-2-18.
 */
public class WeiBoPopuDialog extends DialogFragment implements View.OnClickListener {
    public final static int TYPE_ALL = 0x001;
    /**
     * 删除
     */
    public final static int TYPE_DELETE_ONLY = TYPE_ALL + 1;
    /**
     * 私信
     */
    public final static int TYPE_MESSAGE_ONLY = TYPE_DELETE_ONLY + 1;
    /**
     * 关注
     */
    public final static int TYPE_MESSAGE_FLOW = TYPE_MESSAGE_ONLY + 1;
    /**
     * 取消关注
     */
    public final static int TYPE_MESSAGE_UNFLOW = TYPE_MESSAGE_FLOW + 1;
    /**
     *
     */
    public final static int TYPE_ALL_UNFLOW = TYPE_MESSAGE_UNFLOW + 1;
    /**
     * 个人回复
     */
    public final static int TYPE_PERSONAL_COMMENT = TYPE_ALL_UNFLOW + 1;

    public final static int TYPE_ALL_FLOW = TYPE_PERSONAL_COMMENT + 1;
    private TextView mTextDel;
    private TextView mTextCancel;
    private OnItemClick mOnItemClick;
    private int mType;

    public static WeiBoPopuDialog newInstence(int type) {
        WeiBoPopuDialog popuMenuDialog = new WeiBoPopuDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        popuMenuDialog.setArguments(bundle);
        return popuMenuDialog;
    }

    public void setOnItemClick(OnItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.input_dialog);
        mType = getArguments().getInt("type", TYPE_ALL);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(0xff000000));
        window.setWindowAnimations(R.style.dialog_filter_animation);
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weibo_popumenu, container, false);
        mTextDel = (TextView) view.findViewById(R.id.weibo_popu_delete);
        mTextCancel = (TextView) view.findViewById(R.id.weibo_popu_cancel);
        TextView message = (TextView) view.findViewById(R.id.weibo_popu_message);
        message.setOnClickListener(this);
        TextView flow = (TextView) view.findViewById(R.id.weibo_popu_flow);
        flow.setOnClickListener(this);
        mTextCancel.setOnClickListener(this);
        mTextDel.setOnClickListener(this);
        switch (mType) {
            case TYPE_ALL:
                flow.setVisibility(View.GONE);
                break;
            case TYPE_ALL_FLOW:
                break;
            case TYPE_ALL_UNFLOW:
                flow.setText("取消关注");
                break;
            case TYPE_DELETE_ONLY:
                message.setVisibility(View.GONE);
                flow.setVisibility(View.GONE);
                break;
            case TYPE_MESSAGE_ONLY:
                mTextDel.setVisibility(View.GONE);
                flow.setVisibility(View.GONE);
                break;
            case TYPE_MESSAGE_FLOW:
                mTextDel.setVisibility(View.GONE);
                break;
            case TYPE_MESSAGE_UNFLOW:
                mTextDel.setVisibility(View.GONE);
                flow.setText("取消关注");
                break;
            case TYPE_PERSONAL_COMMENT:
                message.setText("回复");
                flow.setVisibility(View.GONE);
                break;
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weibo_popu_delete:
                if (mOnItemClick != null) {
                    mOnItemClick.onDeletecleck();
                }
                dismiss();
                break;
            case R.id.weibo_popu_cancel:
                dismiss();
                break;
            case R.id.weibo_popu_message:
                if (mOnItemClick != null) {
                    mOnItemClick.onMsgClick();
                }
                dismiss();
                break;
            case R.id.weibo_popu_flow:
                if (mOnItemClick != null) {
                    mOnItemClick.onFlowClick();
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnItemClick != null) {
            mOnItemClick.onDismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public interface OnItemClick {
        void onDeletecleck();

        void onMsgClick();

        void onFlowClick();

        void onDismiss();
    }
}
