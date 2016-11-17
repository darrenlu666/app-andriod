
package com.codyy.erpsportal.reservation.controllers.activities;

import android.os.Bundle;

import com.android.volley.VolleyError;
import com.codyy.erpsportal.commons.controllers.activities.FilterBaseActivity;
import com.codyy.erpsportal.commons.models.Titles;
import com.codyy.erpsportal.reservation.controllers.fragments.ReservationClassFragment;

import org.json.JSONObject;

/**
 * 应用--约课列表
 */
public class ReservationClassActivity extends FilterBaseActivity {
    private ReservationClassFragment mReservationClassFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(Titles.sWorkspaceNetClassClass);
        mReservationClassFragment = ReservationClassFragment.newInstance();
        setFragment(mReservationClassFragment);
    }

    @Override
    public void onFilter(String areaID, String schoolID, boolean isDirect) {
        mReservationClassFragment.onFilter(areaID, schoolID, isDirect);
    }

    @Override
    protected void onGetResult(JSONObject object, int msg) {

    }

    @Override
    protected void onError(VolleyError error, int msg) {

    }
}
