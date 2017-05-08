package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.groups.controllers.adapters.OnlineMeetingInteractAdapter;
import com.codyy.erpsportal.commons.models.entities.CoCoAction;
import com.codyy.erpsportal.commons.models.entities.MeetingAction;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.commons.receivers.ScreenBroadcastReceiver;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.PullXmlUtils;
import com.codyy.erpsportal.commons.widgets.CodyyViewPager;
import butterknife.Bind;
import de.greenrobot.event.EventBus;

/**
 * 互动区 （视频模式/演示模式）
 * @author poe
 */
public class OnlineInteractFragment extends OnlineFragmentBase {
    private static final String TAG = "OnlineInteractFragment";
    /**
     * 当前模式-视频模式
     */
    public static final String ACTION_SHOW_VIDEO = "video";
    /**
     * 当前模式－文档模式
     */
    public static final String ACTION_SHOW_DOCUMENT = "show";

    @Bind(R.id.tab_layout)TabLayout mTabLayout;
    @Bind(R.id.vp_online_meeting_interact)CodyyViewPager mViewPager;
    @Bind(R.id.rlt_tab_container)RelativeLayout mTabRelativeContainer;
    /**
     * 最新需要更新的会议模式
     */
    private String mNewShowModel ;
    private boolean isScreenLocked = false;
    private ScreenBroadcastReceiver mScreenReceiver;
    private boolean mIsPaused = false;//是否暂停，用于演示文档判断

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cog.e(TAG, "onCreate~");
        EventBus.getDefault().register(this);
        registerScreenReceiver();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setRetainInstance(true);
    }

    //注册屏幕锁屏监听
    private void registerScreenReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mScreenReceiver = new ScreenBroadcastReceiver(mScreenStateListener);
        getActivity().registerReceiver(mScreenReceiver, filter);
    }

    /**
     * unregister .
     */
    private void unRegisterScreenReceiver(){
        if(mScreenReceiver != null){
            getActivity().unregisterReceiver(mScreenReceiver);
        }
    }

    public int getIndex(){
        if(null != mTabLayout){
            return  mTabLayout.getSelectedTabPosition();
        }
        return 0;
    }


     /**
     * 手动切换会议模式(要求:发言人权限)
     */
    private void setModel(int index) {
        Cog.i(TAG,"setModel :"+ index);
        if(index == 0 ){
            mNewShowModel   =   MeetingBase.BASE_MEET_MODEL_VIDEO;
            if(null != getChatService()&& OnlineMeetingActivity.mShowMyViewState) {
                getChatService().showView();
            }
        }else if(index == 1){
            mNewShowModel   =   MeetingBase.BASE_MEET_MODEL_SHOW;
            if(null != getChatService()){
                getChatService().hideView();
            }
        }
        //持有最新的tabindex
        setTabIndex(Integer.parseInt(mNewShowModel));
        //根据
        if(mViewPager.getCurrentItem() != Integer.parseInt(mNewShowModel)){
            asyncModel();
        }
    }

    /**
     * 同步最新的Item选中情况 .
     */
    private void asyncModel() {
        if(null == mViewPager) return;
        Cog.i(TAG,"asyncModel :"+ mNewShowModel);
        //持有最新的tabindex
        mViewPager.setCurrentItem(Integer.parseInt(mNewShowModel));
        setTabIndex(Integer.parseInt(mNewShowModel));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getParentActivity().setTitle(mMeetingBase.getBaseTitle());
    }

    @Override
    public int obtainLayoutId() {
        return R.layout.fragment_online_main;
    }

    @Override
    public void viewLoadCompleted() {
        super.viewLoadCompleted();
        Cog.e(TAG,"viewLoadCompleted~");
        //add tabs
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.main_green));
        mTabLayout.setSelectedTabIndicatorHeight((int)(getResources().getDimension(R.dimen.tab_layout_select_indicator_height)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.video_model)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.show_model)));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setPagingEnabled(false);

        OnlineMeetingInteractAdapter adapter = new OnlineMeetingInteractAdapter(getActivity(), getChildFragmentManager(), mViewPager);
        Bundle bd = new Bundle();
        bd.putParcelable(Constants.USER_INFO , mUserInfo);
        adapter.addTab("0", getString(R.string.video_model), OnlineInteractVideoFragment.class, bd);
        adapter.addTab("1", getString(R.string.show_model), OnlineInteractShowFragment.class, bd);
        mViewPager.setAdapter(adapter);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Cog.i(TAG,"onTabSelected : " +mTabLayout.getSelectedTabPosition());
                Cog.i(TAG,"onTabSelected : tab.getPosition :" +tab.getPosition());
                setModel(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //同步模式 .
        mNewShowModel   =   mMeetingBase.getBaseModel();
        asyncModel();
        Cog.i(TAG , "TabHost height : " + mTabLayout.getMeasuredHeight() + " ; " + mTabLayout.getHeight());
    }

    /**
     * 全屏完成之后回调函数 .
     * @param action
     * @throws RemoteException
     */
    public void onEventMainThread(MeetingAction action) throws RemoteException {
        Cog.d(TAG,"event bus received success ~");
        switch (action.getType()){
            case MeetingAction.ACTION_TYPE_EXPAND:
                mTabRelativeContainer.setVisibility(View.GONE);
                break;
            case MeetingAction.ACTION_TYPE_COLLAPSE:
                mTabRelativeContainer.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * 处理COCO回来的信息 ,主动推送的即时命令 如:禁言/免打扰/踢出房间/会议结束.
     * @throws RemoteException
     */
    public void onEventMainThread(CoCoAction action) throws RemoteException {

        switch (action.getActionType()) {
            case PullXmlUtils.SWITCH_MODE :
                //跟随wen切换模式 .
                Cog.e(TAG,action.getByOperationObject()+","+action.getActionResult());
                 if(action.getActionResult().equals(ACTION_SHOW_VIDEO)){//演示模式
                     Cog.i(TAG ," showMode~ mViewPager is Visibility :"+ (mViewPager.getVisibility() == View.VISIBLE));
                    mNewShowModel   =   MeetingBase.BASE_MEET_MODEL_SHOW;

                     if(mViewPager.getCurrentItem() != Integer.parseInt(mNewShowModel)) {
                       if(!mIsPaused)  {
                           mViewPager.setCurrentItem(1);//此处有问题/会导致调用失败 ToolBarLayout 的onSelected 为 0 .过滤未回复过来的情况
                       }
                         if (null != getChatService()) {
                             getChatService().hideView();
                         }
                     }
                }else if(action.getActionResult().equals(ACTION_SHOW_DOCUMENT)){//视频模式
                     Cog.i(TAG ," videoMode~");
                    mNewShowModel   =   MeetingBase.BASE_MEET_MODEL_VIDEO;
                     if(mViewPager.getCurrentItem() != Integer.parseInt(mNewShowModel)) {
                         if(!mIsPaused) mViewPager.setCurrentItem(0);
                         //过滤处于其他fragment被控制切模式的情况。
                         int index = ((OnlineMeetingActivity) getActivity()).getHostTabIndex();
                         if (index == 0 && null != getChatService() && OnlineMeetingActivity.mShowMyViewState) {
                             getChatService().showView();
                         }
                     }
                }
                break;
            case PullXmlUtils.CHAT_IS_CLOSE_BACK :
                 /*
                 * setActionResult = [true] 设置某人禁言
                 * setActionResult = [false] 取消某人禁言
                 */
                break;
            case PullXmlUtils.REFUSE_SPEAKER_BACK :
                /**
                 * setActionResult = ["3d43ac9f912e48299d10293213a75400"]  主持人拒绝某人申请发言
                 */
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsPaused   =   true ;
        Cog.e(TAG, "onPause~");
        if(null != getChatService() && !isScreenLocked){
            getChatService().hideView();
        }
    }

    @Override
    public void onDestroy() {
        mIsPaused   =   false;
        unRegisterScreenReceiver();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Cog.e(TAG, "onSaveInstanceState~");
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsPaused   =   false;
        Cog.e(TAG,"onResume~");
        if(null!=mNewShowModel){
            int current = mViewPager.getCurrentItem();
            int newpos = Integer.parseInt(mNewShowModel);
            //位置不是目标位置则 同步（只有发言人才会能改变本地的BaseModel .）
            if(current != newpos){
                asyncModel();
            }
            if(!isScreenLocked){//非锁屏恢复
                if(newpos == 0)
                tryResumeMyVideo(newpos);
            }else{
                //锁屏被解锁恢复
                isScreenLocked  =   true;
            }
        }
    }

    //尝试resume我的视频
    private void tryResumeMyVideo(int newpos) {
        Cog.i(TAG," tryResumeMyVideo position : " + newpos);
        if(newpos == 0 && mMeetingBase.getBaseRole()<=MeetingBase.BASE_MEET_ROLE_2 && getHostTabIndex()==0){
            if(null != getChatService() &&OnlineMeetingActivity.mShowMyViewState){
                getChatService().showView();
            }
        }
    }

    private ScreenBroadcastReceiver.ScreenStateListener mScreenStateListener = new ScreenBroadcastReceiver.ScreenStateListener() {
        @Override
        public void onScreenOn() {
            Log.i(TAG,"onScreenOn() ");
        }
        @Override
        public void onScreenOff() {
            Log.i(TAG,"onScreenOff() ");
            isScreenLocked  =   true ;
        }

        @Override
        public void onUserPresent() {
            isScreenLocked = false;
        }
    };
}
