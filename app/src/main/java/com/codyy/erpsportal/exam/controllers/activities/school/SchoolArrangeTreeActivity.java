package com.codyy.erpsportal.exam.controllers.activities.school;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.exam.controllers.viewholders.SelectableHeaderHolder;
import com.codyy.erpsportal.exam.controllers.viewholders.SelectableItemHolder;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.exam.models.entities.TreeItem;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.ToastUtil;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.media.node.model.TreeNode;
import com.codyy.media.node.view.AndroidTreeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * 布置试卷-选择人员列表
 * Created by eachann on 2016/1/22.
 */
public class SchoolArrangeTreeActivity extends ToolbarActivity {
    private static final String TAG = SchoolArrangeTreeActivity.class.getSimpleName();
    private AndroidTreeView mTreeView;
    private Context mContext = this;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitle;
    @Bind(R.id.container)
    LinearLayout mContainer;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", mTreeView.getSaveState());
    }

    public static void startActivity(Activity from, String examId, ArrayList<TreeItem> mTreeItemList) {
        Intent intent = new Intent(from, SchoolArrangeTreeActivity.class);
        intent.putExtra(EXTRA_EXAM_ID, examId);
        intent.putParcelableArrayListExtra(EXTRA_DATA, mTreeItemList);
        from.startActivityForResult(intent, 100);
        UIUtils.addEnterAnim(from);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestSender sender = new RequestSender(this);
        sender.sendRequest(new RequestSender.RequestData(getUrl(), getParms(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ("success".equals(response.optString("result")) && mToolbar !=null) {
                    parseJson(response);
                } else {
                    ToastUtil.showToast(getString(R.string.refresh_state_loade_error));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.showToast(getString(R.string.refresh_state_loade_error));
            }
        }));
        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                mTreeView.restoreState(state);
            }
        }
    }

    private void parseJson(JSONObject response) {
        TreeNode root = TreeNode.root();
        try {
            ArrayList<TreeItem> list = getIntent().getParcelableArrayListExtra(EXTRA_DATA);
            JSONArray jsonArray = response.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                TreeItem parentTreeItem = new TreeItem(object.isNull("id") ? "" : object.getString("id"),
                        object.isNull("name") ? "" : object.getString("name"), null, null,
                        false, 2);
                for (TreeItem item : list) {
                    if (object.optString("id").equals(item.getClasslevelId()) && object.optString("name").equals(item.getClasslevelName())) {
                        parentTreeItem.setSelected(true);
                    }
                }
                TreeNode parentTreeNode = new TreeNode(parentTreeItem).setViewHolder(new SelectableHeaderHolder(mContext));
                parentTreeNode.setSelected(parentTreeItem.isSelected());
                JSONArray childArray = object.getJSONArray("children");
                for (int j = 0; j < childArray.length(); j++) {
                    JSONObject childObject = childArray.getJSONObject(j);
                    TreeItem childTreeItem = new TreeItem(childObject.isNull("id") ? "" : childObject.getString("id"),
                            childObject.isNull("name") ? "" : childObject.getString("name"), null, null,
                            false, 1);
                    for (TreeItem item : list) {
                        if (childObject.optString("id").equals(item.getClasslevelId()) && childObject.optString("name").equals(item.getClasslevelName())) {
                            childTreeItem.setSelected(true);
                        }
                    }
                    TreeNode child = new TreeNode(childTreeItem).setViewHolder(new SelectableItemHolder(this));
                    child.setSelected(childTreeItem.isSelected());
                    parentTreeNode.addChild(child);
                }
                root.addChild(parentTreeNode);
            }
            mTreeView = new AndroidTreeView(this, root);
            mTreeView.setDefaultAnimation(true);
            mTreeView.setSelectionModeEnabled(true);
            mContainer.addView(mTreeView.getView());
            mTvComplete.setEnabled(true);
        } catch (JSONException e) {
            ToastUtil.showToast(getString(R.string.refresh_state_loade_error));
            e.printStackTrace();
        }

    }

    private Map<String, String> getParms() {
        Map<String, String> parms = new HashMap<>();
        parms.put("uuid", UserInfoKeeper.obtainUserInfo().getUuid());
        parms.put("examId", getIntent().getStringExtra(EXTRA_EXAM_ID));
        return parms;
    }

    private String getUrl() {
        return URLConfig.GET_RECEIVE_PERSONS;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_arrange_tree;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        setViewAnim(false, mTitle);
        mTitle.setText(getString(R.string.exam_arrange_select_person));

    }

    private TextView mTvComplete;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_button, menu);
        MenuItem menuItem = menu.findItem(R.id.task_read_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        mTvComplete = (TextView) linearLayout.findViewById(R.id.task_title);
        mTvComplete.setEnabled(false);
        mTvComplete.setText(getString(R.string.exam_arrange_select_person_completed));
        mTvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TreeItem> list = mTreeView.getSelectedValues(TreeItem.class);
                if (list == null) {
                    list = new ArrayList<>();
                }
                SchoolArrangeActivity.setResultActivity(list, (Activity) mContext);
                Cog.i(TAG, list.toString());
                finish();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}
