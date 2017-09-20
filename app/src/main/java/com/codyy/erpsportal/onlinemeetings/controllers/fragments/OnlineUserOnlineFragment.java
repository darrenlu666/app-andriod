
package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.SingleChatActivity;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.commons.models.entities.LoginOut;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.onlinemeetings.controllers.viewholders.OnlineUserViewHolder;
import com.codyy.erpsportal.onlinemeetings.models.entities.OnlineUserInfo;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.commons.utils.UiOnlineMeetingUtils;
import com.codyy.erpsportal.commons.widgets.EmptyView;
import com.codyy.tpmp.filterlibrary.adapters.BaseRecyclerAdapter;
import com.codyy.tpmp.filterlibrary.widgets.recyclerviews.SimpleHorizonDivider;

import java.util.List;
import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 已进入用户列表
 * @author poe
 */
public class OnlineUserOnlineFragment extends OnlineFragmentBase {
    private static final String TAG = "OnlineUserOnlineFragment";
    private BaseRecyclerAdapter<OnlineUserInfo, OnlineUserViewHolder> mAdapter;
    private List<OnlineUserInfo> mData;
    @Bind(R.id.recycler_view)RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.empty_view)EmptyView mEmptyView;
    public  boolean mIsChooseSpeaker = false;// default：false true：click to choose an speaker .

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_online_users_online;
    }

    @Override
    public void viewLoadCompleted() {
        super.viewLoadCompleted();
        Cog.e(TAG,"viewLoadCompleted()~");
        initView();
        //加载用户数据
        loadData();
    }

    private void showProgress(){
        mEmptyView.setVisibility(View.VISIBLE);
        mEmptyView.setLoading(true);
    }

    private void cancelProgress(){
        mEmptyView.setVisibility(View.INVISIBLE);
        mEmptyView.setLoading(false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getParentActivity().setTitle(getString(R.string.title_tab_online_meeting_user_lists));
    }

    private void initView() {
        //set data
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getParentActivity()));
        Drawable divider = UiOnlineMeetingUtils.loadDrawable(R.drawable.divider_online_meeting);
        mRecyclerView.addItemDecoration(new SimpleHorizonDivider(divider));
        mAdapter = new BaseRecyclerAdapter<>(new BaseRecyclerAdapter.ViewCreator<OnlineUserViewHolder>() {
            @Override
            public OnlineUserViewHolder createViewHolder(ViewGroup parent, int viewType) {
                return  new OnlineUserViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(
                        parent.getContext(),R.layout.item_online_user), mMeetingBase.getBaseRole(),mUserInfo.getBaseUserId());
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<OnlineUserInfo>() {
            @Override
            public void onItemClicked(View v, int position, OnlineUserInfo data) {
                if(v.getId() == R.id.img_user_item_end){
                    goMessage(data);
                }else if(v.getId() == R.id.img_video_state){
                    goVideo(data);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        //pull to refresh
        mSwipeRefreshLayout.setColorSchemeResources(R.color.md_green_800, R.color.md_yellow_800, R.color.md_red_800, R.color.md_blue_800);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                getParentActivity().getUsers(new OnlineMeetingActivity.ILoader() {
                    @Override
                    public void onLoadUserSuccess(List<OnlineUserInfo> users) {
                        mData = users;
                        mAdapter.setData(mData);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, true , true);
            }
        });
    }

    private void goVideo(OnlineUserInfo data) {
        EventBus.getDefault().post(OnlineInteractVideoFragment.ACTION_CHOOSE_SPEAKER_END);
        CoCoAction action = new CoCoAction();
        action.setActionType(PullXmlUtils.SWITCH_MODE);
        action.setActionResult(OnlineInteractFragment.ACTION_SHOW_DOCUMENT);
        EventBus.getDefault().post(action);
        EventBus.getDefault().post(data);
    }

    private void goMessage(OnlineUserInfo data) {
        Cog.i(TAG,"login user role :"+data.getRole());
        if(mMeetingBase.getBaseRole()> -1 && mMeetingBase.getBaseRole() == MeetingBase.BASE_MEET_ROLE_3) return;
        if(null != mUserInfo && data.isOnline() && !mUserInfo.getBaseUserId().equals(data.getId())){
            //开始聊天
            Intent intent = new Intent(getActivity(), SingleChatActivity.class);
            intent.putExtra(SingleChatActivity.CHAT_TYPE, SingleChatActivity.CHAT_TYPE_SINGLE);
            intent.putExtra(SingleChatActivity.KEY_TO_CHAT_ID, data.getId());
            intent.putExtra(SingleChatActivity.USER_NAME, data.getName());
            intent.putExtra(SingleChatActivity.USER_HEAD_URL, data.getIcon());
            intent.putExtra(SingleChatActivity.HAS_UNREAD, false);
            OnlineMeetingActivity act = (OnlineMeetingActivity) getActivity();
            intent.putExtra(SingleChatActivity.MEETING_ID, act.getMeetingID());
            startActivity(intent);
        }
    }

    /**
     * 刷新数据防止 有用户被踢 , 新的来宾加入等数据变化.
     */
    private void loadData() {
        Cog.i(TAG,"loadData user start....");
        showProgress();
         getParentActivity().getUsers(new OnlineMeetingActivity.ILoader() {
            @Override
            public void onLoadUserSuccess(List<OnlineUserInfo> users) {
                Cog.i(TAG,"loadData user success....");
                if(null ==mSwipeRefreshLayout) return;
                mData = users;
                mAdapter.setData(mData);
                mSwipeRefreshLayout.setRefreshing(false);
                cancelProgress();
            }
        }, true,true);
    }

    /**
     * 处理COCO回来的信息
     * @param msg 选择发言人的TAG .
     * @throws RemoteException
     */
    public void onEventMainThread(String msg) throws RemoteException {
        switch (msg) {
            case OnlineInteractVideoFragment.ACTION_CHOOSE_SPEAKER_START://选择发言热-跳转到用户列表
                mIsChooseSpeaker = true;
                break;
            case OnlineInteractVideoFragment.ACTION_CHOOSE_SPEAKER_END://结束
                mIsChooseSpeaker = false;
                break;
        }
    }

    /**
     * 用户ID下线
     * @throws RemoteException
     */
    public void onEventMainThread(LoginOut loginOut) throws RemoteException {
        Cog.d(TAG, "loginOUt : " + loginOut.getFrom());
        if(!loginOut.getFrom().equals(mUserInfo.getBaseUserId())){
            //更新用户的在线数据状态.
            OnlineMeetingActivity act = getParentActivity();
            if(act != null){
                loadData();
            }
        }
    }
    /**
     * 处理COCO回来的信息 ,主动推送的即时命令 如:禁言/免打扰/踢出房间/会议结束.
     * @throws RemoteException
     */
    public void onEventMainThread(CoCoAction action) throws RemoteException {
        switch (action.getActionType()) {
            case PullXmlUtils.TYPE_LOGIN://某人上线了
            case PullXmlUtils.TYPE_LOGIN_OUT://某人下线了
                Cog.e(TAG,"onEventMainThread(CoCoAction action) Refresh 在线用户列表～ "+action.getActionType());
                //更新用户的在线数据状态.
                if(getParentActivity() != null){
                    loadData();
                }
                break;
            case PullXmlUtils.AGREE_SPEAKER_BACK: //把你设为发言人
            case PullXmlUtils.AGREE_SPEAKER_BACK＿ALL://设置其他人为发言人,同意某人的发言申请.
                String id = action.getByOperationObject();
                for(int i = 0; i< mData.size();i++){
                    if(mData.get(i).getId().equals(id)){
                        mData.get(i).setRole(MeetingBase.BASE_MEET_ROLE_1);
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case PullXmlUtils.CANCEL_SPEAKER://取消发言人
            case PullXmlUtils.WEB_CANCEL_SPEAKER://取消发言人
            case PullXmlUtils.WEB_CANCEL_SPEAKER_ALL://取消发言人
                Cog.e(TAG,"onEventMainThread(CoCoAction action) Refresh 在线用户列表～ "+action.getActionType());
                String id2 = action.getByOperationObject();
                for(int i = 0; i< mData.size();i++){
                    if(mData.get(i).getId().equals(id2)){
                        mData.get(i).setRole(MeetingBase.BASE_MEET_ROLE_2);
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        //放弃选择发言人-恢复 click->聊天界面
        mIsChooseSpeaker = false;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
