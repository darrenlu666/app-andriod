package com.codyy.erpsportal.commons.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.commons.controllers.activities.LoginActivity;
import com.codyy.erpsportal.groups.controllers.activities.GroupSpaceActivity;
import com.codyy.erpsportal.commons.controllers.fragments.filters.CategoryFilterFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.resource.models.entities.Resource;
import com.codyy.erpsportal.commons.models.entities.SearchBase;
import com.codyy.erpsportal.commons.models.entities.SearchGroup;
import com.codyy.erpsportal.commons.models.entities.SearchHistory;
import com.codyy.erpsportal.commons.models.entities.SearchResource;
import com.codyy.erpsportal.commons.models.entities.SearchVideo;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 搜索adapter
 * Created by kmdai on 2015/8/27.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<SearchBase> searchBases;

    private OnHistoryClickListener listener;

    public SearchAdapter(Context mContext, ArrayList<SearchBase> searchBases) {
        this.mContext = mContext;
        this.searchBases = searchBases;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SearchBase.HISTORY_TITLE:
                return new HistoryTitle(LayoutInflater.from(mContext).inflate(R.layout.search_history, parent, false));
            case SearchBase.HISTORY_CONT:
                return new HistoryCont(LayoutInflater.from(mContext).inflate(R.layout.search_history, parent, false));
            case SearchBase.HISTORY_DELETE:
                return new HistoryDelete(LayoutInflater.from(mContext).inflate(R.layout.search_history, parent, false));
            case SearchBase.RESOURCE_TITLE:
                TextView textView = new TextView(mContext);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(mContext, 50)));
                textView.setTextColor(Color.parseColor("#929292"));
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(18);
                return new RecyclerView.ViewHolder(textView) {
                };
            case SearchBase.RESOURCE_CONT:
                return new ResourceContHolder(LayoutInflater.from(mContext).inflate(R.layout.search_resource_itemlayout, parent, false));
            case SearchBase.VIDEO_CONT:
            case SearchBase.DOC_CONT:
                return new VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.search_layout_video, parent, false));
            case SearchBase.GROUP_CONT:
                return new GroupCont(LayoutInflater.from(mContext).inflate(R.layout.search_group_itemlayout, parent, false));
        }
        return new RecyclerView.ViewHolder(new View(mContext)) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case SearchBase.HISTORY_TITLE:
                HistoryTitle title = (HistoryTitle) holder;
                title.mTextView.setText(searchBases.get(position).getmTitle());
                break;
            case SearchBase.HISTORY_CONT:
                HistoryCont cont = (HistoryCont) holder;
                final SearchHistory searchHistory = (SearchHistory) searchBases.get(position);
                cont.mTextView.setText(searchHistory.getCont());
                cont.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.deleteOne(searchHistory, position);
                        }
                    }
                });
                cont.mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onHistoryClick(searchHistory);
                        }
                    }
                });
                break;
            case SearchBase.HISTORY_DELETE:
                HistoryDelete delete = (HistoryDelete) holder;
                delete.mTextView.setText(searchBases.get(position).getmTitle());
                delete.mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.deleteAll();
                        }
                    }
                });
                break;
            case SearchBase.RESOURCE_TITLE:
                TextView textView = (TextView) holder.itemView;
                textView.setText(searchBases.get(0).getTotal() != 0 ? "共搜索出" + searchBases.get(0).getTotal() + "条结果" : "暂时没有相关内容");
                break;
            case SearchBase.RESOURCE_CONT:
                final SearchResource searchResource = (SearchResource) searchBases.get(position);
                ResourceContHolder resourceContHolder = (ResourceContHolder) holder;
                if (!"null".equals(searchResource.getThumb())) {
                    resourceContHolder.mSimpledraweeview.setVisibility(View.VISIBLE);
                    resourceContHolder.mSimpledraweeview.setImageURI(Uri.parse(searchResource.getThumb()));
                } else {
                    resourceContHolder.mSimpledraweeview.setVisibility(View.GONE);
                }
                resourceContHolder.mTextTitle.setText(searchResource.getTitle());
                resourceContHolder.mTextCont.setText(searchResource.getContent());
                resourceContHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InfoDetailActivity.startFromFunction(mContext, searchResource.getInformationId());
                    }
                });
                break;
            case SearchBase.VIDEO_CONT:
            case SearchBase.DOC_CONT:
                final SearchVideo video = (SearchVideo) searchBases.get(position);
                VideoHolder videoHolder = (VideoHolder) holder;
                videoHolder.mVideoSimple.setImageURI(Uri.parse(video.getThumbPath()));
                videoHolder.mVideoTextTitel.setText(video.getResourceName());
                videoHolder.mVideoTextPublicname.setText("发布人：" + video.getCreateUserName());
                if (getItemViewType(position) == SearchBase.DOC_CONT) {
                    videoHolder.mVideoTextNumber.setCompoundDrawables(null, null, null, null);
                }
                videoHolder.mVideoTextNumber.setText(video.getViewCnt() + "次");
//                double a = Math.ceil(video.getRatingAvg()) / 2;
//                videoHolder.mVideoTatingbar.setRating((float) a);
                videoHolder.mRatingAvg.setText(video.getRatingAvg() + "分");
                videoHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Resource resource = new Resource();
                        if (UserInfoKeeper.getInstance().getUserInfo() == null) {
                            LoginActivity.startNoBack(mContext);
                        } else {
                            if (SearchBase.VIDEO_CONT == video.getmType()) {
                                resource.setType(Resource.TYPE_VIDEO);
                            } else {
                                resource.setType(Resource.TYPE_DOCUMENT);
                            }
                            resource.setId(video.getResourceId());
                            Resource.gotoResDetails((Activity) mContext, UserInfoKeeper.getInstance().getUserInfo(), resource);
                        }
                    }
                });
                break;
            case SearchBase.GROUP_CONT:
                ((GroupCont) holder).setData((SearchGroup) searchBases.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return searchBases.size();
    }

    @Override
    public int getItemViewType(int position) {
        return searchBases.get(position).getmType();
    }

    class HistoryTitle extends RecyclerView.ViewHolder {

        @Bind(R.id.search_history_text)
        TextView mTextView;
        @Bind(R.id.search_history_image)
        ImageView mImageView;

        public HistoryTitle(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mImageView.setVisibility(View.GONE);
//            mTextView.setCompoundDrawables(mContext.getResources().getDrawable(R.drawable.delete_garbage), null, null, null);
            mTextView.setTextColor(mContext.getResources().getColor(R.color.main_color));
        }
    }

    class HistoryCont extends RecyclerView.ViewHolder {
        @Bind(R.id.search_history_text)
        TextView mTextView;
        @Bind(R.id.search_history_image)
        ImageView mImageView;

        public HistoryCont(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class GroupCont extends RecyclerView.ViewHolder {
        SimpleDraweeView mHead;
        TextView mCreate;
        TextView mTitle;
        TextView mSubject;
        TextView mlimit;

        public GroupCont(View itemView) {
            super(itemView);
            init();
        }

        private void init() {
            mHead = (SimpleDraweeView) itemView.findViewById(R.id.search_group_head);
            mCreate = (TextView) itemView.findViewById(R.id.search_group_create);
            mTitle = (TextView) itemView.findViewById(R.id.search_group_title);
            mSubject = (TextView) itemView.findViewById(R.id.search_group_subject);
            mlimit = (TextView) itemView.findViewById(R.id.search_group_limit);
        }

        public void setData(final SearchGroup searchGroup) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupSpaceActivity.start(mContext, "圈组", searchGroup.getGroupId(), CategoryFilterFragment.CATEGORY_TYPE_DOOR);
                }
            });
            mHead.setImageURI(Uri.parse(searchGroup.getPic()));
            mTitle.setText(searchGroup.getGroupName());
            if ("INTEREST".equals(searchGroup.getGroupType())) {
                mSubject.setText((TextUtils.isEmpty(searchGroup.getCategoryName()) ? "无" : searchGroup.getCategoryName()));
            } else {
                mSubject.setText(((TextUtils.isEmpty(searchGroup.getClasslevelName()) ? "无" : searchGroup.getClasslevelName())) + "/" + (TextUtils.isEmpty(searchGroup.getSubjectName()) ? "无" : searchGroup.getSubjectName()));
            }
            mCreate.setText((TextUtils.isEmpty(searchGroup.getRealName()) ? "未知" : searchGroup.getRealName()));
            if (searchGroup.getLimited() > 0) {
                mlimit.setText(searchGroup.getMemberCount() + "/" + searchGroup.getLimited() + "人");
            } else {
                mlimit.setText(searchGroup.getMemberCount() + "/不限");
            }
        }
    }

    class HistoryDelete extends RecyclerView.ViewHolder {
        @Bind(R.id.search_history_text)
        TextView mTextView;
        @Bind(R.id.search_history_image)
        ImageView mImageView;

        public HistoryDelete(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.delete_garbage);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
            mTextView.setCompoundDrawables(drawable, null, null, null);//画在左边
//            mTextView.setCompoundDrawables(mContext.getResources().getDrawable(R.drawable.delete_garbage), null, null, null);
            mTextView.setTextColor(mContext.getResources().getColor(R.color.main_color));
            mImageView.setVisibility(View.GONE);
            mTextView.setCompoundDrawablePadding(20);
        }
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'search_resource_itemlayout.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class ResourceContHolder extends RecyclerView.ViewHolder {
        View mView;
        @Bind(R.id.search_resource_simpledraweeview)
        SimpleDraweeView mSimpledraweeview;
        @Bind(R.id.search_resource_text_title)
        TextView mTextTitle;
        @Bind(R.id.search_resource_text_cont)
        TextView mTextCont;

        ResourceContHolder(View view) {
            super(view);
            mView = itemView;
            ButterKnife.bind(this, view);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'search_layout_video.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class VideoHolder extends RecyclerView.ViewHolder {
        View mView;
        @Bind(R.id.search_layout_video_simple)
        SimpleDraweeView mVideoSimple;
        @Bind(R.id.search_layout_video_text_titel)
        TextView mVideoTextTitel;
        @Bind(R.id.search_layout_video_text_publicname)
        TextView mVideoTextPublicname;
        @Bind(R.id.search_layout_video_text_number)
        TextView mVideoTextNumber;
        //        @Bind(R.id.search_layout_video_tatingbar)
//        RatingBar mVideoTatingbar;
        @Bind(R.id.search_layout_video_ratingAvg)
        TextView mRatingAvg;

        VideoHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = itemView;
        }
    }

    public OnHistoryClickListener getListener() {
        return listener;
    }

    public void setListener(OnHistoryClickListener listener) {
        this.listener = listener;
    }

    public interface OnHistoryClickListener {
        void deleteOne(SearchHistory str, int position);

        void deleteAll();

        void onHistoryClick(SearchHistory history);
    }


}
