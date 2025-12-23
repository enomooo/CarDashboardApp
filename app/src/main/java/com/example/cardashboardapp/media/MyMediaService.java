package com.example.cardashboardapp.media;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 車載器のメディアプレイヤーと通信するためのサービス
 */
public class MyMediaService extends MediaBrowserServiceCompat{

    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String TAG = "MyMediaService";

    /**
     * MediaSessionの初期化
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // MediaPlayerの初期化
        mediaPlayer = new MediaPlayer();

        // MediaSessionの初期化（再生・停止などの操作を受け付ける窓口）
        mediaSession = new MediaSessionCompat(this, "MyMediaService");
        setSessionToken(mediaSession.getSessionToken());

        // 「再生」という命令を自分に送るためのチケット(Intent)を作る
        Intent playIntent = new Intent(this, MyMediaService.class);
        playIntent.setAction("ACTION_PLAY");
        PendingIntent playPendingIntent = PendingIntent.getService(
                this, 0,playIntent, PendingIntent.FLAG_IMMUTABLE);

        // 「一時停止」という命令を自分に送るためのチケットを作る
        Intent pauseIntent = new Intent(this, MyMediaService.class);
        pauseIntent.setAction("ACTION_PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(
                this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        mediaSession.setMediaButtonReceiver(playPendingIntent);

        // 車載側からの操作を受け取るコールバックの設定
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                handlePlayMusic();
            }

            @Override
            public void onPause() {
                handlePauseMusic();
            }

            @Override
            public void onSkipToNext() {
                Log.d(TAG, "次へボタンが押されました");
            }
        });

        // 初期状態（停止）を設定
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
    }

    /**
     *実際の再生処理ロジック
     */
    private void handlePlayMusic() {
        Log.d(TAG, "再生を開始します");
        // 疑似のため音楽のロードは無し！！
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
    }

    /**
     * 実際の一時停止処理ロジック
     */
    private void handlePauseMusic() {
        Log.d(TAG, "一時停止します");

        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
    }

    /**
     * 外部アプリ（車載システム）からの接続要求を検証
     * @param clientPackageName 接続を試行しているパッケージ名
     * @param clientUid 接続を試行してUID
     * @param rootHints システムから渡される追加情報
     * @return 接続を許可ならBrowserRoot, 拒否はnull
     */
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    /**
     * 指定された親IDに紐づくコンテンツリスト（曲やフォルダ）を返却します。
     * @param parentID 取得対象の親ID
     * @param result コンテンツリストを渡すための非同期リザルト
     */
    @Override
    public void onLoadChildren(@NonNull String parentID, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        if (MY_MEDIA_ROOT_ID.equals(parentID)) {
            mediaItems.add(createMediaItem("001", "サンプル曲(Mock)", "アーティスト名"));
        }

        result.sendResult(mediaItems);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if ("ACTION_PLAY".equals(action)) {
                handlePlayMusic();
            } else if ("ACTION_PAUSE".equals(action)) {
                handlePauseMusic();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 再生アイテム（MediaItem）を生成します。
     * @param id アイテムの一意識別子
     * @param title 表示タイトル
     * @param artist アーティスト名
     * @return 生成されたMediaItemオブジェクト
     */
    private MediaBrowserCompat.MediaItem createMediaItem(String id, String title, String artist) {
        MediaDescriptionCompat desc = new MediaDescriptionCompat.Builder()
                .setMediaId(id)
                .setTitle(title)
                .setSubtitle(artist)
                .build();
        return new MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    /**
     * 現在の再生状態をシステムに通知し、車載UIの表示を更新します。
     * @param state PlaybackStateCompatで定義される現在の状態（STATE_PLAYING等）
     */
    private void updatePlaybackState(int state) {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PAUSE |
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                .setState(state, 0,1.0f);
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    /**
     * サービス破棄時の解放処理を行う。
     * MediaSessionのメモリ解放を含みます。
     */
    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaSession.release();
        super.onDestroy();
    }
}