package com.codyy.erpsportal.rethink.models.entities;

/**
 * Created by gujiajia on 2016/7/5.
 */
public class DeleteCommentEvent {

    private int position;

    public DeleteCommentEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
