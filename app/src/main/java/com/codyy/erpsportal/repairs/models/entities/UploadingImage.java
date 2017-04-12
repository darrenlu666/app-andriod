package com.codyy.erpsportal.repairs.models.entities;

/**
 * 上传的图片
 * Created by gujiajia on 2017/4/7.
 */

public class UploadingImage {

    /**
     * 初始状态码
     */
    public final static int STATUS_INIT = 0;

    /**
     * 上传中状态码
     */
    public final static int STATUS_UPLOADING = 1;

    /**
     * 上传完成状态码
     */
    public final static int STATUS_FINISHED = 2;

    /**
     * 上传出错状态码
     */
    public final static int STATUS_ERROR = 3;

    /**
     * 上传取消
     */
    public final static int STATUS_CANCEL = 4;

    /**
     * 图片位置
     */
    private String path;

    /**
     * 状态
     */
    private volatile int status;

    /**
     * 图片id，上传后资源服务器返回
     */
    private String id;

    /**
     * 文件名，上传后资源服务器返回
     */
    private String name;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
