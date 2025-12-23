package com.example.cardashboardapp.template;

import android.car.Car;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.CarIcon;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.core.graphics.drawable.IconCompat;

public class MainScreen extends Screen {

    private boolean mIsDriving = false;

    public MainScreen(CarContext carContext) {
        super(carContext);
        setSpeedListener();
    }

    private void setSpeedListener() {
        try {
            Car car = Car.createCar(getCarContext());
            CarPropertyManager propertyManager = (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);

            if (propertyManager != null) {
                propertyManager.registerCallback(new CarPropertyManager.CarPropertyEventCallback() {
                    @Override
                    public void onChangeEvent(CarPropertyValue value) {
                        if (value.getPropertyId() == VehiclePropertyIds.PERF_VEHICLE_SPEED) {
                            float speed = (float) value.getValue();
                            boolean isDrivingNow = speed > 5.0f;

                            if (mIsDriving != isDrivingNow) {
                                mIsDriving = isDrivingNow;
                                // 画面を再描画する
                                invalidate();
                            }
                        }
                    }

                    @Override
                    public void onErrorEvent(int propId, int zone) {
                    }
                }, VehiclePropertyIds.PERF_VEHICLE_SPEED, CarPropertyManager.SENSOR_RATE_ONCHANGE);
            }
        } catch (Exception e) {
            // Car APIが使えない環境（通常スマホなど）でのエラー回避
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        // 【修正】走行中の場合は警告画面を表示
        if (mIsDriving) {
            return new MessageTemplate.Builder("安全のため、走行中は駐車場リストを表示できません。")
                    .setTitle("操作制限中")
                    .setHeaderAction(Action.APP_ICON)
                    .setIcon(new CarIcon.Builder(IconCompat.createWithResource(getCarContext(), android.R.drawable.ic_dialog_alert))
                            .setTint(CarColor.YELLOW)
                            .build())
                    .build();
        }

        // 停車中の場合は駐車場リストを表示
        Row row = new Row.Builder()
                .setTitle("近隣の駐車場 A")
                .addText("空車：残り 15台")
                .setImage(createStatusIcon(CarColor.BLUE))
                .build();

        Row rowFull = new Row.Builder()
                .setTitle("駅前コインパーキング")
                .addText("現在 満車です")
                .setImage(createStatusIcon(CarColor.RED))
                .build();

        ItemList itemList = new ItemList.Builder()
                .addItem(row)
                .addItem(rowFull) // 両方のアイテムを追加
                .build();

        return new ListTemplate.Builder()
                .setSingleList(itemList)
                .setTitle("駐車場検索結果")
                .setHeaderAction(Action.APP_ICON)
                .build();
    }

    private CarIcon createStatusIcon(CarColor color) {
        return new CarIcon.Builder(
                IconCompat.createWithResource(getCarContext(), android.R.drawable.presence_online))
                .setTint(color)
                .build();
    }
}