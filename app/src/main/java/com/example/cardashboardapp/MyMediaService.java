package com.example.cardashboardapp;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

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
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";

    /**
     * MediaSessionの初期化
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // MediaSessionの初期化（再生・停止などの操作を受け付ける窓口）
        mediaSession = new MediaSessionCompat(this, "MyMediaService");
        setSessionToken(mediaSession.getSessionToken());

        // 操作を受け取るコールバックの設定
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
            }

            @Override
            public void onPause() {
                updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
            }
        });

        // 初期状態（停止）を設定
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
        mediaSession.release();
        super.onDestroy();
    }
}