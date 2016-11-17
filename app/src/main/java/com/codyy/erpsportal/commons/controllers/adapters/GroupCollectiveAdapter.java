package com.codyy.erpsportal.commons.controllers.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.GroupCollectiveActivityDetail;
import com.codyy.erpsportal.commons.models.entities.GroupCollective;
import com.codyy.erpsportal.commons.utils.DateUtil;

import java.util.List;

/**
 * Created by kmdai on 16-3-9.
 */
public class GroupCollectiveAdapter extends RefreshBaseAdapter<GroupCollective> {

    public GroupCollectiveAdapter(Context mContext) {
        super(mContext);
    }

    public GroupCollectiveAdapter(Context mContext, List<GroupCollective> mDatas) {
        super(mContext, mDatas);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new CollectiveHolder(inflater.inflate(R.layout.group_collective_itemlayout, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position, GroupCollective entity) {
        ((CollectiveHolder) holder).setData(entity);
    }

    class CollectiveHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ImageView mPublic;
        TextView mTitleTV;
        TextView mNameTV;
        TextView mDateTV;

        public CollectiveHolder(View itemView) {
            super(itemView);
            init();
        }

        private void init() {
            mImageView = (ImageView) itemView.findViewById(R.id.group_collective_state);
            mPublic = (ImageView) itemView.findViewById(R.id.group_collective_public);
            mTitleTV = (TextView) itemView.findViewById(R.id.group_collective_text_title);
            mNameTV = (TextView) itemView.findViewById(R.id.group_collective_text_center);
            mDateTV = (TextView) itemView.findViewById(R.id.group_collective_text_date);
        }

        public void setData(final GroupCollective entity) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, CollectivePrepareLessonsDetailActivity.class);
//                    intent.putExtra(Constants.PREPARATIONID, entity.getMeetingId());
//                    intent.putExtra(Constants.TYPE_LESSON, Constants.TYPE_PREPARE_LESSON);
//                    mContext.startActivity(intent);
                    GroupCollectiveActivityDetail.start(mContext, entity.getMeetingId());
                }
            });
            mTitleTV.setText(null == entity.getMeetingTitle() ? "无主题" : entity.getMeetingTitle());
            switch (entity.getStatus()) {
                case "INIT":
                    mImageView.setImageResource(R.drawable.unstart);
                    mDateTV.setText("预计时长：" + getData(entity.getDuration()));
                    break;
                case "PROGRESS":
                    mImageView.setImageResource(R.drawable.xxhdpi_assessmenting_);
                    mDateTV.setText("预计时长：" + getData(entity.getDuration()));
                    break;
                case "END":
                    mImageView.setImageResource(R.drawable.xxhdpi_end);
                    mDateTV.setText("时长：" + getData(entity.getDuration()));
                    break;
            }
            if ("Y".equals(entity.getPublicFlag())) {
                mPublic.setVisibility(View.VISIBLE);
            } else {
                mPublic.setVisibility(View.GONE);
            }
            mNameTV.setText(entity.getRealName() + " | " + DateUtil.getDateStr(entity.getCreateTime(), DateUtil.DEF_FORMAT));
        }

        private String getData(long date) {
            if (date <= 0) {
                return "未知";
            }
            long h = date / 60;
            long m = date % 60;
            if (h > 0) {
                return h + "小时" + m + "分钟";
            }
            return m + "分钟";
        }
    }
}
