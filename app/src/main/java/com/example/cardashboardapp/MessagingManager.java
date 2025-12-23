package com.example.cardashboardapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;

/**
 * 車載向けメッセージ通知を管理するクラスです。
 */
public class MessagingManager {
    private static final String CHANNEL_ID = "car_msg_channel";
    private final Context context;

    /**
     * コンストラクタ
     * @param context　コンテキスト
     */
    public MessagingManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    /**
     * 通知チャンネルを作成します。
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "新着メッセージ",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void showMockNotification(String sender, String message) {
        Person user = new Person.Builder().setName(sender).build();

        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user)
                .addMessage(message, System.currentTimeMillis(), user);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setStyle(style)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, builder.build());
        }
    }
}