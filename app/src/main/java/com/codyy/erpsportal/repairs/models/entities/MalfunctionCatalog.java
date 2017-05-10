package com.codyy.erpsportal.repairs.models.entities;

import java.util.List;

/**
 * 常见问题中的类别
 * Created by gujiajia on 2017/4/10.
 */

public class MalfunctionCatalog {

    /**
     * catalogLevel : 1
     * catalogName : opp22
     * childCatalog : []
     * malGuideCatalogId : 6bfcb1b1b8ed4e24a42ab499e02489ae
     * parentId : -1
     * sort : 17
     */

    private int catalogLevel;
    private String catalogName;
    private String malGuideCatalogId;
    private String parentId;
    private int sort;
    private List<MalfunctionCatalog> childCatalog;

    public int getCatalogLevel() {
        return catalogLevel;
    }

    public void setCatalogLevel(int catalogLevel) {
        this.catalogLevel = catalogLevel;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getMalGuideCatalogId() {
        return malGuideCatalogId;
    }

    public void setMalGuideCatalogId(String malGuideCatalogId) {
        this.malGuideCatalogId = malGuideCatalogId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<MalfunctionCatalog> getChildCatalog() {
        return childCatalog;
    }

    public void setChildCatalog(List<MalfunctionCatalog> childCatalog) {
        this.childCatalog = childCatalog;
    }
}
