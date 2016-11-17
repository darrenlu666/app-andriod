package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.HomeWorkItemDetailActivity;
import com.codyy.erpsportal.commons.models.entities.HomeWorkDetail;
import com.codyy.erpsportal.commons.models.entities.HomeWorkDetailItem;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ningfeng on 2015/7/27.
 */
public class HomeWorkDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<HomeWorkDetail.ClassRoom> classRoomList;
    private boolean showImage;

    public HomeWorkDetailAdapter(Context context, List<HomeWorkDetail.ClassRoom> classRoomList, boolean isShowImage) {
        this.mContext = context;
        this.classRoomList = classRoomList;
        showImage = isShowImage;
    }

    public void setList(List<HomeWorkDetail.ClassRoom> classRoomList) {
        this.classRoomList = classRoomList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_homework_detail_item, parent, false);
        return new HomeWorkMaster(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /*HomeWorkDetail homeWorkDetail = homeWorkDetails.get(position);
        switch (getItemViewType(position)) {
            case HomeWorkDetail.TITLE:
                HomeWorkTitle homeWorkTitle = (HomeWorkTitle) holder;
                homeWorkTitle.schoolName.setText(homeWorkDetail.getSchoolName());
                homeWorkTitle.homeworkClassDetail.setText(homeWorkDetail.getDate());
                homeWorkTitle.mTvGradeSubject.setText(homeWorkDetail.getGrade() + "/" + homeWorkDetail.getSubject());
                break;
            case HomeWorkDetail.MASTER:
            case HomeWorkDetail.RECEIVE:
                HomeWorkMaster master = (HomeWorkMaster) holder;
                master.mTexttitle.setText(homeWorkDetail.getSchoolName() + "/" + homeWorkDetail.getClassroomName());
                master.mRecycleview.setAdapter(new ItemAdapter(homeWorkDetail.getmDetailItems()));
                break;
        }*/

        HomeWorkDetail.ClassRoom classroom = classRoomList.get(position);
        HomeWorkMaster master = (HomeWorkMaster) holder;
        master.mTexttitle.setText(classroom.getSchoolName());
        master.mRecycleview.setAdapter(new ItemAdapter((ArrayList<HomeWorkDetailItem>) classroom.getRoomworkInfoList(), showImage));
    }

    @Override
    public int getItemCount() {
        return classRoomList == null ? 0 : classRoomList.size();
    }

    class HomeWorkTitle extends RecyclerView.ViewHolder {
        @Bind(R.id.school_name)
        TextView schoolName;
        @Bind(R.id.homework_class_detail)
        TextView homeworkClassDetail;
        @Bind(R.id.tv_grade_subject)
        TextView mTvGradeSubject;

        public HomeWorkTitle(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class HomeWorkMaster extends RecyclerView.ViewHolder {
        @Bind(R.id.activity_homework_texttitle)
        TextView mTexttitle;
        @Bind(R.id.activity_homework_recycleview)
        RecyclerView mRecycleview;

        public HomeWorkMaster(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecycleview.setLayoutManager(manager);
        }
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        ArrayList<HomeWorkDetailItem> mDetailItems;
        boolean isShowImage;

        ItemAdapter(ArrayList<HomeWorkDetailItem> mDetailItems, boolean isShowImage) {
            this.mDetailItems = mDetailItems;
            this.isShowImage = isShowImage;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_homework_class, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            if (isShowImage) {
                holder.img.setImageURI(Uri.parse(mDetailItems.get(position).getImgUrl()));
            }
            if ("已阅".endsWith(mDetailItems.get(position).getStatus())) {
                holder.isRead.setVisibility(View.VISIBLE);
            } else {
                holder.isRead.setVisibility(View.GONE);
            }
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, HomeWorkItemDetailActivity.class);
                    intent.putParcelableArrayListExtra("item", mDetailItems);
                    intent.putExtra("number", position);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mDetailItems.size();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img)
        SimpleDraweeView img;
        @Bind(R.id.class_is_read)
        ImageView isRead;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
