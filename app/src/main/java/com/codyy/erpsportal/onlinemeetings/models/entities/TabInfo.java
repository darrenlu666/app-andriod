package com.codyy.erpsportal.onlinemeetings.models.entities;

/**
 * FragmentTabHost tab widget info .
 * Created by poe on 15-8-27.
 */
public class TabInfo {

    private int mTabIcon;
    private String mTabTitle;

    public TabInfo(int icon, String title) {
        this.mTabIcon = icon;
        this.mTabTitle = title;
    }

    public int getTabIcon() {
        return mTabIcon;
    }

    public void setTabIcon(int mTabIcon) {
        this.mTabIcon = mTabIcon;
    }

    public String getTabTitle() {
        return mTabTitle;
    }

    public void setTabTitle(String mTabTitle) {
        this.mTabTitle = mTabTitle;
    }
}
