package com.codyy.erpsportal.info.controllers.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.adapters.StateObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.adapters.StateObjectsAdapter.OnSelectedChangedListener;
import com.codyy.erpsportal.commons.models.ImageFetcher;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.info.controllers.activities.InfoDetailActivity;
import com.codyy.erpsportal.info.models.entities.InfoItem;
import com.codyy.url.URLConfig;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 带全选的资讯列表碎片
 * Created by kmdai on 2015/7/20.
 */
public class InfoEditListFragment extends Fragment implements AdapterView.OnItemClickListener,PullToRefreshBase.OnRefreshListener2 {

    private final static String TAG = "InfoEditListFragment";

    public final static String ARG_USER_INFO = "user_info";

    public final static String ARG_TYPE = "type";

    private final static int LOAD_COUNT = 10;

    private boolean mIsSelectMode = false;

    private int mStart;

    private View mRootView;

    @Bind(R.id.list_view)
    PullToRefreshListView mListView;

    @Bind(R.id.empty_view)
    EmptyView mEmptyView;

    @Bind(R.id.bar_delete)
    RelativeLayout mDeleteContainer;

    @Bind(R.id.cb_select_all)
    CheckBox mSelectAll;

    @Bind(R.id.btn_delete)
    Button mDeleteBtn;

    private OnDeleteCompleteListener mDeleteCompleteListener;

    private StateObjectsAdapter<InfoItem, InfoEditViewHolder> mAdapter;

    private UserInfo mUserInfo;

    private String mType;

    private RequestSender mSender;

    private Object mReqTag = new Object();

    public static InfoEditListFragment newInstance(UserInfo userInfo, String type){
        InfoEditListFragment fragment = new InfoEditListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_USER_INFO, userInfo);
        bundle.putString(ARG_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        mSender = new RequestSender(activity);
        if (activity instanceof OnDeleteCompleteListener) {
            mDeleteCompleteListener = (OnDeleteCompleteListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSender = null;
        mDeleteCompleteListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = getArguments().getParcelable(ARG_USER_INFO);
        mType = getArguments().getString(ARG_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_info_edit_list, container, false);
            ButterKnife.bind(this, mRootView);
            mAdapter = new StateObjectsAdapter<>(getActivity(),
                    InfoEditViewHolder.class,
                    new OnSelectedChangedListener(){

                        @Override
                        public void onSelectedChanged() {
                            updateSelectAllCbState();
                        }
                    });
            mListView.setAdapter(mAdapter);
            mListView.setEmptyView(mEmptyView);
            mListView.setOnItemClickListener(this);
            mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
                @Override
                public void onReloadClick() {
                    loadData(true);
                }
            });

            initPullToRefresh(mListView);
        }
        return mRootView;
    }

    /**
     * 更新全部选择的CheckBox
     */
    private void updateSelectAllCbState() {
        List<InfoItem> infoItems = mAdapter.getItems();
        if (infoItems != null) {
            for(InfoItem infoItem: infoItems) {
                if (!infoItem.isSelected()) {
                    mSelectAll.setChecked(false);
                    return;
                }
            }
            mSelectAll.setChecked(true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(true);
    }

    private void loadData(final boolean refresh) {
        Map<String, String> params = new HashMap<>();
        //?uuid=MOBILE:16bcaa6ec80e461aaae8e6fb29bea254&start=0&end=1000&infoType=ANNOUNCEMENT
        if (refresh) {
            mStart = 0;
        }
        int start = mStart;
        int end = start + LOAD_COUNT;
        params.put("start", "" + start);
        params.put("end", "" + (end - 1));

        if (mUserInfo == null) return;
        params.put("uuid", mUserInfo.getUuid());
        params.put("infoType", mType);

        Cog.d(TAG, "loadData:" + URLConfig.APP_INFO + params);
        mSender.sendRequest(new RequestSender.RequestData(URLConfig.APP_INFO, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG, "onResponse:" + response);
                if ("success".equals(response.optString("result"))) {

                    int total = response.optInt("total");
                    if (total == 0) {
                        mAdapter.setData(null);
                        mAdapter.notifyDataSetChanged();

                        mListView.onRefreshComplete();
                        mEmptyView.setLoading(false);
                        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    } else {
                        JSONArray jsonArray = response.optJSONArray("list");
                        List<InfoItem> infoList = InfoItem.parseJsonArray(jsonArray);
                        if (refresh) {
                            mAdapter.setData(infoList);
                        } else {
                            mAdapter.addData(infoList);
                        }
                        mAdapter.notifyDataSetChanged();
                        updateSelectAllCbState();

                        mListView.onRefreshComplete();
                        //如果已经加载所有，下拉更多关闭
                        if (total <= mAdapter.getCount()) {
                            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            mListView.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                    }
                    mStart = mAdapter.getCount();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cog.e(TAG, "onErrorResponse:" + error);
                UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                if (refresh && mAdapter.getCount() == 0) {
                    mListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
                mListView.onRefreshComplete();
                mEmptyView.setLoading(false);
            }
        }, mReqTag));
        mEmptyView.setLoading(true);
    }

    private void initPullToRefresh(PullToRefreshAdapterViewBase<?> view) {
        view.setMode(PullToRefreshBase.Mode.BOTH);
        view.getLoadingLayoutProxy(false, true).setPullLabel(getResources().getString(R.string.pull_to_refresh));
        view.getLoadingLayoutProxy(false, true).setRefreshingLabel(getResources().getString(R.string.loading));
        view.getLoadingLayoutProxy(false, true).setReleaseLabel(getResources().getString(R.string.release_to_refresh));
        view.setOnRefreshListener(this);
    }

    @OnClick(R.id.cb_select_all)
    public void onSelectAllClick() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mAdapter.getItem(i).setIsSelected(mSelectAll.isChecked());
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_delete)
    public void onDeleteClick(){
        int selectCount = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = mAdapter.getCount() - 1; i >= 0; i--) {
            InfoItem news = mAdapter.getItem(i);
            if (news.isSelected()) {
                selectCount ++;
                sb.append(mAdapter.getItem(i).getInformationId()).append(',');
            }
        }

        if (selectCount == 0){
//            onDeleteComplete();
            return;
        }
        sb.deleteCharAt(sb.length() - 1);
        Map<String, String> params = new HashMap<>();
        if (mUserInfo == null) return;
        params.put("uuid", mUserInfo.getUuid());
        params.put("ids", sb.toString());
        mSender.sendRequest(new RequestSender.RequestData(URLConfig.INFO_DELETE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if ("success".equals(response.optString("result"))) {
                            mAdapter.notifyDataSetChanged();
                            onDeleteComplete();
                            deleteSelected();
                            Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
                            loadData(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mAdapter.notifyDataSetChanged();
                onDeleteComplete();
                Toast.makeText(getActivity(), R.string.net_error, Toast.LENGTH_SHORT).show();
            }
        }, mReqTag));
    }

    public void onDeleteComplete() {
        exitSelectMode();
        notifyDeleteComplete();
    }

    public void notifyDeleteComplete() {
        if (mDeleteCompleteListener != null) {
            mDeleteCompleteListener.onDeleteComplete();
        }
    }

    public void deleteSelected() {
        for (int i = mAdapter.getCount() - 1; i >= 0; i--) {
            InfoItem item = mAdapter.getItem(i);
            if (item.isSelected()) {
                mAdapter.removeItem(i);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InfoItem info = mAdapter.getItem(position - 1 );
        InfoDetailActivity.startFromFunction(view.getContext(), info.getInformationId());
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        loadData(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        loadData( false);
    }

    public void toggleSelectMode() {
        if (isSelectMode()) {
            exitSelectMode();
        } else {
            enterSelectMode();
        }
    }

    public void enterSelectMode() {
        setIsSelectMode(true);
        mDeleteContainer.setVisibility( View.VISIBLE);
        mAdapter.setIsSelectMode(true);
        mAdapter.notifyDataSetChanged();
    }

    public void exitSelectMode() {
        setIsSelectMode(false);
        mDeleteContainer.setVisibility(View.GONE);
        mAdapter.setIsSelectMode(false);
        mAdapter.notifyDataSetChanged();
    }

    public boolean isSelectMode() {
        return mIsSelectMode;
    }

    public void setIsSelectMode(boolean isSelectMode) {
        mIsSelectMode = isSelectMode;
    }

    public OnDeleteCompleteListener getDeleteCompleteListener() {
        return mDeleteCompleteListener;
    }

    public void setDeleteCompleteListener(OnDeleteCompleteListener deleteCompleteListener) {
        mDeleteCompleteListener = deleteCompleteListener;
    }

    public interface OnDeleteCompleteListener {
        void onDeleteComplete();
    }

    @Override
    public void onDestroy() {
        mSender.stop(mReqTag);
        super.onDestroy();
    }

    public static class InfoEditViewHolder extends StateObjectsAdapter.StateViewHolder<InfoItem> {

        @Bind(R.id.cb_select)
        CheckBox selectCb;

        @Bind(R.id.dv_icon)
        SimpleDraweeView iconDv;

        @Bind(R.id.tv_title)
        TextView titleTv;

        @Bind(R.id.tv_intro)
        TextView introTv;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_news_notify;
        }

        @Override
        public void mapFromView(View view) {
            ButterKnife.bind(this, view);
        }

        @Override
        public void setDataToView(final InfoItem data, Context context, boolean flag,
                                  final OnSelectedChangedListener listener) {
            titleTv.setText(data.getTitle());
            String content = data.getContent();
            if (TextUtils.isEmpty(content)) {
                content = "";
            }
            introTv.setText(content);
            if (!TextUtils.isEmpty(data.getThumb())) {
                iconDv.setVisibility(View.VISIBLE);
                ImageFetcher.getInstance(context).fetchSmall(iconDv, data.getThumb());
            } else {
                iconDv.setVisibility(View.GONE);
            }

            if (flag) {
                selectCb.setVisibility(View.VISIBLE);
                if (data.isSelected()) {
                    selectCb.setChecked(true);
                } else {
                    selectCb.setChecked(false);
                }
            } else {
                selectCb.setVisibility(View.GONE);
            }

            selectCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    data.setIsSelected(isChecked);
                    if (listener != null) {
                        listener.onSelectedChanged();
                    }
                }
            });
        }
    }
}
