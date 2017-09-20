package com.codyy.erpsportal.commons.models.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.codyy.erpsportal.commons.utils.NumberUtils;
import com.codyy.erpsportal.commons.utils.UriUtils;
import com.codyy.tpmp.filterlibrary.models.BaseTitleItemBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxinwu on 2015/8/11.
 */
public class PrepareLessonsShortEntityParse  {
    private int total;
    private List<PrepareLessonsShortEntity> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<PrepareLessonsShortEntity> getList() {
        return list;
    }

    public void setList(List<PrepareLessonsShortEntity> list) {
        this.list = list;
    }
}
