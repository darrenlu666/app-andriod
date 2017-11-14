package com.codyy.erpsportal.weibo.controllers.adapters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.MainActivity;
import com.codyy.erpsportal.commons.controllers.activities.PublicUserActivity;
import com.codyy.erpsportal.commons.controllers.adapters.RefreshBaseAdapter;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.services.WeiBoMediaService;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoActivity;
import com.codyy.erpsportal.weibo.controllers.activities.WeiBoCommentActivity;
import com.codyy.erpsportal.weibo.models.entities.WeiBoListInfo;
import com.codyy.erpsportal.weibo.widgets.WeiBoMediaView;
import com.codyy.erpsportal.commons.widgets.ClickTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by kmdai on 15-12-23.
 */
public class WeiBoAdapter extends RefreshBaseAdapter<WeiBoListInfo> implements WeiBoMediaView.onMediaPlay {
    private Context mContext;
    private OnItemClick mOnItemClick;
    private int mWeiBoType;
    private int mIndex = -1;
    private WeiBoMediaService.MediaBinder mMediaBinder;
    private UserInfo mUserInfo;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (WeiBoMediaService.MediaBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notifyItemChanged(mIndex);
            mIndex = -1;
        }
    };

    public WeiBoAdapter(Context mContext, List<WeiBoListInfo> mDatas, int mWeiBoType) {
        super(mContext, mDatas);
        this.mContext = mContext;
        this.mWeiBoType = mWeiBoType;
        mUserInfo = UserInfoKeeper.obtainUserInfo();
        Intent intent = new Intent(mContext, WeiBoMediaService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter(WeiBoMediaService.ACTION_ON_STOP);

        mContext.registerReceiver(broadcastReceiver, intentFilter);
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
            case WeiBoListInfo.TYPE_ORIGINAL:
                return new WeiBoViewHolder(inflater.inflate(R.layout.item_weibo_list, parent, false), WeiBoListInfo.TYPE_ORIGINAL);
            case WeiBoListInfo.TYPE_TURN:
                return new WeiBoViewHolder(inflater.inflate(R.layout.item_weibo_turn_list, parent, false), WeiBoListInfo.TYPE_TURN);
        }
        return new WeiBoViewHolder(inflater.inflate(R.layout.item_weibo_list, parent, false), WeiBoListInfo.TYPE_ORIGINAL);
    }


    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position, WeiBoListInfo entity) {
        switch (getItemViewType(position)) {
            case WeiBoListInfo.TYPE_ORIGINAL:
            case WeiBoListInfo.TYPE_TURN:
                WeiBoViewHolder holder1 = (WeiBoViewHolder) holder;
                holder1.setData(entity, position);
                break;
        }

    }

    /**
     * 设置微博音视频显示
     *
     * @param mediaView
     * @param weiBoListInfo
     * @param position
     */
    void setWeiBoMedia(WeiBoMediaView mediaView, final WeiBoListInfo weiBoListInfo, int position) {
        if (weiBoListInfo.getImageList() != null && weiBoListInfo.getImageList().size() != 0) {
            mediaView.setVisibility(View.VISIBLE);
            mediaView.setListEntities(weiBoListInfo.getImageList());
        } else if (!TextUtils.isEmpty(weiBoListInfo.getAudioName())) {
            mediaView.setVisibility(View.VISIBLE);
            if (position == mIndex) {
                mediaView.setAudio(weiBoListInfo.getAudioName(), WeiBoMediaView.TYPE_PAUSE, position);
            } else {
                mediaView.setAudio(weiBoListInfo.getAudioName(), WeiBoMediaView.TYPE_PLAY, position);
            }
            mediaView.setOnMediaPlay(this);
        } else if (!TextUtils.isEmpty(weiBoListInfo.getVideoName())) {
            mediaView.setVisibility(View.VISIBLE);
            mediaView.setVideo(weiBoListInfo.getVideoName(), UIUtils.getSmallImage(weiBoListInfo.getThumb()), false, weiBoListInfo.getTransFlag());
        } else {
            mediaView.setVisibility(View.GONE);
        }
    }

    @Override
    public void play(String url, int position) {
        if (mIndex > 0 && position != mIndex) {
            notifyItemChanged(mIndex);
        }
        mIndex = position;
        if (mMediaBinder != null) {
            mMediaBinder.play(url);

        }
    }

    @Override
    public void pause(int position) {
        mIndex = -1;
        notifyItemChanged(position);
        if (mMediaBinder != null) {
            mMediaBinder.stop();
        }
    }

    class WeiBoViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mHeadImg;
        TextView mNameTextView;
        TextView mTimeTextView;
        ClickTextView mContTextView;
        ClickTextView mTrunTextView;
        TextView mShareTextView;
        TextView mCommentTextView;
        TextView mAgreeTextView;
        ImageView mAgreeImageView;
        WeiBoMediaView mWeiBoImage;
        LinearLayout mAgreeLayout;
        LinearLayout mConnentLayout;
        LinearLayout mTurnLayout;
        ImageView mPopuMenu;
        int mType;
        ClickTextView.OnClick mClick = new ClickTextView.OnClick() {
            @Override
            public void OnClick(String url) {
                goUserDetail(getUserId(url));
            }
        };

        public WeiBoViewHolder(View itemView, int type) {
            super(itemView);
            mType = type;
            init(itemView);
        }

        void init(View itemView) {
            mHeadImg = (SimpleDraweeView) itemView.findViewById(R.id.item_weibo_list_simpledraweeview);
            mNameTextView = (TextView) itemView.findViewById(R.id.item_weibo_list_textview_name);
            mTimeTextView = (TextView) itemView.findViewById(R.id.item_weibo_list_textview_date);
            mContTextView = (ClickTextView) itemView.findViewById(R.id.item_weibo_list_textview_cont);
            mShareTextView = (TextView) itemView.findViewById(R.id.item_textview_share);
            mCommentTextView = (TextView) itemView.findViewById(R.id.item_textview_comment);
            mAgreeTextView = (TextView) itemView.findViewById(R.id.item_textview_agree);
            mAgreeImageView = (ImageView) itemView.findViewById(R.id.item_image_agree);
            mWeiBoImage = (WeiBoMediaView) itemView.findViewById(R.id.item_weibo_list_weibo_image);
            mAgreeLayout = (LinearLayout) itemView.findViewById(R.id.item_agree_layout);
            mConnentLayout = (LinearLayout) itemView.findViewById(R.id.item_comment_layout);
            mPopuMenu = (ImageView) itemView.findViewById(R.id.item_image_arrows);
            mTurnLayout = (LinearLayout) itemView.findViewById(R.id.item_turn_layout);
            switch (mType) {
                case WeiBoListInfo.TYPE_TURN:
                    mTrunTextView = (ClickTextView) itemView.findViewById(R.id.item_weibo_list_textview_turn_cont);
                    mTrunTextView.setmOnClick(mClick);
                    break;
            }
            mContTextView.setmOnClick(mClick);
        }

        private void goUserDetail(String id) {
            if (mUserInfo.getBaseUserId().equals(id)) {
                MainActivity.start((Activity) mContext, UserInfoKeeper.obtainUserInfo(), 2);
            } else {//2.访客
                PublicUserActivity.start((Activity) mContext, id);
            }
        }

        void setData(final WeiBoListInfo weiBoListInfo, int position) {
            mHeadImg.setImageURI(Uri.parse(weiBoListInfo.getHeadPic()));
            mHeadImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goUserDetail(weiBoListInfo.getBaseUserId());
                }
            });
            mNameTextView.setText(weiBoListInfo.getRealName());
            mNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goUserDetail(weiBoListInfo.getBaseUserId());
                }
            });
            mTimeTextView.setText(DateUtil.formatTime(weiBoListInfo.getPublishTime()));
            int agree = weiBoListInfo.getAgreeCount();
            if (agree > 0) {
                mAgreeTextView.setText(String.valueOf(agree));
            } else {
                mAgreeTextView.setText(mContext.getResources().getString(R.string.weibo_list_item_favour));
            }
            int comment = weiBoListInfo.getCommentCount();
            if (comment > 0) {
                mCommentTextView.setText(String.valueOf(comment));
            } else {
                mCommentTextView.setText(mContext.getString(R.string.weibo_list_item_comment));
            }

            if ("N".equals(weiBoListInfo.getAgreeFlag())) {
                mAgreeImageView.setImageResource(R.drawable.weibo_favour_true);
            } else {
                mAgreeImageView.setImageResource(R.drawable.weibo_favour);
            }
            switch (mType) {
                case WeiBoListInfo.TYPE_TURN:
                    mTrunTextView.setVisibility(View.VISIBLE);
                    if (weiBoListInfo.getOriginalMiblogBlog() != null && !"Y".equals(weiBoListInfo.getOriginalDelFlag())) {
                        mWeiBoImage.setVisibility(View.VISIBLE);
                        setWeiBoMedia(mWeiBoImage, weiBoListInfo.getOriginalMiblogBlog(), position);
                        mTrunTextView.setHtmlString(weiBoListInfo.getOriginalMiblogBlog().getBlogContent());
                    } else {
                        mWeiBoImage.setVisibility(View.GONE);
                        mTrunTextView.setHtmlString(mContext.getString(R.string.weibo_original_delete));
                    }
                    break;
                case WeiBoListInfo.TYPE_ORIGINAL:
                    setWeiBoMedia(mWeiBoImage, weiBoListInfo, position);
                    break;
            }
            if (!"null".equals(weiBoListInfo.getBlogContent())) {
                mContTextView.setHtmlString(weiBoListInfo.getBlogContent());
            } else {
                mContTextView.setHtmlString(mContext.getString(R.string.weibo_turn_blog));
            }

            mConnentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("N".equals(weiBoListInfo.getLocked())) {
                        switch (mWeiBoType) {
                            case WeiBoActivity.TYPE_GROUP:
                            case WeiBoActivity.TYPE_GROUP_VISITOR:
                            case WeiBoActivity.TYPE_GROUP_MANAGER:
                                WeiBoCommentActivity.start(mContext, weiBoListInfo.getGroupMiblogId(), mWeiBoType);
                                break;
                            case WeiBoActivity.TYPE_PERSONAL:
                            case WeiBoActivity.TYPE_PERSNOAL_MY:
                            case WeiBoActivity.TYPE_VISITOR:
                                WeiBoCommentActivity.start(mContext, weiBoListInfo.getMiblogId(), mWeiBoType);
                                break;
                        }
                    } else {
                        if (itemView != null) {
                            Snackbar.make(itemView, mContext.getString(R.string.weibo_has_lock), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            mAgreeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("N".equals(weiBoListInfo.getLocked())) {//微博是否锁定
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.weibo_favour_anim);
                        mAgreeImageView.startAnimation(animation);
                        if ("N".equals(weiBoListInfo.getAgreeFlag()) && mOnItemClick != null) {
                            mOnItemClick.unAgreeWeibo(weiBoListInfo);
                        } else if (mOnItemClick != null) {
                            mOnItemClick.agreeWeibo(weiBoListInfo);
                        }
                    } else {
                        if (itemView != null) {
                            Snackbar.make(itemView, mContext.getString(R.string.weibo_has_lock), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            if (mWeiBoType == WeiBoActivity.TYPE_VISITOR) {
                mPopuMenu.setVisibility(View.GONE);
            }
            if (mWeiBoType == WeiBoActivity.TYPE_GROUP || mWeiBoType == WeiBoActivity.TYPE_GROUP_VISITOR || mWeiBoType == WeiBoActivity.TYPE_GROUP_MANAGER) {
                mTurnLayout.setVisibility(View.GONE);
            } else {
                mShareTextView.setVisibility(View.VISIBLE);
                mTurnLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("N".equals(weiBoListInfo.getLocked())) {
                            if (mOnItemClick != null) {
                                mOnItemClick.turnWeiBo(weiBoListInfo);
                            }
                        } else {
                            if (itemView != null) {
                                Snackbar.make(itemView, mContext.getString(R.string.weibo_has_lock), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
            mPopuMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("N".equals(weiBoListInfo.getLocked())) {
                        if (mOnItemClick != null) {
                            mOnItemClick.onMenuClick(weiBoListInfo);
                        }
                    } else {
                        if (itemView != null) {
                            Snackbar.make(itemView, mContext.getString(R.string.weibo_has_lock), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        /**
         * null/toUserIndex/f56441ec2e07480c8c92ef1428f24193.html
         *
         * @param url
         * @return
         */
        private String getUserId(String url) {
            return url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
        }
    }

    public void onDestroy() {
        mContext.unbindService(mServiceConnection);
        mContext.unregisterReceiver(broadcastReceiver);
    }

    public interface OnItemClick {
        void agreeWeibo(WeiBoListInfo weiBoListInfo);

        void unAgreeWeibo(WeiBoListInfo weiBoListInfo);

        void onMenuClick(WeiBoListInfo weiBoListInfo);

        void turnWeiBo(WeiBoListInfo weiBoListInfo);
    }
}
