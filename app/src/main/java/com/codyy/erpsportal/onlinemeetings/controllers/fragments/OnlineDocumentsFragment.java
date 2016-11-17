package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.BaseRecyclerAdapter;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.onlinemeetings.controllers.viewholders.OnlineDocumentViewHolder;
import com.codyy.erpsportal.onlinemeetings.models.entities.DocumentDetailEntity;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.erpsportal.commons.widgets.RecyclerView.SimpleHorizonDivider;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 视频会议-文档管理
 * @author ldh
 * @change poe 2015-12-18
 */
public class OnlineDocumentsFragment extends OnlineFragmentBase {

    private static final String TAG = OnlineDocumentsFragment.class.getSimpleName();
    @Bind(R.id.recycler_view)RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    private BaseRecyclerAdapter<DocumentDetailEntity, OnlineDocumentViewHolder> mAdapter;
    private BaseRecyclerAdapter.OnItemClickListener mListViewItemClickListener = null;
    private List<DocumentDetailEntity> mDocumentDetailList = new ArrayList<>();
    private RequestSender mRequestSender;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.d(TAG, "onCreate()~!");
        EventBus.getDefault().register(this);
        mRequestSender = new RequestSender(getParentActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Cog.d(TAG, "onAttach()~!");
        mListViewItemClickListener = (BaseRecyclerAdapter.OnItemClickListener) activity;
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_online_documents;
    }

    //界面初始化
    private void initView() {
        getParentActivity().setTitle("文档管理");
        mEmptyView.setOnReloadClickListener(new EmptyView.OnReloadClickListener() {
            @Override
            public void onReloadClick() {
                loadData(false);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<OnlineDocumentViewHolder>() {
            @Override
            public OnlineDocumentViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return new OnlineDocumentViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                        parent.getContext(),R.layout.item_documentmanagement),
                        mMeetingBase);
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<DocumentDetailEntity>() {
            @Override
            public void onItemClicked(View v, int position, DocumentDetailEntity data) {
                if(null != mListViewItemClickListener && mMeetingBase.isWhiteBoardManager()) try {
                    mListViewItemClickListener.onItemClicked(v ,position , data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        //pull to refresh
        mSwipeRefreshLayout.setColorSchemeResources(R.color.md_green_800, R.color.md_yellow_800, R.color.md_red_800, R.color.md_blue_800);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cog.d(TAG, "onActivityCreated()~!");
        loadData(false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        Cog.d(TAG, "onViewCreated()~!");
        initView();

    }

    /**
     * 请求文档数据
     * @param isRefresh  是否为下拉刷新
     */
    private void loadData(final boolean isRefresh) {
        Map<String,String> params = new HashMap<>();
        params.put("uuid",mUserInfo.getUuid());// "MOBILE:6e73dbbef9ce4d0d9c180b4d8e1173fa"
        params.put("mid",mMeetID);// "681f2fee6216443588c171e5b13248b9"

        if(isRefresh){
            mSwipeRefreshLayout.setRefreshing(true);
        }else{
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setLoading(true);
        }

        mRequestSender.sendRequest(new RequestSender.RequestData(URLConfig.GET_VIDEODOCUMENT_DETAIL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.d(TAG,response.optInt("total")+"");
                if("success".equals(response.optString("result")) && response.optInt("total") >0){
                    if(isRefresh){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }else{
                        mEmptyView.setVisibility(View.INVISIBLE);
                        mEmptyView.setLoading(false);
                    }
                    JSONArray jsonArray = response.optJSONArray("list");
                    mDocumentDetailList =  DocumentDetailEntity.parseArray(jsonArray);
                    mAdapter.setData(mDocumentDetailList);
                }else{
                    mEmptyView.setVisibility(View.VISIBLE);
                    mEmptyView.setLoading(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mRecyclerView, getResources().getString(R.string.net_error), Snackbar.LENGTH_SHORT).show();
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setLoading(false);
            }
        }));
    }

    public void onEventMainThread(CoCoAction action) throws RemoteException {
        //发言人的变更/更新文档列表
        switch (action.getActionType()) {
            case PullXmlUtils.COMMAND_WHITE_BOARD_MARK: //授予-白板标注权限
                Cog.e(TAG,"获取白板标注权限："+action.getActionResult());
                String result = action.getActionResult();
                mMeetingBase.setWhiteBoardManager(Boolean.valueOf(result)?"1":"0");
                mAdapter.notifyDataSetChanged();
                break;
            case PullXmlUtils.WEB_ADD_DOCUMENT://新增文档
            case PullXmlUtils.COMMAND_REFRESH_DOC://删除文档后执行
                Cog.e(TAG,"添加文档！");
                loadData(true);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
