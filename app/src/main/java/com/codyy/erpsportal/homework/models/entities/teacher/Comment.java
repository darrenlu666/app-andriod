package com.codyy.erpsportal.homework.models.entities.teacher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *评论内容列表
 * Created by ldh on 2016/2/3.
 */
public class Comment {

    /**
     * commentsId :
     * commentsStr : 很好很好很好
     */

    private String commentsId;
    private String commentsStr;

    public void setCommentsId(String commentsId) {
        this.commentsId = commentsId;
    }

    public void setCommentsStr(String commentsStr) {
        this.commentsStr = commentsStr;
    }

    public String getCommentsId() {
        return commentsId;
    }

    public String getCommentsStr() {
        return commentsStr;
    }

    public static List<Comment> parseResponse(JSONObject response){
        List<Comment> list = new ArrayList<>();
        JSONArray commentArray = response.optJSONArray("list");
        for(int i = 0; i < commentArray.length();i++){
            Comment comment = new Comment();
            JSONObject commentObject = commentArray.optJSONObject(i);
            comment.setCommentsId(commentObject.isNull("commentsId")?"":commentObject.optString("commentsId"));
            comment.setCommentsStr(commentObject.isNull("commentStr")?"":commentObject.optString("commentStr"));
            list.add(comment);
        }
        return list;
    }
}
