package com.codyy.erpsportal.homework.interfaces;

/**
 * 音频录制接口，采用策略模式
 * Created by ldh on 2016/5/20.
 */
public interface RecordStrategy {
    /**
     * 在这里进行录音准备工作，重置录音文件名等
     */
    void ready();

    /**
     * 开始录音
     */
    void start();

    /**
     * 结束录音
     */
    void stop();

    /**
     * 释放对象
     */
    void release();
    /**
     * 删除音频文件
     */
    void deleteOldFiles();

    /**
     * 获取音频音量大小
     */
    int getAmplitude();

    /**
     * 返回音频文件完整路径
     * @return
     */
    String getFilePath();
}

