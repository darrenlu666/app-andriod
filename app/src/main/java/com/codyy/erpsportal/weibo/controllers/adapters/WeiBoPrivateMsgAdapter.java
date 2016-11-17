package com.codyy.erpsportal.weibo.controllers.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.utils.DateUtil;
import com.codyy.erpsportal.weibo.controllers.fragments.WeiBoImageFilpperDialog;
import com.codyy.erpsportal.weibo.models.entities.WeiBoListInfo;
import com.codyy.erpsportal.weibo.models.entities.WeiBoPrivateMessage;
import com.codyy.erpsportal.commons.widgets.ClickTextView;
import com.codyy.erpsportal.weibo.widgets.ChatImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 私信聊天界面
 * Created by kmdai on 16-1-27.
 */
public class WeiBoPrivateMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int LAST_GONE = 0x002;
    public static final int LOADING = 0x003;
    public static final int NO_MORE = 0x004;
    private List<WeiBoPrivateMessage> mDatas;
    private Context mContext;
    private long mDate;
    private int mRefreshType;

    public WeiBoPrivateMsgAdapter(Context mContext, List<WeiBoPrivateMessage> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mRefreshType = LAST_GONE;
        mDate = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case WeiBoPrivateMessage.MESSAGE_LEFT:
                return new PrivateMessageHoler(LayoutInflater.from(mContext).inflate(R.layout.item_weibo_msg_left, parent, false));
            case WeiBoPrivateMessage.MESSAGE_RIGHT:
                return new PrivateMessageHoler(LayoutInflater.from(mContext).inflate(R.layout.item_weibo_msg_right, parent, false));
            case WeiBoPrivateMessage.TITLE_VIEW:
                return new TitleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_viewholder_last, parent, false));
            case WeiBoPrivateMessage.MESSAGE_LEFT_IMAGE:
                return new PrivateImageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_weibo_msg_left_image, parent, false));
            case WeiBoPrivateMessage.MESSAVE_RIGHT_IMAGE:
                return new PrivateImageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_weibo_right_image, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case WeiBoPrivateMessage.TITLE_VIEW:
                TitleViewHolder holder1 = (TitleViewHolder) holder;
                holder1.setState(mRefreshType);
                break;
            case WeiBoPrivateMessage.MESSAGE_LEFT:
            case WeiBoPrivateMessage.MESSAGE_RIGHT:
                bindViewHolder((PrivateMessageHoler) holder, position);
                break;
            case WeiBoPrivateMessage.MESSAGE_LEFT_IMAGE:
            case WeiBoPrivateMessage.MESSAVE_RIGHT_IMAGE:
                bindImageHolder((PrivateImageHolder) holder, position);
                break;
        }
    }

    private void bindViewHolder(PrivateMessageHoler holder, int position) {
        if (position + 1 < mDatas.size()) {
            mDate = mDatas.get(position + 1).getCreateTime();
        } else {
            mDate = -1;
        }
        holder.setData(mDatas.get(position));
    }

    private void bindImageHolder(PrivateImageHolder holder, int position) {
        if (position + 1 < mDatas.size()) {
            mDate = mDatas.get(position + 1).getCreateTime();
        } else {
            mDate = -1;
        }
        holder.setData(mDatas.get(position));
    }


    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position == mDatas.size() ? WeiBoPrivateMessage.TITLE_VIEW : mDatas.get(position).getType();
    }


    /**
     * 最后显示的viewholder
     */
    class TitleViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;
        TextView mTextView;
        View mItemView;

        public TitleViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.adapter_holder_process);
            mTextView = (TextView) itemView.findViewById(R.id.adapter_holder_textview);
        }

        public void setState(int state) {
            switch (state) {
                case NO_MORE:
                    itemView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setText(mContext.getString(R.string.refresh_state_no_more));
                    break;
                case LOADING:
                    itemView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTextView.setText(mContext.getString(R.string.refresh_state_loading));
                    break;
                case LAST_GONE:
                    itemView.setVisibility(View.GONE);
                    break;
            }
        }

        public void setVisibility(int v) {
            mItemView.setVisibility(v);
        }
    }

    public void setRefreshType(int type) {
        mRefreshType = type;
        notifyItemChanged(getItemCount() - 1);
    }

    class PrivateMessageHoler extends RecyclerView.ViewHolder {
        @Bind(R.id.private_msg_item_time)
        TextView mTitleTime;
        @Bind(R.id.private_msg_item_simpledraweeview)
        SimpleDraweeView mSimpleDraweeView;
        @Bind(R.id.private_msg_item_textcontent)
        ClickTextView mClickTextView;

        public PrivateMessageHoler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(WeiBoPrivateMessage message) {
            if (mDate < 0 || (message.getCreateTime() - mDate) > DateUtil.ONE_MINUTE) {
                mTitleTime.setVisibility(View.VISIBLE);
                mTitleTime.setText(DateUtil.formatTime2(message.getCreateTime()));
            } else {
                mTitleTime.setVisibility(View.GONE);
            }
            mSimpleDraweeView.setImageURI(Uri.parse(message.getSpeakerUserHeadPic()));
            mClickTextView.setEmojiconString(message.getMessageContent());
        }
    }

    class PrivateImageHolder extends RecyclerView.ViewHolder {
        TextView mTitleTime;
        SimpleDraweeView mSimpleDraweeView;
        //        SimpleDraweeView mImageView;
        ChatImageView mChatImageView;

        public PrivateImageHolder(View itemView) {
            super(itemView);
            mTitleTime = (TextView) itemView.findViewById(R.id.private_msg_item_time);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.private_msg_item_simpledraweeview);
//            mImageView = (SimpleDraweeView) itemView.findViewById(R.id.private_msg_item_image);
            mChatImageView = (ChatImageView) itemView.findViewById(R.id.private_msg_item_image);
        }

        void setData(final WeiBoPrivateMessage message) {
            if (mDate < 0 || (message.getCreateTime() - mDate) > DateUtil.ONE_MINUTE) {
                mTitleTime.setVisibility(View.VISIBLE);
                mTitleTime.setText(DateUtil.formatTime2(message.getCreateTime()));
            } else {
                mTitleTime.setVisibility(View.GONE);
            }
            mChatImageView.setStringUrl(getSmall(message.getImagePath()));
            mChatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 0;
                    int count = 0;
                    ArrayList<WeiBoListInfo.ImageListEntity> entities = new ArrayList<>();
                    for (int i = 0; i < mDatas.size(); i++) {
                        if (!TextUtils.isEmpty(mDatas.get(i).getImagePath())) {
                            WeiBoListInfo.ImageListEntity entity = new WeiBoListInfo.ImageListEntity();
                            entity.setImage(mDatas.get(i).getImagePath());
                            if (mDatas.get(i).getImagePath().equals(message.getImagePath())) {
                                index = count;
                            }
                            entities.add(entity);
                            count++;
                        }
                    }
                    WeiBoImageFilpperDialog dialog = WeiBoImageFilpperDialog.newInstance(entities, index);
                    dialog.setOnShowing(new WeiBoImageFilpperDialog.OnShowing() {
                        @Override
                        public View getStartView(int position) {
                            return mChatImageView;
                        }

                        @Override
                        public View getEndView(int position) {
                            return null;
                        }
                    });
                    dialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "dialog----");
                }
            });
            mSimpleDraweeView.setImageURI(Uri.parse(message.getSpeakerUserHeadPic()));
        }

        private String getSmall(String url) {

            StringBuilder stringBuilder = null;
            if (url != null) {
                stringBuilder = new StringBuilder(url);
                int a = stringBuilder.lastIndexOf(".");
                if (a > 0) {
                    return stringBuilder.insert(a, ".small").toString();
                }
            }
            return url;
        }
    }

}
