package com.codyy.erpsportal.commons.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;

import java.lang.ref.WeakReference;

/**
 * Created by kmdai on 2015/4/22.
 */
public class DialogUtil {
    private Dialog dialog;
    private AnimationDrawable animationDrawable;
    private TextView msg;
    private Button btnCancel, btnSubmit;
    private WeakReference<Context> mContextWeakRefrence;


    public DialogUtil(Context context) {
        initContext(context);
        dialog = new Dialog(mContextWeakRefrence.get(), R.style.transdialog);
        dialog.setContentView(R.layout.loading_layout);
        //dialog.setCancelable(false);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.loading_layout_iamge);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
    }

    public DialogUtil(Context context, boolean cancelable) {
        initContext(context);
        dialog = new Dialog(mContextWeakRefrence.get(), R.style.transdialog);
        dialog.setContentView(R.layout.loading_layout);
        dialog.setCancelable(cancelable);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.loading_layout_iamge);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
    }

    private void initContext(Context context) {
        mContextWeakRefrence = new WeakReference<>(context);
    }

    public void showDialog() {
        animationDrawable.start();
        dialog.show();
    }

    public void cancel() {
        if (dialog.isShowing()) {
            dialog.cancel();
            if (animationDrawable != null) {
                animationDrawable.stop();
            }
        }
    }

    public DialogUtil(Context context, View.OnClickListener left, View.OnClickListener right) {
        initContext(context);
        dialog = new Dialog(mContextWeakRefrence.get(), R.style.input_dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_sure_not, null);
        msg = (TextView) view.findViewById(R.id.dialog_sure_text);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
        view.findViewById(R.id.dialog_not).setOnClickListener(left);
        view.findViewById(R.id.dialog_sure).setOnClickListener(right);
    }

    //add by poe

    /**
     * @param context
     * @param left     left  button click listener
     * @param right    right button click listener
     * @param cancel   left button text to cancel
     * @param makeSure right button text to submit
     */
    public DialogUtil(Context context, View.OnClickListener left, View.OnClickListener right, String cancel, String makeSure) {
        initContext(context);
        dialog = new Dialog(mContextWeakRefrence.get(), R.style.input_dialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_sure_not, null);
        msg = (TextView) view.findViewById(R.id.dialog_sure_text);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);

        btnCancel = (Button) view.findViewById(R.id.dialog_not);
        btnSubmit = (Button) view.findViewById(R.id.dialog_sure);
        btnCancel.setOnClickListener(left);
        btnSubmit.setOnClickListener(right);

        btnCancel.setText(cancel);
        btnSubmit.setText(makeSure);
    }

    public void showDialog(String msg) {
        if (null == mContextWeakRefrence.get() || null == dialog) return;
        this.msg.setText(msg);
        dialog.show();
    }

    public void cancle() {
        if (null == mContextWeakRefrence.get()) return;
        if (dialog.isShowing()) {
            dialog.cancel();
            if (animationDrawable != null) {
                animationDrawable.stop();
            }
        }
    }
}
