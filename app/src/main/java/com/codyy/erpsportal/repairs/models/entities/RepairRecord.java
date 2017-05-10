package com.codyy.erpsportal.repairs.models.entities;

/**
 * 报修记录
 * Created by gujiajia on 2017/3/21.
 */

public class RepairRecord {

    public RepairRecord(String malDetailId, String skey, String classRoomName, String reporter,
                        long createTime, String malDescription, String status) {
        this.malDetailId = malDetailId;
        this.skey = skey;
        this.classRoomName = classRoomName;
        this.reporter = reporter;
        this.createTime = createTime;
        this.malDescription = malDescription;
        this.status = status;
    }

    /**
     * 报修记录id
     */
    private String malDetailId;

    /**
     * 教室编号
     */
    private String skey;

    /**
     * 教室名称
     */
    private String classRoomName;

    /**
     * 报修人名
     */
    private String reporter;

    /**
     * 报修时间
     */
    private long createTime;

    private String malDescription;

    /**
     * 处理状态
     */
    private String status;

    public String getMalDetailId() {
        return malDetailId;
    }

    public void setMalDetailId(String malDetailId) {
        this.malDetailId = malDetailId;
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

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMalDescription() {
        return malDescription;
    }

    public void setMalDescription(String malDescription) {
        this.malDescription = malDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
