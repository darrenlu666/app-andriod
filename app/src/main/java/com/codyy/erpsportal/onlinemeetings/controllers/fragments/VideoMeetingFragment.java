package com.codyy.erpsportal.onlinemeetings.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.TipProgressFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.commons.utils.UiMainUtils;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerFragment;
import com.codyy.erpsportal.onlinemeetings.controllers.activities.VideoMeetingDetailActivity;
import com.codyy.erpsportal.onlinemeetings.controllers.viewholders.VideoMeetingViewHolder;
import com.codyy.erpsportal.onlinemeetings.models.entities.VideoMeetingEntity;
import com.codyy.url.URLConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

/**
 * 视频会议
 * Created by ldh on 2015/7/30.
 * modified by poe on 2017/3/3 .
 */
public class VideoMeetingFragment extends SimpleRecyclerFragment<VideoMeetingEntity> {
    private final static String TAG = "VideoMeetingFragment";
    private final static String ARG_LIST_TYPE = "video.meeting.type";
    public final static int TYPE_FOR_LAUNCH = 0;//我发起的
    public final static int TYPE_FOR_ATTEND = 1;//我参与的
    public final static int TYPE_FOR_AREA  =  2;//本级会议管理
    public final static int TYPE_FOR_SCHOOL = 2;//本校会议管理
    public static final int REQUEST_VIDEO_MEETING_OUT = 0x001;
    private int mCurType;//类型：｛＃TYPE_FOR_LAUNCH｝
    private String mState = "";//筛选状态.

    public static VideoMeetingFragment newInstance(int type , UserInfo userinfo){
        VideoMeetingFragment fragment = new VideoMeetingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LIST_TYPE, type);
        args.putParcelable(Constants.USER_INFO,userinfo);
        fragment.setArguments(args);
        return  fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(null != getArguments()){
            //登录用户信息
            mCurType = getArguments().getInt(ARG_LIST_TYPE);
        }
    }

    @Override
    public String getAPI() {
        return URLConfig.GET_VIDEOMEETING;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        if(null != mUserInfo) hashMap.put("uuid", mUserInfo.getUuid());
        if(null != mState) hashMap.put("meet_sate", mState);
        hashMap.put("meet_type",String.valueOf(mCurType));
        hashMap.put("start",mDataList.size()+"");
        hashMap.put("end",(mDataList.size()+sPageCount-1)+"");
        return hashMap;
    }

    @Override
    public void parseData(JSONObject response) {
        if ("success".equals(response.optString("result"))) {
            mTotal = response.optInt("total");//total为列表条数
            JSONArray jsonArray = response.optJSONArray("list");
            List<VideoMeetingEntity> result = VideoMeetingEntity.parseJsonArray(jsonArray);
            if(null != result && result.size()>0){
                for(VideoMeetingEntity entity : result){
                    entity.setBaseViewHoldType(0);
                    mDataList.add(entity);
                }
            }
        }
    }

    @Override
    public void onViewLoadCompleted() {
        super.onViewLoadCompleted();
        initData();
    }

    @Override
    public BaseRecyclerViewHolder<VideoMeetingEntity> getViewHolder(ViewGroup parent) {
        return new VideoMeetingViewHolder(UiMainUtils.setMatchWidthAndWrapHeight(parent.getContext(),R.layout.item_videomeeting));
    }

    @Override
    public void OnItemClicked(View v, int position, VideoMeetingEntity data) {
        Intent intent = new Intent(getActivity(), VideoMeetingDetailActivity.class);
        intent.putExtra("mid", data.getId());
        intent.putExtra("meetingType", mCurType);
        intent.putExtra(Constants.USER_INFO , mUserInfo);
        this.startActivityForResult(intent, REQUEST_VIDEO_MEETING_OUT);
        UIUtils.addEnterAnim(getActivity());
    }

    @Override
    public int getTotal() {
        return mTotal;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_MEETING_OUT && resultCode == 1) {
            refresh();
            TipProgressFragment fragment = TipProgressFragment.newInstance(TipProgressFragment.OUT_STATUS_TIP);
            fragment.show(getFragmentManager(), "showtips");
        }
    }

    public void doFilter(Bundle bd){
        if(null != bd){
            mState   =   bd.getString("state");
            initData();
        }
    }
}
