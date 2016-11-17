package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.models.homenews.FamousClassBean;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;


/**
 * Created by ningfeng on 2015/7/24.
 * modify by ldh 2016/10/09
 */
public class HomeWorkAdapter extends BaseAdapter {
    private Context mContext;
    private List<FamousClassBean> mData;

    public HomeWorkAdapter(Context context, List<FamousClassBean> data) {
        mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_task, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        FamousClassBean bean = mData.get(i);
        viewHolder.schoolName.setText(bean.getSchoolName());
        viewHolder.classType.setText(bean.getGrade() + "·" + bean.getSubject());
        viewHolder.teacherName.setText(bean.getTeacherName());
        viewHolder.taskDate.setText(bean.getDate());
        viewHolder.classIcon.setImageURI(Uri.parse(bean.getSubjectPic()));
        viewHolder.mTvWorkCount.setText(bean.getWorkCount());
        return view;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_task.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @butterknife.Bind(R.id.class_icon)
        SimpleDraweeView classIcon;
        @butterknife.Bind(R.id.school_name)
        TextView schoolName;
        @butterknife.Bind(R.id.class_type)
        TextView classType;
        @butterknife.Bind(R.id.teacher_name)
        TextView teacherName;
        @butterknife.Bind(R.id.task_date)
        TextView taskDate;
        @Bind(R.id.tv_work_count)
        TextView mTvWorkCount;

        ViewHolder(View view) {
            butterknife.ButterKnife.bind(this, view);
        }
    }

}
