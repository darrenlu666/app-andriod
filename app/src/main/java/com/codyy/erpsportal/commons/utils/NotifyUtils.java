package com.codyy.erpsportal.commons.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.codyy.erpsportal.onlinemeetings.controllers.activities.OnlineMeetingActivity;
import com.codyy.erpsportal.commons.controllers.activities.SingleChatActivity;
import com.codyy.erpsportal.onlinemeetings.models.entities.ChatMessage;

import java.util.HashSet;
import java.util.List;

/**
 * Created by yangxinwu on 2015/8/27.
 */
public class NotifyUtils {
    private final static String TAG = "notify";
    private Ringtone ringtone = null;
    private Context appContext;
    private Vibrator vibrator;
    private AudioManager audioManager;
    private NotificationManager notificationManager = null;
    private long mLastNotifiyTime = 0L;
    private static int notifyID = 0525; // start notification id
    private static int foregroundNotifyID = 0555;
    private HashSet<String> fromUsers = new HashSet<String>();
    private int notificationNum = 0;
    private String packageName;



    public NotifyUtils() {
    }

    public NotifyUtils init(Context context) {
        appContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        packageName = appContext.getApplicationInfo().packageName;
        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);

        return this;
    }


    /**
     * 处理新收到的消息，然后发送通知
     * @param message
     */
    public synchronized void onNewMsg(ChatMessage message) {
        Cog.d(TAG, "xxxapp is running in backgroud");
        // 判断app是否在后台
        if (!isAppRunningForeground(appContext)) {
            Cog.d(TAG, "app is running in backgroud");
            sendNotification(message, false);
        } else {
            Cog.d(TAG, "app not is running in backgroud");
           // sendNotification(message, true);
        }
        viberateAndPlayTone(message);
    }


    /**
     * 发送通知栏提示
     *
     * @param messages
     * @param isForeground
     */
    protected void sendNotification(List<ChatMessage> messages, boolean isForeground) {
        for (ChatMessage message : messages) {
            if (!isForeground) {
                notificationNum++;
                fromUsers.add(message.getName());
            }
        }
        sendNotification(messages.get(messages.size() - 1), isForeground, false);
    }

    protected void sendNotification(ChatMessage message, boolean isForeground) {
        sendNotification(message, isForeground, true);
    }

    /**
     * 发送通知栏提示
     *
     * @param message
     */
    protected void sendNotification(ChatMessage message, boolean isForeground, boolean numIncrease) {
        String username = message.getName();
        try {
            Cog.d(TAG, "username "+username);
            String notifyText = "主人,"+username + "给您发了一条消息哦";
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                    .setSmallIcon(appContext.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);
            Intent msgIntent= null;
            if (message.getChatType() == ChatMessage.GROUP_CHAT) {
                 msgIntent = new Intent(appContext, OnlineMeetingActivity.class);
            }else {
                msgIntent = new Intent(appContext, SingleChatActivity.class);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (numIncrease) {
                if (!isForeground) {
                    notificationNum++;
                    fromUsers.add(username);
                }
            }
            mBuilder.setContentTitle(username);
            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(message.getMsg());
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();

            if (isForeground) {
                notificationManager.notify(foregroundNotifyID, notification);
                notificationManager.cancel(foregroundNotifyID);
            } else {
                notificationManager.notify(notifyID, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAppRunningForeground(Context var0) {
        ActivityManager var1 = (ActivityManager) var0.getSystemService(Context.ACTIVITY_SERVICE);
        List var2 = var1.getRunningTasks(1);
        return var0.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo) var2.get(0)).baseActivity.getPackageName());
    }


    /**
     * 手机震动和声音提示
     */
    public void viberateAndPlayTone(ChatMessage message) {

        if (System.currentTimeMillis() - mLastNotifiyTime < 1000) {
            return;
        }

        try {
            mLastNotifiyTime = System.currentTimeMillis();
            // 判断是否处于静音模式
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                Cog.e(TAG, "in slient mode now");
                return;
            }

            if (ringtone == null) {
                Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
                if (ringtone == null) {
                    Cog.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
                    return;
                }
            }

            if (!ringtone.isPlaying()) {
                String vendor = Build.MANUFACTURER;
                ringtone.play();
                if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                    Thread ctlThread = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                if (ringtone.isPlaying()) {
                                    ringtone.stop();
                                }
                            } catch (Exception e) {
                            }
                        }
                    };
                    ctlThread.run();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
