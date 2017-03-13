package com.codyy.erpsportal.commons.controllers.fragments;

/**
 * Created by poe on 3/8/17.
 */

import android.view.View;
import android.view.ViewGroup;

import com.codyy.erpsportal.commons.controllers.viewholders.BaseRecyclerViewHolder;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 代理基本的评论类接口
 */
public interface BaseCommentDelegate<T> {
    /**
     * 获取一级评
     * @return
     */
    SimpleRequestDelegate<T> getParentDelegate();

    /**
     * 获取二级评论
     * @return
     */
    SimpleRequestDelegate<T> getChildrenDelegate();

    /**
     * 发送一级评论
     * @return
     */
    SimpleRequestDelegate<T> sendCommentDelegate();

    /**
     * 发送二级评论　
     * @return
     */
    SimpleRequestDelegate<T> sendReplyDelegate();
}
