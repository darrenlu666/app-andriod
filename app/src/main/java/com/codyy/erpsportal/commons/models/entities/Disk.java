package com.codyy.erpsportal.commons.models.entities;

/**
 * 用于监控磁盘的BEAN类
 */
public class Disk {

    @Override
    public String toString() {
        return "内存总容量====>" + memorySize + "  内存可用空间====>" + memoryAvail
                + "  SD卡总容量====>" + sdSize + "  SD卡剩余容量====>" + sdAvail;
    }

    private String memorySize;
    private String memoryAvail;
    private String sdSize;
    private String sdAvail;
    private String memoryUsed;
    private String sdUsed;

    public String getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(String memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public String getSdUsed() {
        return sdUsed;
    }

    public void setSdUsed(String sdUsed) {
        this.sdUsed = sdUsed;
    }

    public String getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    public String getMemoryAvail() {
        return memoryAvail;
    }

    public void setMemoryAvail(String memoryAvail) {
        this.memoryAvail = memoryAvail;
    }

    public String getSdSize() {
        return sdSize;
    }

    public void setSdSize(String sdSize) {
        this.sdSize = sdSize;
    }

    public String getSdAvail() {
        return sdAvail;
    }

    public void setSdAvail(String sdAvail) {
        this.sdAvail = sdAvail;
    }

}
