package com.example.cardashboardapp.messaging;

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

    /**
     * 擬似的なメッセージ受信通知をシステムに発行します。
     * <p>
     * {@link NotificationCompat.MessagingStyle} を使用することで、
     * 車載器（Android Automotive OS）側で「読み上げ」や「音声返信」の
     * メニューが自動的に表示される形式で通知を構成します。
     * </p>
     *
     * @param sender メッセージの送信者名（例: "佐藤 太郎"）
     * @param message 受信したメッセージの本文内容
     */
    public void showMockNotification(String sender, String message, boolean isDriving) {
        Person user = new Person.Builder().setName(sender).build();

        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user)
                .addMessage(message, System.currentTimeMillis(), user);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setStyle(style)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (!isDriving) {
            builder.addAction(android.R.drawable.ic_menu_send, "返信", null);
            builder.addAction(android.R.drawable.ic_menu_agenda, "詳細を表示", null);
        } else {
            builder.addAction(android.R.drawable.ic_input_add, "了承", null);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(1, builder.build());
        }
    }
}