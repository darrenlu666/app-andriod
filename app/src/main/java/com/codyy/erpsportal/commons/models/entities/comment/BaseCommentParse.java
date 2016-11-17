package com.codyy.erpsportal.commons.models.entities.comment;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by poe on 16-2-19.
 */
public class BaseCommentParse {

    private String message;
    private String result;
    private int total;//总评论数
    @SerializedName(value = "list",alternate = {"comments","data"})
    private List<BaseComment> commentList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<BaseComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<BaseComment> commentList) {
        this.commentList = commentList;
    }
}
