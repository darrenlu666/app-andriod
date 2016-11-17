package com.codyy.erpsportal.resource.models.entities;

/**
 * Created by gujiajia on 2016/7/5.
 */
public class DeleteReplyEvent {

    private int position;

    public DeleteReplyEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
