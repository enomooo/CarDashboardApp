package com.example.cardashboardapp.template;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Screen;
import androidx.car.app.Session;
import androidx.car.app.validation.HostValidator;

/**
 * 車載器と通信し、テンプレートアプリのセッションを開始するサービスです。
 */
public class MyCarAppService extends CarAppService {

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        // 全ての接続元(Host)を許可します
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
    }

    @NonNull
    @Override
    public Session onCreateSession() {
        return new Session() {
            @NonNull
            @Override
            public Screen onCreateScreen(@NonNull Intent intent) {
                // ここで「画面」であるMainScreen を呼び出します
                return new MainScreen(getCarContext());
            }
        };

    }
}











