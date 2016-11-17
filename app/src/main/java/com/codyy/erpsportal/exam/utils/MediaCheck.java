package com.codyy.erpsportal.exam.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lijian on 2016/4/13.
 */
public class MediaCheck {
    private static final String TAG = "MediaCheck";

    public boolean isUsable(String urlStr) {
        if(!urlStr.contains("http"))//说明是本地的
            return true;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlStr);
            InputStream is = url.openStream();
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.connect();
            Log.i(TAG, String.valueOf(urlConnection.getResponseCode()));
            if (urlConnection.getResponseCode() != 200) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "MediaCheck error", e);
            return false;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }
}
