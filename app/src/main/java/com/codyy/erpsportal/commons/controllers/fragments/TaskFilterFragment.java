package com.codyy.erpsportal.commons.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.TabsWithFilterActivity.FilterParamsProvider;
import com.codyy.erpsportal.commons.controllers.adapters.ObjectsAdapter;
import com.codyy.erpsportal.commons.controllers.viewholders.AbsViewHolder;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.entities.FilterItem;
import com.codyy.erpsportal.commons.models.network.JsonArrayPostRequest;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试、作业模块-筛选过滤
 * added by eachann 2016-03-01
 */
public class TaskFilterFragment extends Fragment implements FilterParamsProvider {

    private static final String TAG = TaskFilterFragment.class.getSimpleName();
    /**
     * 右侧筛选项目内容
     */
    private ListView mOptionsLv;

    /**
     * 左侧帅选项目内容
     */
    private ListView mChoiceLv;

    /**
     * 右侧项目列表当前被选中项
     */

    private ObjectsAdapter<FilterItem, FilterItemViewHolder> mOptionsAdapter;

    private ObjectsAdapter<Choice, ChoiceViewHolder> mChoiceAdapter;

    private View mView;

    private String mUuid;
    private static String ARG_ITEMS = "ARG_ITEMS";
    private static String ARG_UUID = "ARG_UUID";

    public static TaskFilterFragment newInstance(ArrayList<FilterItem> items, String uuid) {
        TaskFilterFragment fragment = new TaskFilterFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_ITEMS, items);
        bundle.putString(ARG_UUID, uuid);
        fragment.setArguments(bundle);
        return fragment;
    }

    public TaskFilterFragment() {
    }

    private int mLastOptionPos = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUuid(getArguments().getString(ARG_UUID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_resource_filter, container, false);
            mOptionsLv = (ListView) mView.findViewById(R.id.first);
            mOptionsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final FilterItem item = mOptionsAdapter.getItem(position);
                    RequestQueue requestQueue = RequestManager.getRequestQueue();
                    Map<String, String> params = new HashMap<>();
                    mLastOptionPos = position;
                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Cog.d(TAG, "onErrorResponse error:" + error);
                            uncheckItem(position);
                            UIUtils.toast(R.string.net_error, Toast.LENGTH_SHORT);
                        }
                    };
                    Cog.d(TAG, "sendRequest=", item.getUrl(), params);
                    if (item.getResponseType() == FilterItem.ARRAY) {
                        switch (item.getParamName()) {
                            case "areaName":
                                JSONArray areaNameArray = new JSONArray();
                                for (String str : getResources().getStringArray(R.array.areas_province)) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("id", str);
                                        jsonObject.put("name", str);
                                        areaNameArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + areaNameArray.toString());
                                handleJsonItems(areaNameArray, item);
                                break;
                            case "year":
                                JSONArray yearArray = new JSONArray();
                                for (int i = 0; i < 11; i++) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        if(i==10) {
                                            jsonObject.put("id", "其他");
                                            jsonObject.put("name", "其他");
                                        }else {
                                            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - i);
                                            jsonObject.put("id", year);
                                            jsonObject.put("name", year);
                                        }
                                        yearArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + yearArray.toString());
                                handleJsonItems(yearArray, item);
                                break;
                            case "state":
                                JSONArray stateArray = new JSONArray();
                                for (int i = 0; i < getResources().getStringArray(R.array.states_id).length; i++) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("id", getResources().getStringArray(R.array.states_id)[i]);
                                        jsonObject.put("name", getResources().getStringArray(R.array.states_name)[i]);
                                        stateArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + stateArray.toString());
                                handleJsonItems(stateArray, item);
                                break;
                            case "parState":
                                JSONArray parStateArray = new JSONArray();
                                for (int i = 0; i < getResources().getStringArray(R.array.states_id).length; i++) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("id", getResources().getStringArray(R.array.states_id)[i]);
                                        jsonObject.put("name", getResources().getStringArray(R.array.par_states_name)[i]);
                                        parStateArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + parStateArray.toString());
                                handleJsonItems(parStateArray, item);
                                break;
                            case "stateSelf":
                                JSONArray stateSelfArray = new JSONArray();
                                for (int i = 0; i < getResources().getStringArray(R.array.self_states_id).length; i++) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("id", getResources().getStringArray(R.array.self_states_id)[i]);
                                        jsonObject.put("name", getResources().getStringArray(R.array.self_states_name)[i]);
                                        stateSelfArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + stateSelfArray.toString());
                                handleJsonItems(stateSelfArray, item);
                                break;
                            case "schoolClsState":
                                JSONArray schoolClsStateArray = new JSONArray();
                                for (int i = 0; i < getResources().getStringArray(R.array.school_states_id).length; i++) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("id", getResources().getStringArray(R.array.school_states_id)[i]);
                                        jsonObject.put("name", getResources().getStringArray(R.array.school_states_name)[i]);
                                        schoolClsStateArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + schoolClsStateArray.toString());
                                handleJsonItems(schoolClsStateArray, item);
                                break;
                            case "teaState"://作业模块-老师-筛选
                                JSONArray teaStateArray = new JSONArray();
                                for (int i = 0; i < getResources().getStringArray(R.array.school_states_id).length; i++) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("id", getResources().getStringArray(R.array.school_states_id)[i]);
                                        jsonObject.put("name", getResources().getStringArray(R.array.work_tea_states_name)[i]);
                                        teaStateArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + teaStateArray.toString());
                                handleJsonItems(teaStateArray, item);
                                break;
                            case "stuState"://作业模块-学生、家长筛选
                                JSONArray stuStateArray = new JSONArray();
                                for (int i = 0; i < getResources().getStringArray(R.array.work_states_id).length; i++) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("id", getResources().getStringArray(R.array.work_states_id)[i]);
                                        jsonObject.put("name", getResources().getStringArray(R.array.states_name)[i]);
                                        stuStateArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + stuStateArray.toString());
                                handleJsonItems(stuStateArray, item);
                                break;
                            case "stuSelfState"://作业模块-学生-我的批阅筛选
                                JSONArray stuSelfStateArray = new JSONArray();
                                for (int i = 0; i < getResources().getStringArray(R.array.work_stu_states_id).length; i++) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("id", getResources().getStringArray(R.array.work_stu_states_id)[i]);
                                        jsonObject.put("name", getResources().getStringArray(R.array.work_stu_states_name)[i]);
                                        stuSelfStateArray.put(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                                Cog.d(TAG, "onResponse JSONArray response:" + stuSelfStateArray.toString());
                                handleJsonItems(stuSelfStateArray, item);
                                break;
                            default:
                                requestQueue.add(new JsonArrayPostRequest(item.getUrl(), params, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Cog.d(TAG, "onResponse JSONArray response:" + response);
                                        handleJsonItems(response, item);
                                    }
                                }, errorListener));
                                break;
                        }

                    } else {
                        if ("classId".equals(item.getParamName())) {
                            params.put("uuid", mUuid);
                            if (position == 2) { //点击班级
                                FilterItem classLevelItem = mOptionsAdapter.getItem(0);
                                Choice classLevel = classLevelItem.getChoice();
                                if (classLevel == null || classLevel.getId() == null) {
                                    uncheckItem(2);
                                    shortlyToast(getString(R.string.exam_filter_toast, mOptionsAdapter.getItem(0).getTypeName()));
                                    return;
                                }
                            }
                            params.put("classlevelId", mOptionsAdapter.getItem(0).getChoice().getId());
                        }
                        requestQueue.add(new NormalPostRequest(item.getUrl(), params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Cog.d(TAG, "onResponse JSONObject response:" + response);
                                if ("success".equals(response.optString("result"))) {
                                    handleJsonItems(response.optJSONArray("list"), item);
                                }
                            }
                        }, errorListener));
                    }

                }
            });

            mChoiceLv = (ListView) mView.findViewById(R.id.second);
//        mChoiceLv.setEmptyView(mSecondEmptyView);
            mChoiceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Choice choice = mChoiceAdapter.getItem(position);
                    int currentPosition = mOptionsLv.getCheckedItemPosition();
                    boolean hasChanged = mOptionsAdapter.getItem(currentPosition).setChoice(choice);
                    if (hasChanged) {
                        clearItemsAfter(currentPosition + 1);
                    }
                    mOptionsAdapter.notifyDataSetChanged();
                    int nextPosition = currentPosition + 1;
                    //选全部不自动跳下个选择项
                    if (position != 0 && nextPosition < mOptionsAdapter.getCount()) {
                        mOptionsLv.performItemClick(null, nextPosition, 0);
                    }
                }
            });

            mOptionsAdapter = new ObjectsAdapter<>(
                    getActivity(), FilterItemViewHolder.class);
            mChoiceAdapter = new ObjectsAdapter<>(
                    getActivity(), ChoiceViewHolder.class);
            mOptionsLv.setAdapter(mOptionsAdapter);
            mChoiceLv.setAdapter(mChoiceAdapter);
            initOptionItems();
        }
        return mView;
    }

    private void handleJsonItems(JSONArray jsonArray, FilterItem item) {
        List<Choice> choices = item.getChoiceParser().parseArray(jsonArray);
        if (choices == null) {
            choices = new ArrayList<>(1);
        }
        choices.add(0, new Choice("全部"));
        mChoiceAdapter.setData(choices);
        mChoiceAdapter.notifyDataSetChanged();
        if (item.getChoice() == null) {
            mChoiceLv.setItemChecked(0, true);
        } else {
            int idx = choices.indexOf(item.getChoice());
            mChoiceLv.setItemChecked(idx, true);
        }
    }

    private void uncheckItem(int position) {
        mOptionsLv.setItemChecked(position, false);
        if (mLastOptionPos != -1) {
            mOptionsLv.setItemChecked(mLastOptionPos, true);
        } else {
            mChoiceAdapter.clearData();
            mChoiceAdapter.notifyDataSetChanged();
        }
    }

    private void shortlyToast(String msg) {
        UIUtils.toast(getActivity(), msg, Toast.LENGTH_SHORT);
    }

    /**
     * 清除start位置之后的已选项，包括start
     *
     * @param start
     */
    private void clearItemsAfter(int start) {
        for (int i = start; i < mOptionsAdapter.getCount(); i++) {
            mOptionsAdapter.getItem(i).setChoice(null);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initOptionItems() {
        ArrayList<FilterItem> items = getArguments().getParcelableArrayList(ARG_ITEMS);
        mOptionsAdapter.addData(items);
    }

    public void setUuid(String uuid) {
        this.mUuid = uuid;
    }

    /**
     * 获取筛选数据
     *
     * @return
     */
    public List<FilterItem> getResourceFilterItems() {
        return mOptionsAdapter.getItems();
    }

    @Override
    public Map<String, String> acquireFilterParams() {
        return FilterItem.obtainParams(getResourceFilterItems());
    }

    public static class FilterItemViewHolder extends AbsViewHolder<FilterItem> {

        private TextView title;

        private TextView content;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_first;
        }

        @Override
        public void mapFromView(View view) {
            this.title = (TextView) view.findViewById(R.id.title);
            this.content = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public void setDataToView(FilterItem data, Context context) {
            title.setText(data.getTypeName());
            Choice choice = data.getChoice();
            if (choice == null) {
                content.setText(R.string.all);
            } else {
                content.setText(choice.getTitle());
            }
        }
    }

    public static class ChoiceViewHolder extends AbsViewHolder<Choice> {

        private TextView title;

        @Override
        public int obtainLayoutId() {
            return R.layout.item_second;
        }

        @Override
        public void mapFromView(View view) {
            title = (TextView) view;
        }

        @Override
        public void setDataToView(Choice data, Context context) {
            title.setText(data.getTitle());
        }
    }
}
