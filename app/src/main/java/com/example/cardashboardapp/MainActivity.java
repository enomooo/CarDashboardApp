package com.example.cardashboardapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.car.Car;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 画面操作に使う部品
    private TextView statusText;
    private Switch driveSwitch;
    private Button settingsButton;

    // 速度データのやり取りに使う部品
    private Car car;
    private CarPropertyManager carPropertyManager;

    private int currentGear = 4;
    private float currentSpeed = 0.0f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // XMLの読み込み
        setContentView(R.layout.activity_main);

        // XMLのIDとJavaの変数を紐づけ
        statusText = findViewById(R.id.status_text);
        driveSwitch = findViewById(R.id.drive_switch);
        settingsButton = findViewById(R.id.settings_button);

        // 既存の画面遷移ロジック
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // 車速監視の準備
        setupCarApi();
    }

    /**
     * 車速監視のsetup
     */
    private void setupCarApi() {
        try {
            car = Car.createCar(this);
            carPropertyManager = (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);

            if (carPropertyManager != null) {
                // 車速の監視を開始
                carPropertyManager.registerCallback(speedCallback,
                        VehiclePropertyIds.PERF_VEHICLE_SPEED,
                        CarPropertyManager.SENSOR_RATE_ONCHANGE);

                carPropertyManager.registerCallback(speedCallback,
                        VehiclePropertyIds.GEAR_SELECTION,
                        CarPropertyManager.SENSOR_RATE_ONCHANGE);

            } else {
                startMockData();
            }
        } catch (Exception e) {
            Log.e("CarApp", "Car APIの初期化に失敗しました", e);
            startMockData();
        }
    }

    private final CarPropertyManager.CarPropertyEventCallback speedCallback =
            new CarPropertyManager.CarPropertyEventCallback() {
                @Override
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    float speed = (float) carPropertyValue.getValue();

                    runOnUiThread(() -> {
                        statusText.setText("実車速: " + String.format("%.1f", speed) + " km/h");

                        boolean isDriving = speed > 5.0f;
                        settingsButton.setEnabled(!isDriving);
                        settingsButton.setAlpha(isDriving ? 0.5f : 1.0f);
                    });
                }

                @Override
                public void onErrorEvent(int propId, int zone) {
                }
            };

    /**
     * 車両の走行状態に応じてUIの有効 / 無効を切り替えます
     *
     * @param isDriving true = 走行中, false = 停車中
     */
    private void updateUI(boolean isDriving) {
        if (isDriving) {
            statusText.setText("現在の状態：走行中");
            settingsButton.setEnabled(false);
            settingsButton.setAlpha(0.5f);
        } else {
            statusText.setText("現在の状態：停車中");
            settingsButton.setEnabled(true);
            settingsButton.setAlpha(1.0f);
        }
    }

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
