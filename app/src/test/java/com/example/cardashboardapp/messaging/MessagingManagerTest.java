package com.example.cardashboardapp.messaging;

import static org.junit.Assert.assertEquals;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowNotificationManager;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class MessagingManagerTest {
    private MessagingManager messagingManager;
    private Context context;

    /**
     * Androidのコンテキストを取得
     */
    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        messagingManager = new MessagingManager(context);
    }

    /**
     * UT_01: 走行中の通知アクション検証
     */
    @Test
    public void UT_01_showMockNotification_Driving_ShouldHaveOnlyOneAction() {
        // 走行中(true)として通知を発行
        messagingManager.showMockNotification("テスト太郎", "こんにちは", true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        List<Notification> notifications = org.robolectric.Shadows.shadowOf(nm).getAllNotifications();

        Notification notification = notifications.get(notifications.size() - 1);

        // 検証：アクション（ボタン）が1つであること
        assertEquals(1, notification.actions.length);
        // 検証：ボタンのタイトルが「了承」であること
        assertEquals("了承", notification.actions[0].title.toString());
    }

    /**
     * UT_02: 停車中の通知アクション検証
     */
    @Test
    public void UT_02_showMockNotification_Stopped_ShouldHaveTwoActions() {
        // 停車中(false)として通知を発行
        messagingManager.showMockNotification("Test User", "Hello", false);


        android.app.NotificationManager nm = (android.app.NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        java.util.List<Notification> notifications = org.robolectric.Shadows.shadowOf(nm).getAllNotifications();

        Notification notification = notifications.get(notifications.size() - 1);

        // 検証：停車中は「返信」「詳細を表示」の2つがあるはず
        assertEquals(2, notification.actions.length);
        assertEquals("返信", notification.actions[0].title.toString());
        assertEquals("詳細を表示", notification.actions[1].title.toString());
    }
}