package com.codyy.erpsportal.statistics.controllers.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.tutorship.widgets.WheelDatePicker;

import org.joda.time.LocalDate;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 布置试卷-时间选择控件
 * Created by eachann on 2016-01-21.
 */
public class DayPickerDialog extends DialogFragment {
    public static final String TAG = DayPickerDialog.class.getSimpleName();
    public static final String ARG_BEFORE = "arg_before";

    public static final String ARG_AFTER = "arg_after";

    public static final String ARG_DEFAULT = "arg_default";

    @Bind(R.id.tv_title)
    TextView mTitleTv;

    @Bind(R.id.btn_cancel)
    Button mCancelBtn;
    @Bind(R.id.btn_confirm)
    Button mConfirmBtn;

    @Bind(R.id.picker_date)
    WheelDatePicker mWheelDatePicker;

    private OnClickTimePicker mOnClickTimePicker;

    private LocalDate mBeforeDate;

    private LocalDate mAfterDate;

    private LocalDate mDefaultDate;

    public static DayPickerDialog newInstance(LocalDate afterDate, LocalDate beforeDate, LocalDate defaultDate) {
        DayPickerDialog dayPickerDialog = new DayPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_AFTER, afterDate);
        bundle.putSerializable(ARG_BEFORE, beforeDate);
        bundle.putSerializable(ARG_DEFAULT, defaultDate);
        dayPickerDialog.setArguments(bundle);
        return dayPickerDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDefaultDate = (LocalDate) getArguments().getSerializable(ARG_DEFAULT);
            if (mDefaultDate == null) mDefaultDate = LocalDate.now();
            mAfterDate = (LocalDate) getArguments().getSerializable(ARG_AFTER);
            mBeforeDate = (LocalDate) getArguments().getSerializable(ARG_BEFORE);
            if (mAfterDate != null && mBeforeDate != null
                    && mAfterDate.isAfter(mBeforeDate)) {
                LocalDate tempData = mAfterDate;
                mAfterDate = mBeforeDate;
                mBeforeDate = tempData;
            }
            if (mAfterDate != null && mDefaultDate.isBefore(mAfterDate)) {
                mDefaultDate = mAfterDate;
            }
            if (mBeforeDate != null && mDefaultDate.isAfter(mBeforeDate)) {
                mDefaultDate = mBeforeDate;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_day_picker, null);
        ButterKnife.bind(this, view);
        mWheelDatePicker.setCurved(true);
        mWheelDatePicker.setSelectedItemTextColor(0xff313131);
        mWheelDatePicker.setCyclic(true);
        mWheelDatePicker.setSelectedYear(mDefaultDate.getYear());
        mWheelDatePicker.setSelectedMonth(mDefaultDate.getMonthOfYear());
        mWheelDatePicker.setSelectedDay(mDefaultDate.getDayOfMonth());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setCancelable(true);
        return alertDialog;
    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (mOnClickTimePicker != null) {
                    LocalDate selectedDate = LocalDate.fromDateFields(mWheelDatePicker.getCurrentDate());

                    if (mBeforeDate != null && selectedDate.isAfter(mBeforeDate)) {
                        Snackbar.make(view, "请选择" + mBeforeDate.toString("yyyy-MM-dd") + "前的时间", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (mAfterDate != null && selectedDate.isBefore(mAfterDate)) {
                        Snackbar.make(view, "请选择" + mAfterDate.toString("yyyy-MM-dd") + "后的时间", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    mOnClickTimePicker.onConfirmClickListener(selectedDate.toString("yyyy-MM-dd"));
                    dismiss();
                } else {
                    Snackbar.make(view, getString(R.string.exam_arrange_time_picker_exception_msg), Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void setOnConfirmListener(OnClickTimePicker listener) {
        this.mOnClickTimePicker = listener;
    }

    public interface OnClickTimePicker {
        void onConfirmClickListener(String time);
    }
}
