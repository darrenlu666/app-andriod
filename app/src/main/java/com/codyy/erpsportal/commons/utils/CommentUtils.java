package com.codyy.erpsportal.commons.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.codyy.erpsportal.commons.models.entities.BaseTitleItemBar;
import com.codyy.erpsportal.commons.models.entities.comment.BaseComment;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 评论工具类
 * Created by poe on 16-6-16.
 */
public class CommentUtils {

    /**
     * 根据评论的id 返回在mData中的下一个位置 .
     * @param blogCommentId
     * @return
     */
    public static int getPos(String blogCommentId , List<BaseTitleItemBar> dataList) {
        int pos = -1 ;
        for(int i = 0 ; i < dataList.size() ; i++){
            if(dataList.get(i) instanceof BaseComment){
                BaseComment bc = (BaseComment) dataList.get(i);
                //find the position to insert .
                if(bc.getCommentId().equals(blogCommentId)){
                    pos = i+1 ;//下一个位置开始添加
                    if("更多".equals(bc.getCommentContent()) && pos >0 ){
                        pos -- ;
                        break;
                    }
                }
            }
        }
        return pos;
    }
    /**
     * 删除一级评论
     * @param blogCommentId
     */
    public static void removeFirstLevel(String blogCommentId ,List<BaseTitleItemBar> dataList) {
        List<BaseComment> deleteList = new ArrayList<>();
        for(BaseTitleItemBar blog : dataList){
            if(blog instanceof BaseComment){
                BaseComment bc = (BaseComment) blog;
                if(bc.getCommentId().equals(blogCommentId) || (bc.getParentCommentId()!=null && bc.getParentCommentId().equals(blogCommentId))){
                    deleteList.add(bc);
                }
            }
        }

        if(deleteList.size()>0){
            dataList.removeAll(deleteList);
        }
    }

    /**
     * 根据评论的id 获取二级评论的更多的列表.
     * @param blogCommentId
     * @return
     */
    public static int getSecondMorePos(String blogCommentId ,LinkedList<BaseTitleItemBar> data) {
        int pos = -1 ;
        for(int i = 0 ; i < data.size() ; i++){
            String id = "";
            //find the position to insert .
            if(data.get(i) instanceof BaseComment){
               id =  ((BaseComment) data.get(i)).getCommentId();
            }
            if(id.equals(getSecondMoreId(blogCommentId))){
                pos = i ;
                break;
            }
        }
        return pos;
    }

    /**
     * 制造一级评论的更多 id
     * @return
     */
    public static String getFirstMoreId(String blogId){
        return "m"+blogId ;
    }

    /**
     * 制造二级评论的更多 id
     * @param blogCommentId
     * @return
     */
    @NonNull
    public static String getSecondMoreId(String blogCommentId) {
        return "m"+blogCommentId;
    }

    /**
     * 返回二级评论列表的条数 .
     * @param parentBlogId
     * @return
     */
    public static int getReplyCount(String parentBlogId , LinkedList<BaseTitleItemBar> data){
        int count = 0 ;
        for(int i = 0 ; i < data.size() ; i++){
            if(data.get(i) instanceof BaseComment){
                BaseComment bc = (BaseComment) data.get(i);
                if(null != bc.getParentCommentId() &&bc.getParentCommentId().equals(parentBlogId)){
                    if(!"更多".equals(bc.getCommentContent()) ){
                        count++;
                    }
                }
            }
        }
        return  count ;
    }

    /**
     * 获取一级评论数
     * @return
     */
    public static int getFirstCommentCount(LinkedList<BaseTitleItemBar> data){
        int count = 0 ;
        for(int i = 0 ; i < data.size() ; i++){
            String parentId = null;
            String content = "";
            if(data.get(i) instanceof BaseComment){
                parentId = ((BaseComment)data.get(i)).getParentCommentId();
                content = ((BaseComment)data.get(i)).getCommentContent();
            }
            if(TextUtils.isEmpty(parentId)){
                if(!"更多".equals(content)){
                    count++;
                }
            }
        }
        return  count ;
    }
}
