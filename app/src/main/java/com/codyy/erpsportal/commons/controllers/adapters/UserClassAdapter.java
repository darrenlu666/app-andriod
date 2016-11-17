package com.codyy.erpsportal.commons.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.EApplication;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserClassBase;
import com.codyy.erpsportal.commons.models.entities.UserClassStudent;
import com.codyy.erpsportal.commons.models.entities.UserClassTeacher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kmdai on 2015/9/1.
 */
public class UserClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<UserClassBase> userClassBases;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public UserClassAdapter(Context mContext, ArrayList<UserClassBase> userClassBases) {
        this.userClassBases = userClassBases;
        this.mContext = mContext;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case UserClassBase.TYPE_STUDENT:
            case UserClassBase.TYPE_TEACHER:
                return new UserClassHolder(LayoutInflater.from(mContext).inflate(R.layout.class_room_teacher_student, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final UserClassHolder holder1 = (UserClassHolder) holder;
        switch (getItemViewType(position)) {
            case UserClassBase.TYPE_STUDENT:
                final UserClassStudent student = (UserClassStudent) userClassBases.get(position);
//                holder1.mSimpledraweeview.setImageURI(Uri.parse());
                ImageFetcher.getInstance(EApplication.instance()).fetchSmall(holder1.mSimpledraweeview,student.getStudentHeadPic());
                holder1.mTextName.setText(student.getStudentName());
                holder1.mSimpledraweeview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.OnStudentItemClick(position, holder1.itemView);
                        }
                    }
                });
                break;
            case UserClassBase.TYPE_TEACHER:
                final UserClassTeacher teacher = (UserClassTeacher) userClassBases.get(position);
                System.out.println(teacher.getTeacherHeadpic());
                ImageFetcher.getInstance(EApplication.instance()).fetchSmall(holder1.mSimpledraweeview,teacher.getTeacherHeadpic());
//                holder1.mSimpledraweeview.setImageURI(Uri.parse(teacher.getTeacherHeadpic()));
                holder1.mTextName.setText(teacher.getTeacherName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //1.自己的信息跳转到首页-"我的"
                        UserInfo userInfo = UserInfoKeeper.getInstance().getUserInfo();
                        if (teacher.getTeacherId().equals(userInfo.getBaseUserId())) {
                            MainActivity.start((Activity) mContext, userInfo, 2);
                        } else {//2.访客
                            PublicUserActivity.start((Activity) mContext, teacher.getTeacherId());
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return userClassBases.size();
    }

    @Override
    public int getItemViewType(int position) {
        return userClassBases.get(position).getType();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'class_room_teacher_student.xml'
     * for easy to all layout elements.
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class UserClassHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.class_room_simpledraweeview)
        SimpleDraweeView mSimpledraweeview;
        @Bind(R.id.class_room_text_name)
        TextView mTextName;
        View itemView;

        UserClassHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void OnStudentItemClick(int position, View view);
    }

}
