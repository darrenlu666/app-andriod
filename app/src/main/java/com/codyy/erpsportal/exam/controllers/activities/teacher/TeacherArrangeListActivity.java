package com.codyy.erpsportal.exam.controllers.activities.teacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.Choice;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.DividerItemDecoration;
import com.codyy.erpsportal.exam.controllers.activities.media.adapters.MMBaseRecyclerViewAdapter;
import com.codyy.erpsportal.exam.models.entities.TreeItem;
import com.codyy.url.URLConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by eachann on 2016/3/2.
 */
@Deprecated
public class TeacherArrangeListActivity extends ToolbarActivity {
    private static final int REQUEST_CODE = 100;
    private static final int RESULT_CODE = 200;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitle;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private ArrayList<Choice> mList = new ArrayList<>();
    private ListAdapter mAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE && data != null) {
            List<TreeItem> list = data.getParcelableArrayListExtra(EXTRA_DATA);
            TeacherArrangeActivity.setResultActivity(list, TeacherArrangeListActivity.this);
            finish();
        }
    }

    public static void setResultActivity(List<TreeItem> list, Activity activity) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_DATA, (ArrayList<? extends Parcelable>) list);
        activity.setResult(RESULT_CODE, intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new ListAdapter(new ArrayList<Choice>(), this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        mAdapter.setOnInViewClickListener(R.id.rl_refect, new MMBaseRecyclerViewAdapter.onInternalClickListener<Choice>() {
            @Override
            public void OnClickListener(View parentV, View v, Integer position, Choice values) {
                try {
                    TeacherArrangeTreeActivity.startActivity(TeacherArrangeListActivity.this, mArray.getJSONObject(position).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnLongClickListener(View parentV, View v, Integer position, Choice values) {

            }
        });
        getData();

    }

    private JSONArray mArray;

    private void getData() {
        Map<String, String> param = new HashMap<>();
        param.put("uuid", UserInfoKeeper.getInstance().getUserInfo().getUuid());
        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(URLConfig.GET_SELECT_STU_BY_TEACHER, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Cog.i(TeacherArrangeListActivity.class.getSimpleName(), response.toString());
                if (response.isNull("list")) return;
                try {
                    mArray = response.getJSONArray("list");
                    for (int i = 0; i < mArray.length(); i++) {
                        JSONObject object = mArray.getJSONObject(i);
                        Choice choice = new Choice(object.isNull("id")?"":object.getString("id"), object.isNull("name")?"":object.getString("name"));
                        mList.add(choice);
                    }
                    mAdapter.setList(mList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable error) {

            }
        }));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_teacher_arrange_list_layout;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        setViewAnim(false, mTitle);
        mTitle.setText(getString(R.string.exam_arrange));

    }

    class ListAdapter extends MMBaseRecyclerViewAdapter<Choice> {

        public ListAdapter(List<Choice> list, Context context) {
            super(list, context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            final View view = LayoutInflater.from(context).inflate(R.layout.item_teacher_arrange_list, parent, false);
            return new NormalRecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            ((NormalRecyclerViewHolder) holder).tvName.setText(list.get(position).getTitle());
        }

        public class NormalRecyclerViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;

            public NormalRecyclerViewHolder(View view) {
                super(view);
                tvName = (TextView) view.findViewById(R.id.tv_name);
            }
        }
    }
}
