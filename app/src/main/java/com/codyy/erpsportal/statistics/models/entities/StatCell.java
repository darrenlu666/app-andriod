package com.codyy.erpsportal.statistics.models.entities;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Created by gujiajia on 2016/6/12.
 */
public class StatCell {

    public final static int STATUS_INCREASE = 1;

    public final static int STATUS_DECREASE = -1;

    public final static int STATUS_NORMAL = 0;

    private String text;

    private int status;

    public StatCell(String text) {
        this.text = text;
    }

    public StatCell(String text, int status) {
        this.text = text;
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(@CellStatus int status) {
        this.status = status;
    }

    @IntDef({STATUS_INCREASE, STATUS_DECREASE, STATUS_NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @interface CellStatus{}
}
