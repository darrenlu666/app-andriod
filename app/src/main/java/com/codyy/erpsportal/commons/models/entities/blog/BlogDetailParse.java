package com.codyy.erpsportal.commons.models.entities.blog;

import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poe on 16-2-17.
 */
public class BlogDetailParse {
    private String message;
    private String result;
    private int total;
    @SerializedName("comments")
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
