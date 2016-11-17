package com.codyy.erpsportal.onlineteach.models.entities;

import java.util.List;

/**
 * Created by poe on 16-6-21.
 */
public class NetTeachParse {
    private int total ;//总数
    private List<NetTeach> data ;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<NetTeach> getData() {
        return data;
    }

    public void setData(List<NetTeach> data) {
        this.data = data;
    }
}
