package com.codyy.erpsportal.homework.widgets;

import android.media.MediaPlayer;
import android.os.Bundle;

/**
 * Created by codyy on 2016/6/20.
 */
public class WorkAnswerMediaPlayer {
    private static MediaPlayer mMediaPlayer;
    public static MediaPlayer newInstance() {
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }else{
            return mMediaPlayer;
        }
        return mMediaPlayer;
    }
}
