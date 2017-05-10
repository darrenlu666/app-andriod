package com.codyy.erpsportal.repairs.models.entities;

import java.util.List;

/**
 * 故障类型选择项
 * Created by gujiajia on 2017/3/28.
 */
public class CategoriesItem {

    /**
     * 选中项，没选为-1
     */
    private int position;

    /**
     * 故障列表
     */
    private List<MalfuncCategory> categories;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<MalfuncCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<MalfuncCategory> categories) {
        this.categories = categories;
    }

    public int count() {
        if (categories == null) return 0;
        return categories.size();
    }
}
