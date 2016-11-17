package com.codyy.erpsportal.exam.controllers.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.exam.controllers.activities.school.SchoolArrangeActivity;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.exam.widgets.wheelview.LoopView;
import com.codyy.erpsportal.exam.widgets.wheelview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 布置试卷-时间选择控件
 * Created by eachann on 2016-01-21.
 */
public class TimePickerDialog extends DialogFragment {
    public static final String TAG = TimePickerDialog.class.getSimpleName();
    private LoopView mLoopViewY, mLoopViewM, mLoopViewD, mLoopViewH, mLoopViewMM;
    List<String> mListDays;
    /**
     * 二月平年
     */
    private static final String[] mDayFebruaryP = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28"};
    /**
     * 二月闰年
     */
    private static final String[] mDayFebruaryR = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29"};
    /**
     * 4 6 9 11
     */
    private static final String[] mDayNormalP = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
    /**
     * 1 3 5 7 8 10 12
     */
    private static final String[] mDayNormalT = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    /**
     * 月
     */
    private static final String[] mMonth = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    /**
     * 小时
     */
    private static final String[] mHour = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};

    public interface OnClickTimePicker {
        void onConfirmClickListener(String time);
    }

    public void setOnConfirmListener(OnClickTimePicker listener) {
        this.mOnClickTimePicker = listener;
    }

    private OnClickTimePicker mOnClickTimePicker;
    private ArrayList<String> mListYs;
    private ArrayList<String> mListMs;
    private List<String> mListHs;
    private List<String> mListMMs;
    private static final float TEXT_SIZE = 18f;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_time_picker_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        Button confirm = (Button) view.findViewById(R.id.btn_confirm);
        mLoopViewY = (LoopView) view.findViewById(R.id.lv_year);
        mLoopViewM = (LoopView) view.findViewById(R.id.lv_mon);
        mLoopViewD = (LoopView) view.findViewById(R.id.lv_y_d);
        mLoopViewH = (LoopView) view.findViewById(R.id.lv_y_h);
        mLoopViewMM = (LoopView) view.findViewById(R.id.lv_y_mm);

        if (getArguments().getString(SchoolArrangeActivity.DIALOG_TITLE) != null) {
            title.setText(getArguments().getString(SchoolArrangeActivity.DIALOG_TITLE));
        }
        Calendar calendar = Calendar.getInstance();
        // 当前年
        final int year = calendar.get(Calendar.YEAR);
        // 当前月
        final int month = (calendar.get(Calendar.MONTH)) + 1;
        // 当前月的第几天：即当前日
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        // 当前时：HOUR_OF_DAY-24小时制；HOUR-12小时制
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // 当前分
        int minute = calendar.get(Calendar.MINUTE);

        mListYs = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            mListYs.add(year + j + "");
        }

        mListMs = new ArrayList();
        for (int j = month; j < 13; j++) {
            mListMs.add((j < 10 ? "0" + j : j + ""));
        }
        mListDays = getDays(year, month);
        mListHs = Arrays.asList(mHour);
        mListMMs = new ArrayList();
        for (int i = 1; i < 61; i++) {
            mListMMs.add(i < 10 ? "0" + String.valueOf(i) : String.valueOf(i));
        }

        mLoopViewY.setNotLoop();
        mLoopViewY.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mListMs.clear();
                if(index!=0){
                    //重新获取月数据
                    for (int j = 1; j < 13; j++) {
                        mListMs.add(j < 10 ? "0" + j : j+"");
                    }
                }else{
                    for (int j = month; j < 13; j++) {
                        mListMs.add((j < 10 ? "0" + j : j + ""));
                    }
                }
                mLoopViewM.setItems(mListMs);
            }
        });
        mLoopViewY.setItems(mListYs);
        mLoopViewY.setInitPosition(0);
        mLoopViewY.setTextSize(TEXT_SIZE);
        //设置是否循环播放
        mLoopViewM.setNotLoop();
        //滚动监听
        mLoopViewM.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String string = mListMs.get(index);
                //int selectMonth = Integer.valueOf(string.split(" ")[1]);
                mLoopViewD.setItems(getDays(year, Integer.valueOf(string)));
                mLoopViewD.setTextSize(TEXT_SIZE);
            }
        });
        //设置原始数据
        mLoopViewM.setItems(mListMs);
        //设置初始位置
        mLoopViewM.setInitPosition(0);
        mLoopViewM.setTextSize(TEXT_SIZE);
        mLoopViewD.setItems(mListDays);
        mLoopViewD.setInitPosition(dayOfMonth - 1);
        mLoopViewD.setTextSize(TEXT_SIZE);
        mLoopViewH.setItems(mListHs);
        if (title.getText().toString().contains(getString(R.string.exam_arrange_end_time_title)))
            mLoopViewH.setInitPosition(hour + 2);
        else
            mLoopViewH.setInitPosition(hour - 1);
        mLoopViewH.setTextSize(TEXT_SIZE);
        mLoopViewMM.setItems(mListMMs);
        mLoopViewMM.setInitPosition(minute - 1);
        mLoopViewMM.setTextSize(TEXT_SIZE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickTimePicker != null) {
                    StringBuilder time = new StringBuilder();
                    time.append(mListYs.get(mLoopViewY.getSelectedItem())+"-");
                    time.append(mListMs.get(mLoopViewM.getSelectedItem()));
                    time.append("-" + mListDays.get(mLoopViewD.getSelectedItem()) + " ");
                    time.append(mListHs.get(mLoopViewH.getSelectedItem())).append(":");
                    time.append(mListMMs.get(mLoopViewMM.getSelectedItem()));
                    if (DateUtil.stringToLong(time.toString(), DateUtil.DEF_FORMAT) >= DateUtil.stringToLong(DateUtil.getNow(DateUtil.DEF_FORMAT), DateUtil.DEF_FORMAT)) {
                        mOnClickTimePicker.onConfirmClickListener(time.toString());
                        dismiss();
                    } else {
                        Snackbar.make(v, getString(R.string.exam_arrange_time_picker_error_msg), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    Snackbar.make(v, getString(R.string.exam_arrange_time_picker_exception_msg), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        setCancelable(true);
        return alertDialog;
    }

    /**
     * 根据年月 获取天数
     *
     * @param year
     * @param month
     * @return
     */
    private List<String> getDays(int year, int month) {
        List<String> list;
        if (month == 2) {
            if ((year % 4 == 0 && year % 100 != 0) || (year % 100 == 0 && year % 400 == 0)) {//闰年2月29天
                list = Arrays.asList(mDayFebruaryR);
            } else {
                list = Arrays.asList(mDayFebruaryP);//平年2月28天
            }
        } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {//1 3 5 7 8 10 12 月每月31天
            list = Arrays.asList(mDayNormalT);
        } else {
            list = Arrays.asList(mDayNormalP);//2 4 6 9 11 月每月30天
        }
        return list;
    }
}
