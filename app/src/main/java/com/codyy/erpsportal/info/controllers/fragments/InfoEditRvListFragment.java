package com.codyy.erpsportal.info.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.LoadMoreFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BindingRvHolder;
import com.codyy.erpsportal.commons.controllers.viewholders.ShareDataVhrCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.ViewHolderCreator;
import com.codyy.erpsportal.commons.controllers.viewholders.annotation.LayoutId;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.info.controllers.fragments.InfoEditRvListFragment.InfoView1Holder;
import com.codyy.erpsportal.info.models.entities.InfoItem;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 应用资讯列表
 * Created by gujiajia on 2017/3/1.
 */

public class InfoEditRvListFragment extends LoadMoreFragment<InfoItem, InfoView1Holder> {

    public final static String ARG_USER_INFO = "user_info";

    public final static String ARG_TYPE = "type";

    private UserInfo mUserInfo;

    private String mType;

    private SelectingFlag mSelecting;

    private AllSelectedListener mAllSelectedListener;

    public static InfoEditRvListFragment newInstance(UserInfo userInfo, String type){
        InfoEditRvListFragment fragment = new InfoEditRvListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_USER_INFO, userInfo);
        bundle.putString(ARG_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public InfoEditRvListFragment() {
        mSelecting = new SelectingFlag(false);
        mSelecting.listener = new SelectingListener() {
            @Override
            public void onSelectedChanged(boolean selecting) {
                if (mAllSelectedListener == null) return;
                if (selecting) {
                    for (int i=0; i<mAdapter.getItemCount(); i++) {
                        InfoItem infoItem = mAdapter.obtainItem(i);
                        if (!infoItem.isSelected()) {
                            return;
                        }
                    }
                    mAllSelectedListener.onSelectAll();
                } else {
                    mAllSelectedListener.onCancelSelectAll();
                }
            }
        };
    }

    public void setAllSelectedListener(AllSelectedListener allSelectedListener) {
        mAllSelectedListener = allSelectedListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = getArguments().getParcelable(ARG_USER_INFO);
        mType = getArguments().getString(ARG_TYPE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setBackgroundColor(
                ResourcesCompat.getColor(getResources(), R.color.md_grey_300, null));
    }

    @Override
    protected ViewHolderCreator<InfoView1Holder> newViewHolderCreator() {
        return new ShareDataVhrCreator<>(InfoView1Holder.class, SelectingFlag.class, mSelecting);
    }

    @Override
    protected String getUrl() {
        return URLConfig.APP_INFO;
    }

    @Override
    protected List<InfoItem> getList(JSONObject response) {
        JSONArray jsonArray = response.optJSONArray("list");
        return InfoItem.parseJsonArray(jsonArray);
    }

    @Override
    protected void addParams(Map<String, String> params) {
        params.put("uuid", mUserInfo.getUuid());
        params.put("infoType", mType);
    }

    public void setSelecting(boolean selecting) {
        mSelecting.setSelecting(selecting);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 全选与全不选
     * @param selectAll true全选 false全不选
     */
    public void selectAll(boolean selectAll) {
        for (int i=0; i<mAdapter.getItemCount(); i++) {
            InfoItem infoItem = mAdapter.obtainItem(i);
            infoItem.setIsSelected(selectAll);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取选中的id们
     * @return id1,id2,id3,id4形式字串
     */
    public String obtainSelectedIdsStr() {
        int selectCount = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = mAdapter.getItemCount() - 1; i >= 0; i--) {
            InfoItem news = mAdapter.obtainItem(i);
            if (news.isSelected()) {
                selectCount ++;
                sb.append(news.getInformationId()).append(',');
            }
        }

        if (selectCount == 0){
            return null;
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    protected void onLoadMoreSuccess() {
        super.onLoadMoreSuccess();
        if (mAllSelectedListener != null) {
            mAllSelectedListener.onCancelSelectAll();
        }
    }

    @Override
    protected void onRefreshSuccess() {
        super.onRefreshSuccess();
        if (mAllSelectedListener != null) {
            mAllSelectedListener.onCancelSelectAll();
        }
    }

    @LayoutId(R.layout.item_news_notify)
    public static class InfoView1Holder extends BindingRvHolder<InfoItem> {

        @Bind(R.id.cb_select)
        CheckBox selectCb;

        @Bind(R.id.dv_icon)
        SimpleDraweeView iconDv;

        @Bind(R.id.tv_title)
        TextView titleTv;

        @Bind(R.id.tv_intro)
        TextView introTv;

        SelectingFlag selectingFlag;

        public InfoView1Holder(View itemView, SelectingFlag selectingFlag) {
            super(itemView);
            this.selectingFlag = selectingFlag;
        }

        @Override
        public void setDataToView(final InfoItem data, int position) {
            titleTv.setText(data.getTitle());
            String content = data.getContent();
            if (TextUtils.isEmpty(content)) {
                content = "";
            }
            introTv.setText(content);
            if (!TextUtils.isEmpty(data.getThumb())) {
                iconDv.setVisibility(View.VISIBLE);
                ImageFetcher.getInstance(iconDv).fetchSmall(iconDv, data.getThumb());
            } else {
                iconDv.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectingFlag.isSelecting()) {
                        boolean originalChecked = selectCb.isChecked();
                        selectCb.setChecked(!originalChecked);
                        checkItem(data, !originalChecked);
                    } else {
                        InfoDetailActivity.startFromFunction(v.getContext(), data.getInformationId());
                    }
                }
            });

            if (selectingFlag.isSelecting()) {
                selectCb.setVisibility(View.VISIBLE);
                if (data.isSelected()) {
                    selectCb.setChecked(true);
                } else {
                    selectCb.setChecked(false);
                }
            } else {
                selectCb.setVisibility(View.GONE);
            }

            selectCb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox)v;
                    checkItem(data, cb.isChecked());
                }
            });
        }

        /**
         * 选中或不选一项
         * @param data 数据项
         * @param checking true选中，false不选
         */
        private void checkItem(InfoItem data, boolean checking) {
            data.setIsSelected(checking);
            if (selectingFlag.listener != null) {
                selectingFlag.listener.onSelectedChanged(checking);
            }
        }
    }

    /**
     * 正在选择状态标识
     */
    private static class SelectingFlag{
        private boolean isSelecting;

        private SelectingListener listener;

        public SelectingFlag(boolean isSelecting) {
            this.isSelecting = isSelecting;
        }

        public boolean isSelecting() {
            return isSelecting;
        }

        public void setSelecting(boolean selecting) {
            isSelecting = selecting;
        }
    }

    /**
     * 资讯项选择状态变化监听
     */
    private interface SelectingListener {
        /**
         *
         * @param selecting true选择了某个 false取消选择了某个
         */
        void onSelectedChanged(boolean selecting);
    }

    public interface AllSelectedListener {
        void onSelectAll();
        void onCancelSelectAll();
    }
}
