package com.codyy.erpsportal.repairs.models.entities;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.codyy.erpsportal.R;

import java.util.List;

/**
 * 报修详情
 * Created by gujiajia on 2017/3/24.
 */

public class RepairDetails {

    private String malCode;

    private String skey;

    private String classRoomName;

    private String malDescription;

    private String malCatalogName1;

    private String malCatalogName2;

    private String malCatalogName3;

    private String reporter;

    private String reporterContact;

    private long createTime;

    private String status;

    private String repairman;

    private List<String> imgsPath;

    public String getMalCode() {
        return malCode;
    }

    public void setMalCode(String malCode) {
        this.malCode = malCode;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getClassRoomName() {
        return classRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        this.classRoomName = classRoomName;
    }

    public String getMalDescription() {
        return malDescription;
    }

    public void setMalDescription(String malDescription) {
        this.malDescription = malDescription;
    }

    public String getCategories() {
        return (TextUtils.isEmpty(malCatalogName1)? "": malCatalogName1)
                + (TextUtils.isEmpty(malCatalogName2)? "": "-" + malCatalogName2)
                + (TextUtils.isEmpty(malCatalogName3)? "": "-" + malCatalogName3);
    }

    public String getMalCatalogName1() {
        return malCatalogName1;
    }

    public void setMalCatalogName1(String malCatalogName1) {
        this.malCatalogName1 = malCatalogName1;
    }

    public String getMalCatalogName2() {
        return malCatalogName2;
    }

    public void setMalCatalogName2(String malCatalogName2) {
        this.malCatalogName2 = malCatalogName2;
    }

    public String getMalCatalogName3() {
        return malCatalogName3;
    }

    public void setMalCatalogName3(String malCatalogName3) {
        this.malCatalogName3 = malCatalogName3;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReporterContact() {
        return reporterContact;
    }

    public void setReporterContact(String reporterContact) {
        this.reporterContact = reporterContact;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRepairman() {
        return repairman;
    }

    public void setRepairman(String repairman) {
        this.repairman = repairman;
    }

    public List<String> getImgsPath() {
        return imgsPath;
    }

    public void setImgsPath(List<String> imgsPath) {
        this.imgsPath = imgsPath;
    }

    public @StringRes int statusStr() {
        switch (status) {
            case StatusItem.STATUS_NEW:
                return R.string.status_await_handle;
            case StatusItem.STATUS_PROGRESS:
                return R.string.status_handling;
            case StatusItem.STATUS_DONE:
                return R.string.status_handled;
            case StatusItem.STATUS_VERIFIED:
                return R.string.status_accepted;
        }
        return android.R.string.unknownName;
    }
}
