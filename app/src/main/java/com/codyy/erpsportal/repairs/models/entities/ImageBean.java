package com.codyy.erpsportal.repairs.models.entities;

/**
 * 图片
 * Created by gujiajia on 2017/4/15.
 */

public class ImageBean {

    /**
     * baseResourceServerId :
     * imageName : 8598d1316d6747b8948100c5661c65b8_266173276e78472bb6ad922ce0fe46c4.png
     * imgPath : http://10.1.70.15:8080/ResourceServer/images/8598d1316d6747b8948100c5661c65b8_266173276e78472bb6ad922ce0fe46c4.png
     * malAppendId : acf474cdca1845a39ba9c5e4a8f73bc0
     * malDetailId :
     * malImageId : 4D1E0F7AF7406CB0E0530A32010ABC60
     * serverAddress : http://10.1.70.15:8080/ResourceServer
     * sort : 1
     */

    private String baseResourceServerId;
    private String imageName;
    private String imgPath;
    private String malAppendId;
    private String malDetailId;
    private String malImageId;
    private String serverAddress;
    private int sort;

    public String getBaseResourceServerId() {
        return baseResourceServerId;
    }

    public void setBaseResourceServerId(String baseResourceServerId) {
        this.baseResourceServerId = baseResourceServerId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getMalAppendId() {
        return malAppendId;
    }

    public void setMalAppendId(String malAppendId) {
        this.malAppendId = malAppendId;
    }

    public String getMalDetailId() {
        return malDetailId;
    }

    public void setMalDetailId(String malDetailId) {
        this.malDetailId = malDetailId;
    }

    public String getMalImageId() {
        return malImageId;
    }

    public void setMalImageId(String malImageId) {
        this.malImageId = malImageId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
