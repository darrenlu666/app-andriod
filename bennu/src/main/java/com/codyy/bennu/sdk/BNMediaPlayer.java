package com.codyy.bennu.sdk;

import android.nfc.Tag;
import android.util.Log;
import android.view.Surface;
import android.util.Log;
import com.codyy.bennu.sdk.impl.BNAudioMixer;
import com.codyy.bennu.sdk.impl.BNClassroomInfo;
import com.codyy.bennu.sdk.impl.BNPlayerImpl;

public class BNMediaPlayer {
    private BNPlayerImpl mPlayback;

    public BNMediaPlayer() {
        mPlayback = new BNPlayerImpl();
    }

    /**
     * Instantiate a player with initializing.
     */
    public static BNMediaPlayer createPlayer() {
        BNMediaPlayer mediaPlayer = new BNMediaPlayer();
        mediaPlayer.init();

        return mediaPlayer;
    }

    /**
     * initialize the player.
     */
    public void init() {
        mPlayback.init();
        mPlayback.setCallback(this);
    }

    /**
     * Make sure you call this when you're done to free up any opened
     * component instance instead of relying on the garbage collector
     * to do this for you at some point in the future.
     */
    public void release() {
        mPlayback.release();
    }

    /**
     * Tell the publisher object to player, this function is just for AECM.
     * Don't call this function if you do not set echo cancellation.
     * [FIXME]It's not a good code architecture. It may confuse the user.
     */
    public void setPublisher(BNPublisher publisher) {
        Log.i("BNMediaPlayer","mplayback :"+mPlayback + " publisher:"+publisher);
        mPlayback.setPublisher(publisher);
    }

    /*Modify by sparktend 20151224.*/
    public void setAudioMixer(BNAudioMixer audioMixer) {mPlayback.setAudioMixer(audioMixer);}
    /*Modify end.*/

    /**
     * Not use any more.
     * Audio mixer with software.
     * Most of Android device support hardware audio mixer.
     * If the device you use not support hardware audio mixer, try to use it.
    */
    public void playWithAudioMix(BNClassroomInfo info) { mPlayback.playWithAudioMix(info);}

    /**
     * After successfully set the attributes, call {@code playWithChat}.
     * this function is for video conference.
     */
    public void playWithChat() {
        mPlayback.setPipelineType(BNPlayerImpl.CHAT_HARDWARE);
        mPlayback.play();
    }

    /**
     * After successfully set the attributes, call {@code play}.
     * this function is for VOD or playing local files.
     */
    public void play() {
        mPlayback.setPipelineType(BNPlayerImpl.LIVE_HARDWARE);
        mPlayback.play();
    }

    /**
     * call {@code pause} to pause the playback.
     * Support only when VOD and play local files.
     */
    public void pause() {
        mPlayback.pause();
    }

    /**
     * call {@code stop} to stop the playback.
     */
    public void stop() {
        mPlayback.stop();
    }

    /**
     * call {@code seekTo} to seek the position to specified location when playing on PLAYING or PAUSED state.
     * @param position  Specify a valid location to seek to.
     */
    public void seekTo(long position) {
        mPlayback.seekTo(position);
    }

    /*-------------------------------------------------------------------
    * -------------------------------------------------------------------
    * Some attribute setting and getting functions.
    * uri and surface setting are must, others are optional.
     */

    /**
     * call {@code setUri} to set playing uri.
     * It must set before playing.
     * @param uri  Specify a valid uri.
     */
    public void setUri(String uri) {
        mPlayback.setUri(uri);
    }

    public String getUri() {
        return mPlayback.getUri();
    }

    /**
     * call {@code setSurface} to set playing surface.
     * It must set before playing.
     * @param surface  Specify a valid surface.
     */
    public void setSurface(Surface surface) {
        mPlayback.setSurface(surface);
    }

    public Surface getSurface() {
        return mPlayback.getSurface();
    }

    /**
     * call {@code setDecodeType} to set the decode type.
     * It must set before playing.
     * @param isUseHardware  use hardware or software decoder.
     */
    public void setDecodeType(boolean isUseHardware) { mPlayback.setDecodeType(isUseHardware);}

    /*
    public long getPosition() {
        return mPlayback.getPosition();
    }
    */

    /**
     * call {@code setVolume} to set the audio volume.
     * It can set before playing or on playing.
     * @param volume  [0,100].
     */
    public void setVolume(int volume) {
        mPlayback.setVolume(volume);
    }

    public int getVolume() {
        return mPlayback.getVolume();
    }

    /**
     * call {@code setVolume} to set mute flag.
     * It can set before playing or on playing.
     * @param mute  mute flag.
     */
    public void setMute(boolean mute) {
        mPlayback.setMute(mute);
    }

    public boolean getMute() {
        return mPlayback.getMute();
    }

    /**
     * call {@code setForceAspectRatio} to set display aspect ratio.
     * It can set before playing.
     * @param b  true means display as original aspect ratio.
     *           false means display on full surface that you specified.
     */
    public void setForceAspectRatio(boolean b) {
        mPlayback.setForceAspectRatio(b);
    }

    public void setReceiveVideo(boolean bRvideo, Surface showSurface)
    {
        mPlayback.setReceiveVideo(bRvideo, showSurface);
    }

    public boolean getForceAspectRatio() {
        return mPlayback.getForceAspectRatio();
    }

    /**
     * call {@code setForceAspectRatio} to set display aspect ratio.
     * It can set before playing or on playing.
     * @param time  the time how long the player don't get the stream then call error callback to tell user.
     */
    public void setTimeOut(int time) {
        mPlayback.setTimeOut(time);
    }

    public int getTimeOut() {
        return mPlayback.getTimeOut();
    }


    public static interface PositionUpdatedListener {
        abstract void positionUpdated(BNMediaPlayer player, long position);
    }

    private PositionUpdatedListener positionUpdatedListener;
    /**
     * call {@code setPositionUpdatedListener} to set position updated listener.
     * It can set before playing.
     * @param listener  the PositionUpdatedListener listener that can listen the position updated when playing.
     *                  Only VOD or local files.
     */
    public void setPositionUpdatedListener(PositionUpdatedListener listener) {
        positionUpdatedListener = listener;
    }

    public void onPositionUpdated(long position) {
        if (positionUpdatedListener != null) {
            positionUpdatedListener.positionUpdated(this, position);
        }
    }

    public static interface DurationChangedListener {
        abstract void durationChanged(BNMediaPlayer player, long duration);
    }

    private DurationChangedListener durationChangedListener;
    /**
     * call {@code setPositionUpdatedListener} to set duration changed listener.
     * It can set before playing.
     * @param listener  the DurationChangedListener listener that can listen the duration changed when playing.
     *                  Only VOD or local files.
     */
    public void setDurationChangedListener(DurationChangedListener listener) {
        durationChangedListener = listener;
    }

    public void onDurationChanged(long duration) {
        if (durationChangedListener != null) {
            durationChangedListener.durationChanged(this, duration);
        }
    }

    private static final State[] stateMap = {State.STOPPED, State.READY, State.PAUSED, State.PLAYING};
    public enum State {
        STOPPED,
        READY,
        PAUSED,
        PLAYING
    }

    public static interface StateChangedListener {
        abstract void stateChanged(BNMediaPlayer player, State state);
    }

    private StateChangedListener stateChangedListener;
    /**
     * call {@code setStateChangedListener} to set state changed listener.
     * It can set before playing.
     * @param listener  the StateChangedListener listener that can listen the state changed after starting the playback.
     */
    public void setStateChangedListener(StateChangedListener listener) {
        stateChangedListener = listener;
    }

    public void onStateChanged(int stateIdx) {
        State state1 = stateMap[stateIdx];
        if (state1 == State.STOPPED) {
            mPlayback.playAfterStop();
        }
        if (stateChangedListener != null) {
            State state = stateMap[stateIdx];
            stateChangedListener.stateChanged(this, state);
        }
    }

    public static interface BufferingListener {
        abstract void buffering(BNMediaPlayer player, int percent);
    }

    private BufferingListener bufferingListener;
    public void setBufferingListener(BufferingListener listener) {
        bufferingListener = listener;
    }

    public void onBuffering(int percent) {
        if (bufferingListener != null) {
            bufferingListener.buffering(this, percent);
        }
    }

    public static interface EndOfStreamListener {
        abstract void endOfStream(BNMediaPlayer player);
    }

    private EndOfStreamListener endOfStreamListener;
    /**
     * call {@code setEndOfStreamListener} to set end of stream listener.
     * It can set before playing.
     * @param listener  the EndOfStreamListener listener that can listen the EOS event when the event happened.
     */
    public void setEndOfStreamListener(EndOfStreamListener listener) {
        endOfStreamListener = listener;
    }

    public void onEndOfStream() {
        if (endOfStreamListener != null) {
            endOfStreamListener.endOfStream(this);
        }
    }

    public static interface ErrorListener {
        abstract void error(BNMediaPlayer player, int errorCode, String errorMsg);
    }

    private ErrorListener errorListener;
    /**
     * call {@code setErrorListener} to set error listener.
     * It can set before playing.
     * @param listener  the ErrorListener listener that can listen the ERROR event when the event happened.
     */
    public void setErrorListener(ErrorListener listener) {
        errorListener = listener;
    }

    public void onError(int errorCode, String errorMsg) {
        if (errorListener != null) {
            errorListener.error(this, errorCode, errorMsg);
        }
    }

    public static interface VideoDimensionsChangedListener {
        abstract void videoDimensionsChanged(BNMediaPlayer player, int width, int height);
    }

    private VideoDimensionsChangedListener videoDimensionsChangedListener;
    /**
     * call {@code setVideoDimensionsChangedListener} to set video dimensions changed listener.
     * It can set before playing.
     * @param listener  the VideoDimensionsChangedListener listener that can listen the video dimensions changed after starting the playback.
     */
    public void setVideoDimensionsChangedListener(VideoDimensionsChangedListener listener) {
        videoDimensionsChangedListener = listener;
    }

    public void onVideoDimensionsChanged(int width, int height) {
        if (videoDimensionsChangedListener != null) {
            videoDimensionsChangedListener.videoDimensionsChanged(this, width, height);
        }
    }
}


