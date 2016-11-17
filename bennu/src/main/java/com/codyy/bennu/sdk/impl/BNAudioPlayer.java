package com.codyy.bennu.sdk.impl;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by ghost on 2015/12/25.
 */
public class BNAudioPlayer {
    private AudioTrack mAudioTrack;

    public BNAudioPlayer(int sampleRate, int channelConfig, int audioFormat) {
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        bufferSize = bufferSize / 5;
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
    }

    public void play() {
        mAudioTrack.play();
    }

    public void stop() {
        mAudioTrack.stop();
    }

    public void release() {
        mAudioTrack.release();
    }

    public void write(short[] audioData, int offsetInShorts, int sizeInShorts) {
        mAudioTrack.write(audioData, offsetInShorts, sizeInShorts);
    }

    public void write(byte[] audioData, int offsetInShorts, int sizeInShorts) {
        mAudioTrack.write(audioData, offsetInShorts, sizeInShorts);
    }

}
