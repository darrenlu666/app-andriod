package com.codyy.erpsportal.exam.controllers.activities.teacher;

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

import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.activities.ToolbarActivity;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.models.network.Response;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.exam.controllers.viewholders.SelectableHeaderHolder;
import com.codyy.erpsportal.exam.controllers.viewholders.SelectableItemHolder;
import com.codyy.erpsportal.exam.models.entities.TreeItem;
import com.codyy.media.node.model.TreeNode;
import com.codyy.media.node.view.AndroidTreeView;
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
 * 布置试卷-选择人员列表
 * Created by eachann on 2016/1/22.
 */
public class TeacherArrangeTreeActivity extends ToolbarActivity {
    protected static final String EXTRA_DATA = ToolbarActivity.class.getPackage() + ".EXTRA_DATA";
    private static final String TAG = TeacherArrangeTreeActivity.class.getSimpleName();
    private AndroidTreeView mTreeView;
    private List<AndroidTreeView> mTreeViewList = new ArrayList<>();
    private Context mContext = this;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mTitle;
    @Bind(R.id.container)
    LinearLayout mContainer;
    private TreeItem mTreeItem;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", mTreeView.getSaveState());
    }

    public static void startActivity(Activity from, String list) {
        Intent intent = new Intent(from, TeacherArrangeTreeActivity.class);
        intent.putExtra(EXTRA_DATA, list);
        from.startActivityForResult(intent, 100);
        UIUtils.addEnterAnim(from);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
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
            /*mTreeItem = new TreeItem(response.isNull("id") ? "" : response.getString("id"),
                    response.isNull("name") ? "" : response.getString("name"),
                    true, 2);*/
            TreeNode orginTreeNode = new TreeNode(
                    new TreeItem(response.isNull("id") ? "" : response.getString("id"),
                            response.isNull("name") ? "" : response.getString("name"), null, null,
                            false, 2)).setViewHolder(new SelectableHeaderHolder(mContext));
            orginTreeNode.setExpanded(false);
            orginTreeNode.setSelected(false);
            JSONArray jsonArray = response.getJSONArray("childList");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                JSONArray childArray = object.getJSONArray("childList");
                TreeNode parentTreeNode = new TreeNode(
                        new TreeItem(object.isNull("id") ? "" : object.getString("id"),
                                object.isNull("name") ? "" : object.getString("name"), response.isNull("name") ? "" : response.getString("name"), childArray.toString(),
                                false, 1)).setViewHolder(new SelectableItemHolder(mContext));
/*                JSONArray childArray = object.getJSONArray("childList");
                for (int j = 0; j < childArray.length(); j++) {
                    JSONObject childObject = childArray.getJSONObject(j);
                    TreeNode child = new TreeNode(new TreeItem(childObject.isNull("id") ? "" : childObject.getString("id"),
                            childObject.isNull("name") ? "" : childObject.getString("name"),
                            false, 1)).setViewHolder(new SelectableItemHolder(this));
                    parentTreeNode.addChild(child);
                }*///去除第三级
                orginTreeNode.addChild(parentTreeNode);
            }
            root.addChild(orginTreeNode);
            mTreeView = new AndroidTreeView(this, root);
            mTreeView.setDefaultAnimation(true);
            mTreeView.setSelectionModeEnabled(true);
            mContainer.addView(mTreeView.getView());
            mTreeViewList.add(mTreeView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                if (response.isNull("list") || mToolbar == null) return;
                try {
                    mArray = response.getJSONArray("list");
                    for (int i = 0; i < mArray.length(); i++) {
                        JSONObject jsonObject = mArray.optJSONObject(i);
                        parseJson(jsonObject);
                    }

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
        return R.layout.activity_arrange_tree;
    }

    @Override
    protected void initToolbar() {
        super.initToolbar(mToolbar);
        setViewAnim(false, mTitle);
        mTitle.setText(getString(R.string.exam_arrange_select_person));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_button, menu);
        MenuItem menuItem = menu.findItem(R.id.task_read_menu);
        LinearLayout linearLayout = (LinearLayout) menuItem.getActionView();
        TextView textView = (TextView) linearLayout.findViewById(R.id.task_title);
        textView.setText(getString(R.string.exam_arrange_select_person_completed));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TreeItem> selectList = new ArrayList<>();
                for (int t = 0; t < mTreeViewList.size(); t++) {
                    List<TreeItem> list = mTreeViewList.get(t).getSelectedValues(TreeItem.class);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getType() == 1)
                            selectList.add(list.get(i));
                    }
                    /*TreeItemList treeItemList = new TreeItemList();
                    if (list == null || list.size() == 0) {
                        list = new ArrayList<>();
                    } else {
                        try {
                            JSONObject response = mArray.getJSONObject(t);//new JSONObject(getIntent().getStringExtra(EXTRA_DATA));
                            JSONArray jsonArray = response.getJSONArray("childList");
                            for (int i = 0; i < jsonArray.length(); i++) {//班级
                                JSONObject object = jsonArray.getJSONObject(i);
                                JSONArray childArray = object.getJSONArray("childList");
                                for (int j = 0; j < childArray.length(); j++) {
                                    JSONObject objectGroup = childArray.getJSONObject(j);
                                    JSONArray arrayGroup = objectGroup.getJSONArray("childList");
                                    TreeItem treeItemGroup = new TreeItem(objectGroup.isNull("id") ? "" : objectGroup.getString("id"),
                                            objectGroup.isNull("name") ? "" : objectGroup.getString("name"),
                                            false, 1);
                                    int position = -1;
                                    for (int h = 0; h < list.size(); h++) {
                                        if (list.get(h).compareTo(treeItemGroup) == 0) {
                                            position = h;
                                            list.remove(h);
                                            break;
                                        }
                                    }
                                    if (position == -1)
                                        break;
                                    else
                                        for (int k = 0; k < arrayGroup.length(); k++) {
                                            JSONObject objectGroupChild = arrayGroup.getJSONObject(k);
                                            list.add(position, new TreeItem(objectGroupChild.isNull("id") ? "" : objectGroupChild.getString("id"),
                                                    objectGroupChild.isNull("name") ? "" : objectGroupChild.getString("name"),
                                                    true, 3));
                                        }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        list.add(0, mTreeItem);
                    }*/
                }
                //TeacherArrangeListActivity.setResultActivity(selectList, (Activity) mContext);
                TeacherArrangeActivity.setResultActivity(selectList, (Activity) mContext);
                finish();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
