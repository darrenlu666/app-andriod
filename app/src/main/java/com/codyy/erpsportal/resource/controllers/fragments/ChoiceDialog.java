package com.codyy.erpsportal.resource.controllers.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.resource.widgets.WheelPicker;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 选择筛选参数弹出窗
 * Created by gujiajia on 2015/4/27.
 */
public class ChoiceDialog extends DialogFragment {

    private final static String TAG = "ChoiceDialog";

    private final static String ARG_CHOICES = "ARG_CHOICES";

    public OnChosenListener mChosenListener;

    private DialogInterface.OnCancelListener mOnCancelListener;

    public List<Choice> mChoices;

    @Bind(R.id.picker)
    WheelPicker mChoicePicker;

    public static ChoiceDialog newInstance(List<Choice> choices) {
        ChoiceDialog dialog = new ChoiceDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_CHOICES, (ArrayList<Choice>)choices);
        dialog.setArguments(bundle);
        return dialog;
    }

    public ChoiceDialog() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnChosenListener) {
            mChosenListener = (OnChosenListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mChosenListener != null) {
            mChosenListener = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChoices = getArguments().getParcelableArrayList(ARG_CHOICES);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_choice, null);
        ButterKnife.bind(this, view);
        Dialog dialog = new Dialog(getActivity(), R.style.input_dialog);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = getActivityWindowHeight() / 3;
        window.setAttributes(lp);
        initPickerDate();
        return dialog;
    }

    private void initPickerDate() {
        mChoicePicker.setWrapSelectorWheel(false);
        mChoicePicker.setMinValue(0);
        mChoicePicker.setMaxValue(mChoices.size() - 1);
        mChoicePicker.setValue(0);
        String[] displayedValues = new String[mChoices.size()];
        for (int i=0; i < displayedValues.length; i++) {
            displayedValues[i] = mChoices.get(i).getTitle();
        }
        mChoicePicker.setDisplayedValues(displayedValues);
        mChoicePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//        mChoicePicker.setFormatter(new NumberPicker.Formatter() {
//            @Override
//            public String format(int value) {
//                return mChoices.get(value).getTitle();
//            }
//        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private int getActivityWindowHeight() {
        return getActivity().getWindow().getDecorView().getHeight();
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirmClick() {
        int position = mChoicePicker.getValue();
        if (mChosenListener != null) {
            mChosenListener.onChosen(mChoices.get(position));
        }
    }

    public void onCancelClick() {
        dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mOnCancelListener != null) mOnCancelListener.onCancel(dialog);
    }

    public void setOnChosenListener(OnChosenListener onChosenListener) {
        this.mChosenListener = onChosenListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
    }

    /**
     * 变换选项数据，在显示（onCreate）之前调用
     * @param choices
     */
    public void changeData(List<Choice> choices) {
        if (getArguments() != null) {
            getArguments().putParcelableArrayList( ARG_CHOICES, (ArrayList<Choice>)choices);
        }
    }

    /**
     * 变换选项数据，真正显示中调用
     * @param choices
     */
    public void changeDataImmediately(List<Choice> choices) {
        mChoices = choices;
        if (getArguments() != null) {
            getArguments().putParcelableArrayList( ARG_CHOICES, (ArrayList<Choice>)choices);
        }
        initPickerDate();
    }

    public interface OnChosenListener {
        void onChosen(Choice choice);
    }
}
