package com.codyy.erpsportal.commons.controllers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.codyy.erpsportal.R;

/**
 * 视频会议列表筛选Fragment
 * Created by ldh on 2015/8/25.
 */
public class FilterVideoMeetingFragment extends Fragment {

    private View mFragmentView;
    private ListView mListView;
    private filterFragmentAdapter mAdapter;
    private String[] mData = {"全部","未开始","进行中","已结束"};
    private String mCurrentSelectItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.fragment_filter_videomeeting,null,false);
        mListView = (ListView)mFragmentView.findViewById(R.id.filter_listView);
        mAdapter = new filterFragmentAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setSelector(R.drawable.video_listitem_selector);
        return mFragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentSelectItem = parent.getItemAtPosition(position).toString();
            }
        });
    }

    public String GetSelectedItem(){
        if(mCurrentSelectItem != null){
            if(mCurrentSelectItem.equals("全部"))
                return "";
            if(mCurrentSelectItem.equals("未开始"))
                return "INIT";
            if(mCurrentSelectItem.equals("进行中"))
                return "PROGRESS";
            if(mCurrentSelectItem.equals("已结束"))
                return "END";
        }


        return "";
    }

    private class filterFragmentAdapter extends BaseAdapter{

        private LayoutInflater mInflater;

        class viewHolder {
            TextView textView;
        }

        public filterFragmentAdapter() {
            super();
            mInflater = getActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mData ==null ? 0:mData.length;
        }

        @Override
        public Object getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            viewHolder holder = null;
            if(convertView == null){
                holder = new viewHolder();
                convertView = (View)mInflater.inflate(R.layout.item_videomeeting_filter, null, false);
                holder.textView = (TextView)convertView.findViewById(R.id.filter_content_videomeeting);
                convertView.setTag(holder);
            }else{
                holder = (viewHolder)convertView.getTag();
            }

            holder.textView.setText(mData[position]);
            return convertView;
        }
    }

}
