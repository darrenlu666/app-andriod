package com.codyy.erpsportal.reservation.controllers.fragments;

import android.app.Dialog;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.reservation.models.entities.ReservationClassDetial;

import java.util.ArrayList;

/**
 * Created by kmdai on 16-8-11.
 */
public class ListDialog extends DialogFragment {
    private ListView mListView;
    private OnItemClick mOnItemClick;
    private ArrayList<ReservationClassDetial.ScheduleListBean> mScheduleListBeanList;

    public static ListDialog newInstance(ArrayList<ReservationClassDetial.ScheduleListBean> mScheduleListBeanList) {

        Bundle args = new Bundle();
        args.putParcelableArrayList("data", mScheduleListBeanList);
        ListDialog fragment = new ListDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduleListBeanList = getArguments().getParcelableArrayList("data");
    }


    public void setOnItemClick(OnItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(), R.style.input_dialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_list, null);
        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setAdapter(mClassAdapter);
        dialog.setContentView(view);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
        dialog.getWindow().setLayout(UIUtils.dip2px(getContext(), 200), UIUtils.dip2px(getContext(), 200));
        return dialog;
    }

    private BaseAdapter mClassAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mScheduleListBeanList != null ? mScheduleListBeanList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ReservationClassDetial.ScheduleListBean bean = mScheduleListBeanList.get(position);
            TextView textview = new TextView(getContext());
            //添加点击效果（5.0以上会有水波纹效果）
            // Attribute array
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs);
            // Drawable held by attribute 'selectableItemBackground' is at index '0'
            Drawable d = a.getDrawable(0);
            a.recycle();
            textview.setBackgroundDrawable(d);
            textview.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(getContext(), 40)));
            textview.setGravity(Gravity.CENTER);
            textview.setText(bean.getSubjectName());
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClick != null) {
                        mOnItemClick.onClick(bean.getLiveAppointmentId());
                    }
                }
            });
            return textview;
        }
    };

    public interface OnItemClick {
        void onClick(String id);
    }
}
