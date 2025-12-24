package com.example.cardashboardapp.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.car.app.model.ItemList;
import androidx.car.app.model.ListTemplate;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.car.app.testing.TestCarContext;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public class MainScreenTest {

    private TestCarContext testCarContext;
    private MainScreen screen;

    @Before
    public void setup() {
        // CarAppLibrary専用のテスト用コンテキストを作成
        testCarContext = TestCarContext.createCarContext(ApplicationProvider.getApplicationContext());
        screen = new MainScreen(testCarContext);
    }

    /**
     * UT_03: 停車中のテンプレート検証
     */
    @Test
    public void UT_03_onGetTemplate_Stopped_ShouldReturnListTemplate() throws Exception {
        // 停車中(false)をセット
        setDrivingStatus(false);

        // テンプレートを取得
        Template template = screen.onGetTemplate();

        // 検証：停車中は「駐車場リスト(ListTemplate)」が返されること
        assertTrue("停車中はlistTemplateである必要があります", template instanceof ListTemplate);
    }

    @Test
    public void UT_04_onGetTemplate_Driving_ShouldReturnMessageTemplate() throws Exception {
        // 走行中(true)をセット
        setDrivingStatus(true);

        // テンプレートを取得
        Template template = screen.onGetTemplate();

        assertTrue("走行中はMessageTemplateである必要があります", template instanceof MessageTemplate);
    }

    @Test
    public void UT_07_MainScreen_駐車場リストのタイトルと項目数を検証() throws Exception {
        setDrivingStatus(false);
        ListTemplate template = (ListTemplate) screen.onGetTemplate();
        assertEquals("駐車場検索結果", template.getTitle().toString());

        ItemList itemList = template.getSingleList();
        assertNotNull(itemList);
        assertEquals(2, itemList.getItems().size());

        Row firstRow = (Row) itemList.getItems().get(0);
        assertTrue(firstRow.getTitle().toString().contains("近隣の駐車場 A"));
    }

    /**
     * テスト用にMainScreenのプライベート変数 mIsDrivingを書き換えるヘルパー
     */
    private void setDrivingStatus(boolean status) throws Exception {
        // フィールド名 mIsDrivingを取得
        java.lang.reflect.Field field = MainScreen.class.getDeclaredField("mIsDriving");
        field.setAccessible(true);

        field.set(this.screen, status);
    }

}







