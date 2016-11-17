package com.codyy.erpsportal.commons.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codyy.erpsportal.R;
import com.codyy.url.URLConfig;
import com.codyy.erpsportal.commons.controllers.adapters.LocationAdapter;
import com.codyy.erpsportal.commons.models.entities.LocationBean;
import com.codyy.erpsportal.commons.models.network.NormalPostRequest;
import com.codyy.erpsportal.commons.models.network.RequestManager;
import com.codyy.erpsportal.commons.utils.Cog;
import com.codyy.erpsportal.commons.widgets.TitleBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 地区选择界面
 */
@Deprecated
public class LocationActivity extends AppCompatActivity {

    private final static String TAG = "LocationActivity";

    List<LocationBean> mData = new ArrayList<>();

    public static final String LOCATION_ID = "locationid";
    public static final String LOCATION_NAME = "locationname";
    public static final String LOCATION_FETCH_TYPE = "locationtype";

    public static final String TYPE_AREA = "area";
    public static final String TYPE_SCHOOL = "school";

    @Bind(R.id.location_list)
    protected ListView mList;
    @Bind(R.id.title_bar)
    protected TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        final String locationId = getIntent().getStringExtra(LOCATION_ID);
        final String locationName = getIntent().getStringExtra(LOCATION_NAME);
        final String fetchType = getIntent().getStringExtra(LOCATION_FETCH_TYPE);

        this.mTitleBar.setTitle(locationName);

        RequestQueue requestQueue = RequestManager.getRequestQueue();
        Cog.d(TAG, (TYPE_AREA.equals(fetchType) ? URLConfig.LOCATION : URLConfig.LOCATION_SCHOOL) + locationId);
        final Map<String, String> params = new HashMap<>();
        params.put("areaId", locationId);
        requestQueue.add(new NormalPostRequest((TYPE_AREA.equals(fetchType) ? URLConfig.LOCATION : URLConfig.LOCATION_SCHOOL), params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if ("success".equals(response.optString("result"))) {
                            mData = new ArrayList<>();
                            if (TYPE_AREA.equals(fetchType)) {
                                LocationBean locationBean2 = new LocationBean();
                                locationBean2.setId(locationId);
                                locationBean2.setName(locationName);
                                locationBean2.setIsFinal(true);
                                mData.add(locationBean2);
                                JSONArray areas = response.optJSONArray("areas");
                                if (areas.length() == 0) {
                                    RequestQueue requestQueue = RequestManager.getRequestQueue();
                                    requestQueue.add(new NormalPostRequest(URLConfig.LOCATION_SCHOOL, params,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    if ("success".equals(response.optString("result"))) {
                                                        JSONArray areas = response.optJSONArray("schools");
                                                        if (areas == null) return;
                                                        int len = areas.length();
                                                        for (int i = 0; i < len; i++) {
                                                            JSONObject area = areas.optJSONObject(i);

                                                            LocationBean locationBean = new LocationBean();
                                                            locationBean.setId(area.optString("schoolId"));
                                                            locationBean.setName(area.optString("schoolName"));
                                                            locationBean.setIsSchool(true);
                                                            locationBean.setIsFinal(true);
                                                            mData.add(locationBean);
                                                        }
                                                        mList.setAdapter(new LocationAdapter(mData));
                                                        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                                returnAreaResult(mData.get(i));
                                                            }
                                                        });
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(LocationActivity.this, "网络请求出错!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    ));
                                    return;
                                }
                                int len = areas.length();
                                for (int i = 0; i < len; i++) {
                                    JSONObject area = areas.optJSONObject(i);
                                    LocationBean locationBean = new LocationBean();
                                    locationBean.setId(area.optString("areaId"));
                                    locationBean.setName(area.optString("areaName"));
                                    locationBean.setFetchType(TYPE_AREA);
                                    mData.add(locationBean);

                                }
                                if ("Y".equals(response.optString("hasDirect"))) {
                                    LocationBean locationBean = new LocationBean();
                                    locationBean.setId(locationId);
                                    locationBean.setName("直辖校");
                                    locationBean.setFetchType(TYPE_SCHOOL);
                                    mData.add(locationBean);
                                }
                                mList.setAdapter(new LocationAdapter(mData));
                                mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        if (mData.get(i).isFinal()) {
                                            returnAreaResult(mData.get(i));
                                        } else {
                                            Intent it = new Intent(LocationActivity.this, LocationActivity.class);
                                            it.putExtra(LOCATION_ID, mData.get(i).getId());
                                            it.putExtra(LOCATION_NAME, mData.get(i).getName());
                                            it.putExtra(LOCATION_FETCH_TYPE, mData.get(i).getFetchType());
                                            startActivityForResult(it,0);
                                        }
                                    }
                                });
                            } else {

                                JSONArray areas = response.optJSONArray("schools");
                                if (areas == null) return;
                                int len = areas.length();
                                for (int i = 0; i < len; i++) {
                                    JSONObject area = areas.optJSONObject(i);

                                    LocationBean locationBean = new LocationBean();
                                    locationBean.setId(area.optString("schoolId"));
                                    locationBean.setName(area.optString("schoolName"));
                                    locationBean.setIsFinal(true);
                                    locationBean.setIsSchool(true);
                                    mData.add(locationBean);

                                }
                                mList.setAdapter(new LocationAdapter(mData));
                                mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        returnAreaResult(mData.get(i));
                                    }
                                });
                            }
                        }
                    }
                }

                , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LocationActivity.this, "网络请求出错!", Toast.LENGTH_SHORT).show();
                    }
                }
        ));
    }

    private void returnAreaResult(LocationBean area) {
        Toast.makeText(LocationActivity.this, area.getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("area", area);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                setResult(RESULT_OK, data);
                finish();
                break;
            default:
                break;
        }
    }
}
