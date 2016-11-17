package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.IMeetingService;
import com.codyy.erpsportal.commons.services.OnlineMeetingService;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.onlinemeetings.models.entities.MeetingBase;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * 视频会议基础Fragment
 * @use 获取公共信息
 * Created by poe on 15-8-17.
 */
public abstract class OnlineFragmentBase extends Fragment{

    private  static final String TAG = OnlineFragmentBase.class.getSimpleName();
    protected UserInfo mUserInfo;
    protected String mMeetID;//
    protected boolean mInit = false;//是否初始化
    protected View mRootView;
    protected MeetingBase mMeetingBase;
    private WeakReference<IMeetingService> mMeetingServiceWeakReference ;
    private WeakReference<OnlineMeetingActivity> mOnlineMeetingWeakReferencer ;
    protected boolean mIsLoginInit = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInit    =   true;
        initData();
    }

    public void initData() {
        if(null != getArguments()){
            mUserInfo = getArguments().getParcelable(Constants.USER_INFO);
        }
        if(null == mUserInfo){
            mUserInfo   = UserInfoKeeper.getInstance().getUserInfo();
        }

        mOnlineMeetingWeakReferencer = new WeakReference(getActivity());
        mMeetID     =  mOnlineMeetingWeakReferencer.get().getMeetingID();
        mMeetingBase     =   mOnlineMeetingWeakReferencer.get().getMeetingBase();
        mIsLoginInit      =    mOnlineMeetingWeakReferencer.get().isLoginInit();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mInit){
            mInit    =   false;
            viewLoadCompleted();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if(mRootView == null){
            mRootView   =   inflater.inflate(obtainLayoutId(), null);
            ButterKnife.bind(this, mRootView);
        }
        return mRootView;
    }

    /**
     * 用户布局id
     * @return
     */
    public abstract int obtainLayoutId();

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 试图加载完成....
     */
    public void viewLoadCompleted(){
        if(mMeetingBase == null){
            initData();
        }
    }

    /**
     * 获取视频服务 .
     * @return
     */
    public IMeetingService getCocoService(){

        if(null == mOnlineMeetingWeakReferencer.get() ) return null;

        //init .
        if(mMeetingServiceWeakReference == null && mOnlineMeetingWeakReferencer.get().getMeetingService() != null){
            mMeetingServiceWeakReference = new WeakReference(mOnlineMeetingWeakReferencer.get().getMeetingService());
        }

        if(mMeetingBase.isWhiteBoardManager()){
            return  mMeetingServiceWeakReference == null?null:mMeetingServiceWeakReference.get();
        }else{
            return  null;
        }
    }

    public OnlineMeetingActivity getParentActivity(){
        return  mOnlineMeetingWeakReferencer == null ? null : mOnlineMeetingWeakReferencer.get();
    }

    //获取视频发送服务对象 .
    public OnlineMeetingService getChatService(){
        return mOnlineMeetingWeakReferencer.get()==null? null :mOnlineMeetingWeakReferencer.get().getChatService();
    }

    /**
     * 互动-设置SlideTabLayout当前的index
     * @return
     */
    public void setTabIndex(int index){
        if(mOnlineMeetingWeakReferencer.get() == null) return;
        mOnlineMeetingWeakReferencer.get().setTabIndex(index);
    }

    /**
     * 互动-SlideTabLayout当前的index
     * @return
     */
    public int getTabIndex(){
        if (mOnlineMeetingWeakReferencer.get() == null) return -1;
        return  mOnlineMeetingWeakReferencer.get().getTabIndex();
    }
    /**
     * 主TabHost-当前的index
     * @return
     */
    public int getHostTabIndex(){
        if (mOnlineMeetingWeakReferencer.get() == null) return -1;
        return   mOnlineMeetingWeakReferencer.get().getHostTabIndex();
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
