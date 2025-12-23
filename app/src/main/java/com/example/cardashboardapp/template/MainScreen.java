package com.example.cardashboardapp.template;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;

/**
 * 車載器の画面に表示される具体的なコンテンツを定義するクラスです。
 */
public class MainScreen extends Screen {

    public MainScreen(CarContext carContext) {
        super(carContext);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        // リストの各行(Row) を作成
        Row row = new Row.Builder()
                .setTitle("近隣の駐車場 A")
                .addText("空車：残り　５台")
                .build();

        ItemList itemList = new ItemList.Builder()
                .addItem(row)
                .build();

                return new ListTemplate.Builder()
                        .setSingleList(itemList)
                        .setTitle("テンプレートアプリ画面")
                        .setHeaderAction(Action.APP_ICON)
                        .build();

    }
}
