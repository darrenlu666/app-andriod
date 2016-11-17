package com.codyy.bennu.sdk.impl;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.codyy.bennu.sdk.BNPublisher;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by ghost on 2015/12/23.
 */
public class BNAudioMixer {
    private static native boolean nativeAudioMixerClassInit();
    private native void nativeAudioMixerInit(int numOfMaxInput, int sampleRate, int channel, int format, int timePerFrame);
    private native void nativeAudioMixerStart();
    private native void nativeAudioMixerStop();
    private native void nativeAudioMixerRelease();
    private native int  nativeAudioMixerGetAudioId();
    private native void nativeAudioMixerAddNewAudioData(int audioId, short[] audioData);
    private native void nativeAudioMixerSetVolume(int audio_id, int audio_volume);
    private native void nativeAudioMixerRemoveAudioId(int audio_id);

    private BNPublisher mPublisher;
    private boolean mRunning = false;
    private static final String TAG = "audioMixer";
    // Audio
    protected static AudioTrack mAudioTrack;

    static {
        System.loadLibrary("SDL2");
        System.loadLibrary("audiomixer_jni");
        nativeAudioMixerClassInit();
    }

    public void init(int numOfMaxInput, int sampleRate, int channel, int format, int timePerFrame) {
        nativeAudioMixerInit(numOfMaxInput, sampleRate, channel, format, timePerFrame);
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void start() {
        nativeAudioMixerStart();
        mRunning = true;
    }

    public void stop() {
        nativeAudioMixerStop();
        mRunning = false;
    }

    public void release() {
        nativeAudioMixerRelease();
    }

    public int getAudioId() {
        return nativeAudioMixerGetAudioId();
    }

    public void removeAudioID(int id) {
        nativeAudioMixerRemoveAudioId(id);
    }

    public void setVolume(int audioId, int volume){
        nativeAudioMixerSetVolume(audioId, volume);
    }

    public void addNewAudioData(int audioId, short[] audioData) {
        nativeAudioMixerAddNewAudioData(audioId, audioData);
    }

    public void setPublisher(BNPublisher publisher) {
        Log.e(TAG,"set BNPublisher : "+publisher);
        mPublisher = publisher;
    }

    private void CFJ_MixeredAudioData(short[] audioData) {
        byte[] audioDataAsByte = byteMe(audioData);
        if (null != mPublisher && mPublisher.isRunning()) {
            // to turn shorts back to bytes
            //+
             //Log.i(TAG, "audioData size = " + audioDataAsByte.length);
            mPublisher.deliverAudioPlayData(audioDataAsByte, audioDataAsByte.length);
        } else {
            if(null == mPublisher){
//                Log.w(TAG, "mPublisher == null ");
            }else{
                Log.w(TAG, "mPublisher not on running state");
            }
        }
    }

    public static byte[] byteMe(short[] shorts) {
        byte[] out = new byte[shorts.length * 2]; // will drop last byte if odd number
        ByteBuffer.wrap(out).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);
        return out;
    }

    byte [] ShortToByte_Twiddle_Method(short [] input)
    {
        int short_index, byte_index;
        int iterations = input.length;

        byte [] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        for(/*NOP*/; short_index != iterations; /*NOP*/)
        {
            buffer[byte_index]     = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index; byte_index += 2;
        }

        return buffer;
    }

    // Audio

    /**
     * This method is called by SDL using JNI.
     */
    public static int audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        int channelConfig = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioFormat = is16Bit ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
        int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);

        Log.v(TAG, "SDL audio: wanted " + (isStereo ? "stereo" : "mono") + " " + (is16Bit ? "16-bit" : "8-bit") + " " + (sampleRate / 1000f) + "kHz, " + desiredFrames + " frames buffer");

        // Let the user pick a larger buffer if they really want -- but ye
        // gods they probably shouldn't, the minimums are horrifyingly high
        // latency already
        desiredFrames = Math.max(desiredFrames, (AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat) + frameSize - 1) / frameSize);

        if (mAudioTrack == null) {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                    channelConfig, audioFormat, desiredFrames * frameSize, AudioTrack.MODE_STREAM);

            // Instantiating AudioTrack can "succeed" without an exception and the track may still be invalid
            // Ref: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/AudioTrack.java
            // Ref: http://developer.android.com/reference/android/media/AudioTrack.html#getState()

            if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
                Log.e(TAG, "Failed during initialization of Audio Track");
                mAudioTrack = null;
                return -1;
            }

            mAudioTrack.play();
        }

        Log.v(TAG, "SDL audio: got " + ((mAudioTrack.getChannelCount() >= 2) ? "stereo" : "mono") + " " + ((mAudioTrack.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) ? "16-bit" : "8-bit") + " " + (mAudioTrack.getSampleRate() / 1000f) + "kHz, " + desiredFrames + " frames buffer");

        return 0;
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioWriteShortBuffer(short[] buffer) {
        for (int i = 0; i < buffer.length; ) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    // Nom nom
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioWriteByteBuffer(byte[] buffer) {
        for (int i = 0; i < buffer.length; ) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    // Nom nom
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    /**
     * This method is called by SDL using JNI.
     */
    public static void audioQuit() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack = null;
        }
    }

}
