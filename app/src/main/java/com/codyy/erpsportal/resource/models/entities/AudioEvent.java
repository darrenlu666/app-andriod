package com.codyy.erpsportal.resource.models.entities;

/**
 * Created by gujiajia on 2016/6/27.
 */
public class AudioEvent {

    private Audio audio;

    private int position;

    public AudioEvent(Audio audio, int position) {
        this.audio = audio;
        this.position = position;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
