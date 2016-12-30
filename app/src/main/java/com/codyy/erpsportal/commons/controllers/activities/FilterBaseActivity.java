package com.codyy.erpsportal.commons.controllers.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.erpsportal.commons.controllers.fragments.FilterFragment;
import com.codyy.erpsportal.commons.models.UserInfoKeeper;
import com.codyy.erpsportal.commons.models.entities.AreaBase;
import com.codyy.erpsportal.commons.models.network.RequestSender;
import com.codyy.erpsportal.commons.widgets.components.FilterButton;

import org.json.JSONObject;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kmdai on 16-7-4.
 */
public abstract class FilterBaseActivity extends AppCompatActivity {
    private Integer mHashTag = this.hashCode();
    @Bind(R.id.title_text)
    TextView mTextView;
    @Bind(R.id.drawerlayout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.btn_filter)
    FilterButton mFilterBtn;
    private FilterFragment mFilterFragment;
    private RequestSender mRequestSender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_class);
        ButterKnife.bind(this);
        mRequestSender = new RequestSender(this);
        mFilterFragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", FilterFragment.SCREEN_TIMETABLEVIEW);
        bundle.putParcelable("userInfo", UserInfoKeeper.obtainUserInfo());
        mFilterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.filter_fragment, mFilterFragment).commitAllowingStateLoss();
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
//                if (mFilterIV.getVisibility() == View.VISIBLE) {
//                    mFilterIV.setAlpha(1 - slideOffset);
//                    mFilterTextView.setAlpha(slideOffset);
//                    if (mFilterTextView.getVisibility() == View.GONE) {
//                        mFilterTextView.setVisibility(View.VISIBLE);
//                    }
//
//                } else if (mFilterTextView.getVisibility() == View.VISIBLE) {
//                    mFilterTextView.setAlpha(slideOffset);
//                    mFilterIV.setAlpha(1 - slideOffset);
//                    if (mFilterIV.getVisibility() == View.GONE) {
//                        mFilterIV.setVisibility(View.VISIBLE);
//                    }
//                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mFilterBtn.setFiltering(true);
//                mFilterIV.setVisibility(View.GONE);
//                mFilterTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mFilterBtn.setFiltering(false);
//                mFilterTextView.setVisibility(View.GONE);
//                mFilterIV.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int newState) { }
        });
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_fraglayout, fragment).commitAllowingStateLoss();
    }

    public void setTitle(String title) {
        mTextView.setText(title);
    }

    public void onBack(View view) {
        this.finish();
    }

    @OnClick(R.id.btn_filter)
    public void onFilterSelect(View view) {
        mFilterBtn.toggle();
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
            AreaBase areaBase = mFilterFragment.getLastArea();
            if ("area".equals(areaBase.getType())) {
                onFilter(areaBase.getAreaId(), null, areaBase.isDirect);
            } else {
                onFilter(areaBase.getAreaId(), areaBase.getSchoolID(), areaBase.isDirect);
            }
        } else {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    protected void httpConnect(String url, Map<String, String> param, final int msg) {
        mRequestSender.sendRequest(new RequestSender.RequestData(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onGetResult(response, msg);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, mHashTag));
    }

    /**
     * 返回地区ｉｄ、学校ｉｄ
     *
     * @param areaID
     * @param schoolID
     */
    public abstract void onFilter(String areaID, String schoolID, boolean isAreaSchool);

    protected abstract void onGetResult(JSONObject object, int msg);

    protected abstract void onError(VolleyError error, int msg);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mRequestSender.stop(mHashTag);
        super.onDestroy();
    }
}
