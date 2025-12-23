package com.example.cardashboardapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardashboardapp.R;
import com.example.cardashboardapp.ui.adapter.SettingsAdapter;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private Switch safetyLockSwitch;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 設定画面用のレイアウトをセット
        setContentView(R.layout.activity_settings);

        // XMLにあるRecyclerViewを取得
        RecyclerView recyclerView = findViewById(R.id.settings_recycler_view);

        // 表示するデータの準備（車載メニューを想定）
        List<String> settingsList = Arrays.asList(
                "Bluetooth設定",
                "サウンド設定",
                "ディスプレイ輝度",
                "車両ステータス",
                "システム情報",
                "ドライビングアシスト",
                "Wi-Fi設定"
        );
        // アダプター（データの橋渡し役）を作成
        SettingsAdapter adapter = new SettingsAdapter(settingsList);

        //RecyclerViewにアダプターをセット
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 戻るボタンがないので、このメソッドで戻る
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}