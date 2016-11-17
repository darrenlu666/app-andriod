package com.codyy.erpsportal.statistics.models.entities;

/**
 * 统计表一行数据
 * Created by gujiajia on 2016/5/31.
 */
public abstract class AbsStatRow {

    public AbsStatRow(){}

    public AbsStatRow(String rowTitle) {
        this.rowTitle = rowTitle;
    }

    /**
     * 行标题
     */
    private String rowTitle;

    public String getRowTitle() {
        return rowTitle;
    }

    public void setRowTitle(String rowTitle) {
        this.rowTitle = rowTitle;
    }

    public abstract String getCellByIndex(int index);

    public abstract int getCellStatus(int index);
}
