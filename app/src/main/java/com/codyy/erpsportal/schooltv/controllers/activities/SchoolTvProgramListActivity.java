package com.codyy.erpsportal.schooltv.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codyy.erpsportal.Constants;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.commons.models.entities.UserInfo;
import com.codyy.erpsportal.commons.utils.UIUtils;
import com.codyy.erpsportal.groups.controllers.activities.SimpleRecyclerActivity;
import com.codyy.erpsportal.groups.controllers.fragments.SimpleRecyclerDelegate;
import com.codyy.erpsportal.schooltv.models.SchoolProgram;
import com.codyy.url.URLConfig;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import butterknife.Bind;

/**
 * 校园电视台-节目单
 * Created by poe on 17-3-14.
 */
public class SchoolTvProgramListActivity extends SimpleRecyclerActivity<SchoolProgram> {
    private static final String TAG = "SchoolTvProgramListActivity";
    @Bind(R.id.tab_layout)TabLayout mTabLayout;

    @Override
    public int obtainLayoutId() {
        //return a customized layout .
        return R.layout.activity_school_tv_program_list;
    }

    @Override
    public void preInitArguments() {

    }

    @Override
    public void init() {
        super.init();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("星期一");
        arrayList.add("星期二二");
        arrayList.add("星期三");
        arrayList.add("星期四二");
        arrayList.add("星期五");
        arrayList.add("星期六二");
        arrayList.add("星期日");

        for(String title : arrayList){
            View customView = LayoutInflater.from(this).inflate(R.layout.tab_item_two_line_text,null);
            ((TextView)customView.findViewById(R.id.tab_item_title)).setText(title);
            ((TextView)customView.findViewById(R.id.tab_item_content)).setText("03-15");
            mTabLayout.addTab(mTabLayout.newTab().setText(title).setCustomView(customView));
        }

        mTabLayout.setTabTextColors(R.color.grey_444,R.color.main_color);
//        mTabLayout.setScrollPosition(3,0f,true);
        mTabLayout.getTabAt(3).select();
        mTabLayout.setSelectedTabIndicatorHeight(0);
        setTitle(Titles.sWorkspaceTvProgramProgramList);
    }

    @Override
    public SimpleRecyclerDelegate<SchoolProgram> getSimpleRecyclerDelegate() {
        return new SimpleRecyclerDelegate<SchoolProgram>() {
            @Override
            public String obtainAPI() {
                return URLConfig.GET_CUSTOMER_LIVING_WATCH_COUNT;
            }

            @Override
            public HashMap<String, String> getParams() {
                return null;
            }

            @Override
            public void parseData(JSONObject response) {

            }

            @Override
            public BaseRecyclerViewHolder<SchoolProgram> getViewHolder(ViewGroup parent) {
                return null;
            }

            @Override
            public void OnItemClicked(View v, int position, SchoolProgram data) {

            }

            @Override
            public int getTotal() {
                return 0;
            }
        };
    }

    public static void start(Activity act , UserInfo userInfo){
        Intent intent = new Intent(act,SchoolTvProgramListActivity.class);
        intent.putExtra(Constants.USER_INFO,userInfo);
        act.startActivity(intent);
        UIUtils.addEnterAnim(act);
    }
}
