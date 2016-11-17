package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.models.entities.PVideo;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.Cog;

import java.util.ArrayList;

/**
 * 分段选择（往期录播）
 * Created by caixingming on 2015/5/8.
 */
public class LivePeriodFragment extends Fragment implements AdapterView.OnItemClickListener{

    private String TAG = "LivePeriodFragment";
    private ListView mListView;
    private FrameLayout mFrameLayout;
    private LivePeriodFragment lpf;
    private UserInfo mUserInfo;
    private ObjectsAdapter<PVideo,LPViewHolder> mAdapter;
    private ArrayList<PVideo> models;

    public LivePeriodFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lpf =   this;

        //get args
        if(null != getArguments()){
            mUserInfo   =   getArguments().getParcelable("user");
            models  =   getArguments().getParcelableArrayList("data");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_live_period, container, false);

        mFrameLayout    = (FrameLayout) view.findViewById(R.id.framePeriodOfPeriodLive);
        mListView   = (ListView) view.findViewById(R.id.listViewOfLivePeriodFragment);
        mListView.setOnItemClickListener(this);

        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }

    private void dismiss() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();//获取FragmentTransaction 实例
        ft.remove(lpf);
        ft.commit();//提交
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //load data ...
        mAdapter = new ObjectsAdapter<>(getActivity(), LPViewHolder.class);
        mListView.setAdapter(mAdapter);

//        loadData();
        if(models!=null && models.size()>0){

            mAdapter.addData(models);
            mAdapter.notifyDataSetChanged();

            if(models.size() == 1){
                Play(0);
            }
        }else{
            dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Play(position);
    }

    private void Play(int position) {
        PVideo pv = mAdapter.getItem(position);
        String playUrl = URLConfig.URL_PLAY_VIDEO_HTTP
                        +"?uuid="+mUserInfo.getUuid()
                        +"&videoId="+pv.getVideoId();
//        HistoryVideoPlayActivity.start(getActivity(), playUrl, pv.getVideoName(), false);
        Cog.i("video:", playUrl);
        dismiss();
    }

    public static class LPViewHolder extends AbsViewHolder<PVideo> {

        private TextView txtView;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_simple_text;
        }

        @Override
        public void mapFromView(View view) {
            txtView = (TextView) view.findViewById(R.id.txtContentOfSimpleText);
        }

        @Override
        public void setDataToView(PVideo data, Context context) {
            txtView.setText(data.getVideoName());
        }
    }
}
