package com.codyy.erpsportal.weibo.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.RefreshEntity;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoPrivateMessageActivity;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoUniversalFragment;
import com.codyy.erpsportal.weibo.models.entities.WeiBoMessage;
import com.codyy.erpsportal.weibo.models.entities.WeiBoSearchPeople;
import com.codyy.erpsportal.commons.widgets.ClickTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kmdai on 15-12-25.
 */
public class WeiBoUniversalAdapter extends RefreshBaseAdapter {
    private Context mContext;
    private OnItemClick mOnItemClick;

    public WeiBoUniversalAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    public WeiBoUniversalAdapter(Context mContext, List mDatas) {
        super(mContext, mDatas);
        this.mContext = mContext;
    }

    public OnItemClick getOnItemClick() {
        return mOnItemClick;
    }

    public void setOnItemClick(OnItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder getHolderView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        switch (viewType) {
            case WeiBoUniversalFragment.TYPE_FIND_PEOPLE:
            case WeiBoUniversalFragment.TYPE_MY_FANS:
            case WeiBoUniversalFragment.TYPE_MY_FOLLOW:
                return new FindPeopleViewHolder(inflater.inflate(R.layout.item_weibo_searchpeople, parent, false), viewType);
            case WeiBoUniversalFragment.TYPE_MY_MSG:
                return new MyMsgHolder(inflater.inflate(R.layout.item_weibo_message, parent, false), viewType);
        }
        return new RecyclerView.ViewHolder(new View(mContext)) {
        };
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position, RefreshEntity entity) {
        switch (getItemViewType(position)) {
            case WeiBoUniversalFragment.TYPE_MY_FANS:
            case WeiBoUniversalFragment.TYPE_MY_FOLLOW:
            case WeiBoUniversalFragment.TYPE_FIND_PEOPLE:
                FindPeopleViewHolder findPeopleViewHolder = (FindPeopleViewHolder) holder;
                findPeopleViewHolder.setData((WeiBoSearchPeople) entity);
                break;
            case WeiBoUniversalFragment.TYPE_MY_MSG:
                MyMsgHolder holder1 = (MyMsgHolder) holder;
                holder1.setData((WeiBoMessage) entity);
                break;
        }
    }

    class FindPeopleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.searchpeople_name)
        TextView mTextName;
        @Bind(R.id.searchpeople_school)
        TextView mTextSchool;
        @Bind(R.id.searchpeople_grade)
        TextView mTextGrade;
        @Bind(R.id.simpledraweeview_searchpeople)
        SimpleDraweeView mSimpleDraweeView;
        @Bind(R.id.searchpeople_setting)
        ImageView mImageView;
        @Bind(R.id.searchpeople_following)
        Button mBtmFollow;
        TextView mPopuRightMenu;
        PopupWindow mPopupWindow;
        int mPopuStart;
        int mPopuEnd;
        WeiBoSearchPeople mWeiBoSearchPeople;
        int mType;

        public FindPeopleViewHolder(View itemView, final int type) {
            super(itemView);
            mType = type;
            ButterKnife.bind(this, itemView);
            View view = LayoutInflater.from(mContext).inflate(R.layout.weibo_popuwindow, null);
            mPopupWindow = new PopupWindow(view, UIUtils.dip2px(mContext, 150), UIUtils.dip2px(mContext, 40));
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            mPopupWindow.setTouchable(true);
            mPopupWindow.setAnimationStyle(R.style.weibo_popu_anim);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopuStart = -UIUtils.dip2px(mContext, 135);
            mPopuRightMenu = (TextView) view.findViewById(R.id.weibo_popu_unfollow);
            mPopuRightMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    mOnItemClick.onItemClick(mWeiBoSearchPeople, type, getAdapterPosition());
                }
            });
            view.findViewById(R.id.weibo_popu_msg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                        WeiBoPrivateMessageActivity.start(mContext, mWeiBoSearchPeople.getUserId(), mWeiBoSearchPeople.getRealName());
                    }
                }
            });
        }

        void setData(final WeiBoSearchPeople weiBoSearchPeople) {
            mWeiBoSearchPeople = weiBoSearchPeople;
            mTextName.setText(weiBoSearchPeople.getRealName());
            mTextSchool.setText(weiBoSearchPeople.getSchoolName());
            mSimpleDraweeView.setImageURI(Uri.parse(weiBoSearchPeople.getHeadPic()));
            mTextGrade.setText(weiBoSearchPeople.getBaseClassName());
            mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClick != null) {
                        mOnItemClick.onHeadClick(weiBoSearchPeople.getUserId());
                    }
                }
            });
            switch (mType) {
                case WeiBoUniversalFragment.TYPE_FIND_PEOPLE:
                    mBtmFollow.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                    if (weiBoSearchPeople.isFollowFlag()) {
                        mBtmFollow.setText(mContext.getString(R.string.weibo_has_add_friend));
                        mBtmFollow.setTextColor(mContext.getResources().getColor(R.color.divider));
                        mBtmFollow.setBackgroundResource(R.drawable.weibo_follow_true);
                    } else {
                        mBtmFollow.setText(mContext.getString(R.string.weibo_add_friend));
                        mBtmFollow.setTextColor(mContext.getResources().getColor(R.color.white));
                        mBtmFollow.setBackgroundResource(R.drawable.weibo_follow_bg);
                    }
                    mBtmFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnItemClick != null) {
                                mOnItemClick.onItemClick(weiBoSearchPeople, mType, getAdapterPosition());
                            }
                        }
                    });
                    break;
                case WeiBoUniversalFragment.TYPE_MY_FOLLOW:
                    mBtmFollow.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopu(v);
                        }
                    });
                    break;
                case WeiBoUniversalFragment.TYPE_MY_FANS:
                    mPopuRightMenu.setText(mContext.getString(R.string.weibo_delete_fans));
                    mBtmFollow.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopu(v);
                        }
                    });
                    break;
            }
        }

        void showPopu(View v) {
            mPopuEnd = -UIUtils.dip2px(mContext, 20) - mImageView.getHeight() / 2;
            mPopupWindow.showAsDropDown(v, mPopuStart, mPopuEnd);
        }
    }

    class MyMsgHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.searchpeople_name)
        TextView mTextName;
        @Bind(R.id.searchpeople_message)
        ClickTextView mTextMsg;
        @Bind(R.id.simpledraweeview_searchpeople)
        SimpleDraweeView mSimpleDraweeView;
        @Bind(R.id.searchpeople_setting)
        ImageView mImageSetting;
        @Bind(R.id.imageview_shield)
        ImageView mImageShield;
        TextView mPopuRightMenu;
        TextView mPopuLeftMenu;
        PopupWindow mPopupWindow;
        int mPopuStart;
        WeiBoMessage mMessage;
        int mType;

        public MyMsgHolder(View itemView, int type) {
            super(itemView);
            mType = type;
            ButterKnife.bind(this, itemView);
            View view = LayoutInflater.from(mContext).inflate(R.layout.weibo_popuwindow, null);
            mPopupWindow = new PopupWindow(view, UIUtils.dip2px(mContext, 150), UIUtils.dip2px(mContext, 40));
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            mPopupWindow.setTouchable(true);
            mPopupWindow.setAnimationStyle(R.style.weibo_popu_anim);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopuStart = -UIUtils.dip2px(mContext, 135);
            mPopuRightMenu = (TextView) view.findViewById(R.id.weibo_popu_unfollow);
            mPopuRightMenu.setText(mContext.getString(R.string.weibo_delete));
            mPopuRightMenu = (TextView) view.findViewById(R.id.weibo_popu_unfollow);
            mPopuRightMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClick.onItemClick(mMessage, WeiBoUniversalFragment.DELETE_MY_MSG, getAdapterPosition());
                    if (mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                }
            });
            mPopuLeftMenu = (TextView) view.findViewById(R.id.weibo_popu_msg);
            mPopuLeftMenu.setText(mContext.getString(R.string.weibo_shield_msg));
            mPopuLeftMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClick.onItemClick(mMessage, mType, getAdapterPosition());
                    if (mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                }
            });
            mImageSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.showAsDropDown(v, mPopuStart, -UIUtils.dip2px(mContext, 20) - v.getHeight() / 2);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeiBoPrivateMessageActivity.start(mContext, mMessage.getTargetUserId(), mMessage.getTargetRealName());
                }
            });
        }

        void setData(final WeiBoMessage message) {
            mMessage = message;
            mSimpleDraweeView.setImageURI(Uri.parse(message.getTargetHeadPic()));
            mTextName.setText(message.getTargetRealName());
            if ("N".equals(message.getReadFlag())) {
                mTextName.setTextColor(mContext.getResources().getColor(R.color.main_color));
            } else {
                mTextName.setTextColor(mContext.getResources().getColor(R.color.primary_text));
            }
            mTextMsg.setEmojiconString(message.getMessageContent());
            if (!TextUtils.isEmpty(message.getBlackUserId())) {
                mPopuLeftMenu.setText(mContext.getString(R.string.weibo_delete_shield_msg));
                mImageShield.setVisibility(View.VISIBLE);
            } else {
                mPopuLeftMenu.setText(mContext.getString(R.string.weibo_shield_msg));
                mImageShield.setVisibility(View.GONE);
            }
            mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClick != null) {
                        mOnItemClick.onHeadClick(message.getTargetUserId());
                    }
                }
            });
        }
    }

    public interface OnItemClick {
        void onItemClick(RefreshEntity refreshEntity, int type, int position);

        void onHeadClick(String id);
    }
}
