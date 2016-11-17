package com.codyy.erpsportal.commons.models.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.codyy.erpsportal.commons.utils.Constants;

import java.lang.ref.WeakReference;

/**
 * 把账号与密码记录到SharedPreferences
 */
public class SavePasswordTask extends AsyncTask<String,Void,Void> {

    private WeakReference<Context> mContextRef;

    public SavePasswordTask(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length != 2) return null;
        Context context = mContextRef.get();
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_KEY_PASSWORD, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username", params[0]);
            editor.putString("password", params[1]);
            editor.commit();
        }
        return null;
    }
}
