package com.codyy.erpsportal.exam.controllers.fragments.school;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.ScreenUtils;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.ItemIndexListRecyBaseAdapter;
import com.codyy.erpsportal.commons.controllers.fragments.TaskFragment;
import com.codyy.erpsportal.commons.controllers.viewholders.RecyclerViewHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.ExamClass;
import com.codyy.erpsportal.exam.models.entities.ExamItemInfo;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.CharUtils;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.widgets.AnalysisProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 题目统计
 * Created by eachann on 2016/1/14.
 */
public class TopicStatisticFragment extends ItemIndexBaseFragment {
    private static final String TAG = TopicStatisticFragment.class.getSimpleName();
    private List<ExamItemInfo> mData;
    private ItemIndexListRecyAdapter mItemIndexListRecyAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
        mItemIndexListRecyAdapter = new ItemIndexListRecyAdapter(getContext(), mData);

    }

    /**
     * 班级列表listview的适配器
     */
    class ClassAdapter extends BaseAdapter {
        private Context mContext;
        private List<ExamClass> mList;

        public ClassAdapter(Context context, List<ExamClass> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mContext);
            textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, UIUtils.dip2px(mContext, 50)));
            textView.setGravity(Gravity.CENTER);
            textView.setText(mList.get(position).getClassRoomName());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            if (textView.getText().toString().equals(mUpOrDownButton.getText().toString())) {
                textView.setTextColor(getResources().getColor(R.color.main_color));
            } else {
                textView.setTextColor(Color.parseColor("#707070"));
            }
            return textView;
        }
    }

    /**
     * 设置adapter
     *
     * @return
     */
    @Override
    protected RecyclerView.Adapter getAdapter() {
        return mItemIndexListRecyAdapter;
    }

    private List<ExamClass> mList;
    private String mClassId;
    private PopupWindow mPopWindow = null;
    private ClassAdapter mClassListAdapter;
    private ListView mListView;

    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        map.put("examTaskId", getArguments().getString(OverallStatisticsFragment.ARG_EXAM_TASK_ID));
        map.put("classId", mClassId);
        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(URLConfig.CLASS_QUESTIOIN_STATISTIC, map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.i(TAG, response.toString());
                if ("success".equals(response.optString("result")) && getActivity() != null) {
                    parseResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }));
    }

    private void parseResponse(JSONObject response) {
        if (mData.size() > 0) {
            mData.clear();
        }
        try {
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                ExamItemInfo info = new ExamItemInfo();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                JSONArray jsonArray1;
                switch (jsonObject1.getString("questionType")) {
                    case TaskFragment.TYPE_SINGLE_CHOICE:
                        jsonArray1 = jsonObject1.getJSONArray("statisticsList");
                        if (jsonArray1.length() == 0)
                            break;
                        info.setType(getString(R.string.type_single_choice));
                        mData.add(info);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            info = new ExamItemInfo();
                            info.setProgress((int) Float.parseFloat(object.get("rightRate").toString()));
                            info.setType(TaskFragment.TYPE_SINGLE_CHOICE);
                            info.setIndex(object.getInt("questionNum"));
                            mData.add(info);
                        }
                        break;
                    case TaskFragment.TYPE_MULTI_CHOICE:
                        jsonArray1 = jsonObject1.getJSONArray("statisticsList");
                        if (jsonArray1.length() == 0) {
                            break;
                        }
                        info.setType(getString(R.string.type_multi_choice));
                        mData.add(info);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            info = new ExamItemInfo();
                            info.setProgress((int) Float.parseFloat(object.get("rightRate").toString()));
                            info.setType(TaskFragment.TYPE_SINGLE_CHOICE);
                            info.setIndex(object.getInt("questionNum"));
                            mData.add(info);
                        }
                        break;
                    case TaskFragment.TYPE_JUDGEMENT:
                        jsonArray1 = jsonObject1.getJSONArray("statisticsList");
                        if (jsonArray1.length() == 0) {
                            break;
                        }
                        info.setType(getString(R.string.type_judgement));
                        mData.add(info);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            info = new ExamItemInfo();
                            info.setProgress((int) Float.parseFloat(object.get("rightRate").toString()));
                            info.setType(TaskFragment.TYPE_SINGLE_CHOICE);
                            info.setIndex(object.getInt("questionNum"));
                            mData.add(info);
                        }
                        break;
                    case TaskFragment.TYPE_FILL_IN_BLANK:
                        jsonArray1 = jsonObject1.getJSONArray("statisticsList");
                        if (jsonArray1.length() == 0) {
                            break;
                        }
                        info.setType(getString(R.string.type_fill_in_blank));
                        mData.add(info);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            info = new ExamItemInfo();
                            info.setProgress((int) Float.parseFloat(object.get("rightRate").toString()));
                            info.setType(TaskFragment.TYPE_SINGLE_CHOICE);
                            info.setIndex(object.getInt("questionNum"));
                            mData.add(info);
                        }
                        break;
                    case TaskFragment.TYPE_ASK_ANSWER:
                        jsonArray1 = jsonObject1.getJSONArray("statisticsList");
                        if (jsonArray1.length() == 0) {
                            break;
                        }
                        info.setType(getString(R.string.type_ask_answer));
                        mData.add(info);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            info = new ExamItemInfo();
                            info.setProgress((int) Float.parseFloat(object.get("rightRate").toString()));
                            info.setType(TaskFragment.TYPE_SINGLE_CHOICE);
                            info.setIndex(object.getInt("questionNum"));
                            mData.add(info);
                        }
                        break;
                    case TaskFragment.TYPE_COMPUTING:
                        jsonArray1 = jsonObject1.getJSONArray("statisticsList");
                        if (jsonArray1.length() == 0) {
                            break;
                        }
                        info.setType(getString(R.string.type_computing));
                        mData.add(info);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            info = new ExamItemInfo();
                            info.setProgress((int) Float.parseFloat(object.get("rightRate").toString()));
                            info.setType(TaskFragment.TYPE_SINGLE_CHOICE);
                            info.setIndex(object.getInt("questionNum"));
                            mData.add(info);
                        }
                        break;
                    case TaskFragment.TYPE_TEXT:
                        jsonArray1 = jsonObject1.getJSONArray("statisticsList");
                        if (jsonArray1.length() == 0) {
                            break;
                        }
                        info.setType(getString(R.string.type_text));
                        mData.add(info);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            info = new ExamItemInfo();
                            info.setProgress((int) Float.parseFloat(object.get("rightRate").toString()));
                            info.setType(TaskFragment.TYPE_SINGLE_CHOICE);
                            info.setIndex(object.getInt("questionNum"));
                            mData.add(info);
                        }
                        break;
                    case TaskFragment.TYPE_FILE:
                        jsonArray1 = jsonObject1.getJSONArray("statisticsList");
                        if (jsonArray1.length() == 0) {
                            break;
                        }
                        info.setType(getString(R.string.type_file));
                        mData.add(info);
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object = jsonArray1.getJSONObject(j);
                            info = new ExamItemInfo();
                            info.setProgress((int) Float.parseFloat(object.get("rightRate").toString()));
                            info.setType(TaskFragment.TYPE_SINGLE_CHOICE);
                            info.setIndex(object.getInt("questionNum"));
                            mData.add(info);
                        }
                        break;
                }
            }
            mItemIndexListRecyAdapter.setList(mData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadData() {
        mList = getArguments().getParcelableArrayList("ARG_LIST");
        mUpOrDownButton.setText(mList.get(0).getClassRoomName());
        mUpOrDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击班级，弹出班级列表PopWindow，并可选择并添加点击事件
                View view = getActivity().getLayoutInflater().inflate(R.layout.class_room_select_class, null);
                view.findViewById(R.id.iamge_1).setVisibility(View.GONE);
                mListView = (ListView) view.findViewById(R.id.class_room_listview);
                mClassListAdapter = new ClassAdapter(getActivity(), mList);
                mListView.setAdapter(mClassListAdapter);
                int num = mList.size() > 3 ? 3 : mList.size();
                mPopWindow = new PopupWindow(view, ScreenUtils.getScreenWidth(getActivity()), num * UIUtils.dip2px(getActivity(), 48), true);
                mPopWindow.setTouchable(true);
                mPopWindow.setOutsideTouchable(true);
                mPopWindow.setBackgroundDrawable((new ColorDrawable(Color.parseColor("#00000000"))));
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mClassId = mList.get(position).getClassRoomId();
                        mUpOrDownButton.setText(mList.get(position).getClassRoomName());
                        getData();
                        mPopWindow.dismiss();
                    }
                });
                mPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, UIUtils.dip2px(getActivity(), 48));
            }
        });
        mClassId = mList.get(0).getClassRoomId();
        getData();
    }

    /**
     * 设置每项的所占宽度
     *
     * @param position
     * @param gridLayoutManager
     * @return
     */
    @Override
    protected int getItemSpanSize(int position, GridLayoutManager gridLayoutManager) {
        return CharUtils.strIsEnglish(mData.get(position).getType().replace("_", "")) ? 1 : gridLayoutManager.getSpanCount();
    }

    @Override
    protected int getSpanCount() {
        return 4;
    }

    public class ItemIndexListRecyAdapter extends ItemIndexListRecyBaseAdapter<ExamItemInfo> {
        private List<ExamItemInfo> list;

        public ItemIndexListRecyAdapter(Context context, List<ExamItemInfo> list) {
            super(context, list);
            this.list = list;
        }

        public void setList(List<ExamItemInfo> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        @Override
        public int getItemType(int position) {
            return CharUtils.strIsEnglish(mData.get(position).getType().replace("_", "")) ? TYPE_CONTENT : TYPE_TITLE;
        }

        @Override
        protected int getLayoutId(int viewType) {
            return viewType == TYPE_TITLE ? R.layout.item_work_item_index_title : R.layout.item_exam_topic_statistics;
        }

        @Override
        protected RecyclerView.ViewHolder createViewHolder(View view, int viewType) {
            return viewType == TYPE_TITLE ? new ItemTitleViewHolder(view) : new ItemIndexViewHolder(view);
        }
    }

    public static class ItemTitleViewHolder extends RecyclerViewHolder<ExamItemInfo> {
        private TextView textView;

        public ItemTitleViewHolder(View view) {
            super(view);
        }

        @Override
        public void mapFromView(View view) {
            textView = (TextView) view.findViewById(R.id.tv_title_item_list);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }

        @Override
        public void setDataToView(ExamItemInfo data) {
            textView.setText(data.getType());
        }
    }

    public static class ItemIndexViewHolder extends RecyclerViewHolder<ExamItemInfo> {
        private TextView topicTextTv;
        private AnalysisProgress topicAp;

        public ItemIndexViewHolder(View view) {
            super(view);
        }

        @Override
        public void mapFromView(View view) {
            topicTextTv = (TextView) view.findViewById(R.id.tv_exam_topic_text);
            topicAp = (AnalysisProgress) view.findViewById(R.id.ap_exam_topic_progress);
            topicAp.setType(AnalysisProgress.TYPE_CIRCLE);
        }

        @Override
        public void setDataToView(ExamItemInfo data) {
            topicTextTv.setText(String.valueOf(data.getIndex()));
            topicAp.setProgress(data.getProgress());
        }
    }
}
