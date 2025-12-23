package com.example.cardashboardapp.ui;

import android.car.Car;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardashboardapp.messaging.MessagingManager;
import com.example.cardashboardapp.R;

/**
 * メイン画面：車速の監視と各機能への遷移を担当します。
 */
public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private Button settingsButton;
    private Button sendMsgButton;

    private Car car;
    private CarPropertyManager carPropertyManager;
    private MessagingManager messagingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // コンポーネントの初期化
        statusText = findViewById(R.id.status_text);
        settingsButton = findViewById(R.id.settings_button);
        sendMsgButton = findViewById(R.id.send_msg_button);

        // MessagingManagerの初期化
        messagingManager = new MessagingManager(this);

        // Car APIが使える環境かどうかをチェック
        if (isCarApiAvailable()) {
            setupCarApi();
        } else {
            Log.d("CarApp", "Car API is not available. Using Mock mode.");
            startMockData();
        }

        // 既存の画面遷移ロジック
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // メッセージ送信ボタンのイベント
        sendMsgButton.setOnClickListener(v -> {
            messagingManager.showMockNotification("佐藤　太郎", "今どこにいますか？",true);
        });
    }

    /**
     * Car APIがシステムに存在するかチェックします
     * @return 存在する場合はtrue
     */
    private boolean isCarApiAvailable() {
        try {
            Class.forName("android.car.Car");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 車速監視のセットアップ
     */
    private void setupCarApi() {
        try {
            car = Car.createCar(this);
            carPropertyManager = (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);

            if (carPropertyManager != null) {
                // インラインでコールバックを定義することで、Car APIがない環境でのロードを防ぐ
                carPropertyManager.registerCallback(new CarPropertyManager.CarPropertyEventCallback() {
                    @Override
                    public void onChangeEvent(CarPropertyValue carPropertyValue) {
                        if (carPropertyValue.getPropertyId() == VehiclePropertyIds.PERF_VEHICLE_SPEED) {
                            float speed = (float) carPropertyValue.getValue();
                            runOnUiThread(() -> {
                                statusText.setText("実車速: " + String.format("%.1f", speed) + " km/h");
                                updateUI(speed > 5.0f);
                            });
                        }
                    }

                    @Override
                    public void onErrorEvent(int propId, int zone) {}
                }, VehiclePropertyIds.PERF_VEHICLE_SPEED, CarPropertyManager.SENSOR_RATE_ONCHANGE);
            }
        } catch (Exception e) {
            Log.e("CarApp", "Car API initialization failed", e);
            startMockData();
        }
    }

    private void updateUI(boolean isDriving) {
        if (isDriving) {
            statusText.setText("現在の状態：走行中");
            settingsButton.setEnabled(false);
            settingsButton.setAlpha(0.5f);

            messagingManager.showMockNotification("システム", "走行中のため操作を制限しています", true);
        } else {
            statusText.setText("現在の状態：走行中");
            settingsButton.setEnabled(false);
            settingsButton.setAlpha(0.5f);

            messagingManager.showMockNotification("佐藤　太郎", "今どこにいますか？", false);
        }
    }

    /**
     * 車載APIがない環境向けの疑似データ生成処理
     */
    private void startMockData() {
        new android.os.Handler().postDelayed(new Runnable() {
            float dummySpeed = 0;

            @Override
            public void run() {
                dummySpeed += 2.0f;
                if (dummySpeed > 20) dummySpeed = 0;

                statusText.setText("実車速(Mock): " + String.format("%.1f", dummySpeed) + "km/h");
                updateUI(dummySpeed > 5.0f);

                new android.os.Handler().postDelayed(this, 1000);
            }
        }, 1000);
    }
}