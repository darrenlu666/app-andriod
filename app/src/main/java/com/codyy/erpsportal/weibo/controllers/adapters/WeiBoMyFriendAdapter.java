package com.codyy.erpsportal.weibo.controllers.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.TitleViewHolder;
import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.codyy.erpsportal.weibo.models.entities.WeiBoGroup;
import com.codyy.erpsportal.weibo.models.entities.WeiBoSearchPeople;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by kmdai on 16-2-23.
 */
public class WeiBoMyFriendAdapter extends RefreshBaseAdapter<WeiBoSearchPeople> {
    /**
     * 最近联系人分割
     */
    public final static int TYPE_DIVIDE_RECENT = RefreshEntity.REFRESH_TYPE_LASTVIEW + 1;
    /**
     * 最近联系人分割
     */
    public final static int TYPE_DIVIDE_MYFRIEND = TYPE_DIVIDE_RECENT + 1;
    /**
     * 最近联系人分割
     */
    public final static int TYPE_MY_FRIEND = TYPE_DIVIDE_MYFRIEND + 1;
    /**
     * 圈组
     */
    public final static int TYPE_MY_GROUP = TYPE_MY_FRIEND + 1;
    private Context mContext;
    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public WeiBoMyFriendAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
//        Fresco.initialize(mContext);
    }

    public WeiBoMyFriendAdapter(Context mContext, List<WeiBoSearchPeople> mDatas) {
        super(mContext, mDatas);
        this.mContext = mContext;
//        Fresco.initialize(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DIVIDE_RECENT:
            case TYPE_DIVIDE_MYFRIEND:
                return TitleViewHolder.create(mContext);
            case TYPE_MY_FRIEND:
                return new MyFriendHolder(inflater.inflate(R.layout.weibo_myfriend_item, parent, false));
            case TYPE_MY_GROUP:
                return new MyGroupHolder(inflater.inflate(R.layout.weibo_my_group, parent, false));
        }
        return new RecyclerView.ViewHolder(new View(mContext)) {
        };
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, final int position, final WeiBoSearchPeople entity) {
        switch (getItemViewType(position)) {
            case TYPE_DIVIDE_RECENT:
                TitleViewHolder holder1 = (TitleViewHolder) holder;
                holder1.getTitleItemBar().setTitle("最近联系人");
                holder1.getTitleItemBar().setHasMore(false);
                break;
            case TYPE_DIVIDE_MYFRIEND:
                TitleViewHolder holder2 = (TitleViewHolder) holder;
                holder2.getTitleItemBar().setTitle("所有联系人");
                holder2.getTitleItemBar().setHasMore(false);
                break;
            case TYPE_MY_FRIEND:
                MyFriendHolder myFriendHolder = (MyFriendHolder) holder;
                myFriendHolder.setData(entity);
                myFriendHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClick != null) {
                            onItemClick.OnItemClick(entity, position);
                        }
                    }
                });
                break;
            case TYPE_MY_GROUP:
                MyGroupHolder myGroupHolder = (MyGroupHolder) holder;
                myGroupHolder.setData((WeiBoGroup) entity);
                break;
        }
    }

    class MyFriendHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mHead;
        TextView mName;

        public MyFriendHolder(View itemView) {
            super(itemView);
            mHead = (SimpleDraweeView) itemView.findViewById(R.id.weibo_friend_head);
            mName = (TextView) itemView.findViewById(R.id.weibo_friend_name);
        }

        public void setData(WeiBoSearchPeople entity) {
            if (!TextUtils.isEmpty(entity.getHeadPic())) {
                mHead.setImageURI(Uri.parse(entity.getHeadPic()));
            }
            mName.setText(entity.getRealName());
        }
    }

    class MyGroupHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mSimpleDraweeView;
        CheckBox mCheckBox;
        TextView mTextView;

        public MyGroupHolder(View itemView) {
            super(itemView);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.weibo_my_group_simple);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.weibo_my_group_check);
            mTextView = (TextView) itemView.findViewById(R.id.weibo_my_group_textview);
        }

        public void setData(final WeiBoGroup weiBoGroup) {
            mSimpleDraweeView.setImageURI(Uri.parse(weiBoGroup.getGroupPic()));
            mTextView.setText(weiBoGroup.getGroupName());
            if (weiBoGroup.ischeck()) {
                mCheckBox.setChecked(true);
            } else {
                mCheckBox.setChecked(false);
            }
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClick != null) {
                        weiBoGroup.setIscheck(mCheckBox.isChecked());
                        onItemClick.OnGroupCheck(weiBoGroup);
                    }
                }
            });
        }
    }

    public interface OnItemClick {
        void OnItemClick(WeiBoSearchPeople entity, int position);

        void OnGroupCheck(WeiBoGroup weiBoGroup);
    }
}
