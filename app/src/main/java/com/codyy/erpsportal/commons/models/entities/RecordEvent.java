package com.codyy.erpsportal.commons.models.entities;

/**
 * 录制动作
 * Created by gujiajia on 2016/9/27.
 */
public class RecordEvent {

    public static final int STATE_START = 0;

    public static final int STATE_PAUSE = 1;

    public static final int STATE_STOP = 2;

    /**
     * 状态
     */
    private int state;

    /**
     * 已录制秒数
     */
    private int second;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "RecordEvent state:" + state
                + ",second:" + second;
    }
}
