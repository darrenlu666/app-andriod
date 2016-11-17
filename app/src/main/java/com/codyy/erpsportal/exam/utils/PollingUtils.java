package com.codyy.erpsportal.exam.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

/**
 * Created by eachann on 2016/3/11.
 */
public class PollingUtils {
    //开启轮询服务
    public static void startPollingService(Context context, int seconds, Class<?> cls, String action) {
        //获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        //包装需要执行Service的Intent
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //触发服务的起始时间
        long triggerAtTime = SystemClock.elapsedRealtime();

        int alarmType = AlarmManager.ELAPSED_REALTIME;
        final int FIFTEEN_SEC_MILLIS = 60000;

        // The AlarmManager, like most system services, isn't created by application code, but
        // requested from the system.

        // setRepeating takes a start delay and period between alarms as arguments.
        // The below code fires after 15 seconds, and repeats every 15 seconds.  This is very
        // useful for demonstration purposes, but horrendous for production.  Don't be that dev.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            manager.setExact(alarmType, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS, pendingIntent);
           /* manager.setInexactRepeating(alarmType, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS,
                    FIFTEEN_SEC_MILLIS, pendingIntent);*/
        else
            manager.set(alarmType, SystemClock.elapsedRealtime() + FIFTEEN_SEC_MILLIS,
                    pendingIntent);

    }

    //停止轮询服务
    public static void stopPollingService(Context context, Class<?> cls, String action) {
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //取消正在执行的服务
        manager.cancel(pendingIntent);
    }

}
