package com.codyy.erpsportal.commons.models.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.text.TextUtils;

import com.codyy.erpsportal.commons.utils.Cog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gujiajia on 2016/2/23.
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private final static String TAG = "DownloadTask";

    private Context mContext;

    private PowerManager.WakeLock mWakeLock;

    private DownloadListener mDownloadListener;

    public DownloadTask(Context context, DownloadListener downloadListener) {
        this.mContext = context;
        this.mDownloadListener = downloadListener;
    }

    /**
     * @param urls 第一位为url 第二位为resId
     * @return
     */
    @Override
    protected String doInBackground(String... urls) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String filePath = urls[1];
        int index = filePath.lastIndexOf('.');
//        String suffix = filePath.substring(index);
        String tempFileName = filePath.substring(0, index);
        File tempFile = new File(tempFileName);
        long startPos = 0;
        if (tempFile.exists()) {//如果缓存文件已经存在，获取大小，用于断点续传。
            startPos = tempFile.length();
        } else {
            String directory = createDir(filePath);
            Cog.d(TAG, "cacheDir=" + directory);
        }
        try {
            URL url = new URL(urls[0].replace(" ", "%20"));
            Cog.d(TAG, "url=", url, ", fileName=", filePath,", bytes=", startPos);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Range", "bytes=" + startPos + "-");
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != 206) {
//                    return "Server returned HTTP " + connection.getResponseCode()
//                            + " " + connection.getResponseMessage();
                Cog.d(TAG, "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
                return null;
            }
            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(tempFile, true);
            byte data[] = new byte[4096];
            long total = startPos;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            if (!tempFile.renameTo(new File(filePath))) {
                return null;
            }
        } catch (Exception e) {
            Cog.e(TAG, e.toString());
            return null;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                Cog.e(TAG, ignored.toString());
            }
            if (connection != null)
                connection.disconnect();
        }
        return filePath;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mDownloadListener != null) {
            mDownloadListener.onDownloadProgress(values[0]);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        if (mDownloadListener != null) {
            mDownloadListener.onStartDownload(this);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Cog.d(TAG, "onPostExecute result=" + result);
        mWakeLock.release();
//            mProgressDialog.dismiss();
        if (mDownloadListener != null) {
            mDownloadListener.onFinishDownload(result);
        }
    }

    /**
     * 创建缓存目录
     *
     * @return
     */
    private String createDir(String path) {
        if (TextUtils.isEmpty(path)) throw new IllegalArgumentException("Path is empty");
        String directory = path.substring(0, path.lastIndexOf('/'));
        File file = new File(directory);
        if (file.exists()) {
            if (!file.isDirectory() && file.delete()) {
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public interface DownloadListener{
        void onFinishDownload(String result);
        void onStartDownload(DownloadTask task);
        void onDownloadProgress(int progress);
    }
}
