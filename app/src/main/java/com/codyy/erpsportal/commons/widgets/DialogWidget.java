package com.codyy.erpsportal.commons.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.Cog;

/**
 * Created by kmdai on 2015/7/24.
 */
public class DialogWidget extends DialogFragment {

    public static final String TAG = DialogWidget.class.getSimpleName();
    private OnDialogClickListener onDialogClickListener;
    private TextView mTextView;

    private String mTitle;//title
    private String mContent;//dialog content .

    private View mRootView;

    public DialogWidget() {
        super();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.input_dialog);
        Cog.d(TAG,"onCreate savedInstanceState:"+savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Cog.d(TAG,"onCreateView~"+savedInstanceState);
        if(null == mRootView){
            mRootView = inflater.inflate(R.layout.dialog_sure_not, container, false);

            mTextView = (TextView) mRootView.findViewById(R.id.dialog_sure_text);
            mRootView.findViewById(R.id.dialog_not).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (onDialogClickListener != null) {
                        onDialogClickListener.onLeftClck();
                    }
                }
            });
            mRootView.findViewById(R.id.dialog_sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDialogClickListener != null) {
                        onDialogClickListener.onRightClick();
                    }
                }
            });
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        System.out.println(this.getTag());
        getDialog().getWindow().setWindowAnimations(R.style.dialog_animation);
        Cog.d(TAG, "savedInstanceState:" + savedInstanceState);
    }

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    interface OnDialogClickListener {
        void onLeftClck();

        void onRightClick();
    }
}
