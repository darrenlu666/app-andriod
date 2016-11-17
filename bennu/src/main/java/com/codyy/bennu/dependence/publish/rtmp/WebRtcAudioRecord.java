package com.codyy.bennu.dependence.publish.rtmp;

import java.util.concurrent.locks.ReentrantLock;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

public class WebRtcAudioRecord 
{
    final String TAG = "WebRtcAudioRecord";
	private final ReentrantLock _recLock = new ReentrantLock();
    private AudioRecord _audioRecord = null;

    private byte[] _tempBufRec;
    private boolean _isRecording = false;
    private int _bufferedRecSamples = 0;
    
    private void doLog(String msg)
    {
        Log.d(TAG, msg);
    }

    private void doLogErr(String msg)
    {
        Log.e(TAG, msg);
    }

    public WebRtcAudioRecord() 
    {
        _tempBufRec = new byte[2 * 480];// Max 10 ms @ 48kHz
    }

    @SuppressWarnings("unused")
    public int InitRecording(int audioSource, int sampleRate) 
    {
        // get the minimum buffer size that can be used
        int minRecBufSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);

        // doLog("min rec buf size is " + minRecBufSize);

        // double size to be more safe
        int recBufSize = minRecBufSize * 2;
        // On average half of the samples have been recorded/buffered and the
        // recording interval is 1/100s.
        _bufferedRecSamples = sampleRate / 200;
        
        doLog("sampleRate=" + sampleRate + " minRecBufSize=" + minRecBufSize + " recBufSize=" + recBufSize + " _bufferedRecSamples=" + _bufferedRecSamples);

        // release the object
        if (_audioRecord != null) 
        {
            _audioRecord.release();
            _audioRecord = null;
        }

        try 
        {
            _audioRecord = new AudioRecord(
                            audioSource,
                            sampleRate,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            recBufSize);
        } 
        catch (Exception e) 
        {
            doLog(e.getMessage());
            return -1;
        }

        // check that the audioRecord is ready to be used
        if (_audioRecord.getState() != AudioRecord.STATE_INITIALIZED) 
        {
            // doLog("rec not initialized " + sampleRate);
            return -1;
        }

        // doLog("rec sample rate set to " + sampleRate);

        return _bufferedRecSamples;
    }

    @SuppressWarnings("unused")
    public int StartRecording() 
    {
        try 
        {
            _audioRecord.startRecording();
        } 
        catch (IllegalStateException e) 
        {
            e.printStackTrace();
            return -1;
        }

        _isRecording = true;
        return 0;
    }

    @SuppressWarnings("unused")
    public int StopRecording() 
    {
        _recLock.lock();
        try 
        {
            // only stop if we are recording
            if (_audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) 
            {
                try 
                {
                    _audioRecord.stop();
                } 
                catch (IllegalStateException e) 
                {
                    e.printStackTrace();
                    return -1;
                }
            }

            // release the object
            _audioRecord.release();
            _audioRecord = null;
        } 
        finally 
        {
            // Ensure we always unlock, both for success, exception or error
            // return.
            _recLock.unlock();
        }

        _isRecording = false;
        return 0;
    }

    @SuppressWarnings("unused")
    public int RecordAudio(int lengthInBytes, byte[] buffer) 
    {
        _recLock.lock();
        try 
        {
            if (_audioRecord == null) 
            {
                return -2; // We have probably closed down while waiting for rec lock
            }

            int readBytes = _audioRecord.read(_tempBufRec, 0, lengthInBytes);
            if (readBytes != lengthInBytes) 
            {
                return -1;
            }
            System.arraycopy(_tempBufRec, 0, buffer, 0, lengthInBytes);
        } 
        catch (Exception e) 
        {
            doLogErr("RecordAudio try failed: " + e.getMessage());
        } 
        finally 
        {
            _recLock.unlock();
        }

        return _bufferedRecSamples;
    }
}

