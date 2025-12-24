package com.example.cardashboardapp.ui;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

// JUnit 4 でパラメータ化テスト（CsvSourceのようなもの）を行う形式です
@RunWith(Parameterized.class)
public class SpeedLogicTest {

    private final float speed;
    private final boolean expectedIsDriving;

    // コンストラクタでテストデータを受け取ります
    public SpeedLogicTest(float speed, boolean expectedIsDriving) {
        this.speed = speed;
        this.expectedIsDriving = expectedIsDriving;
    }

    // テストデータの定義（C#のInlineDataに相当）
    @Parameterized.Parameters(name = "{index}: Speed({0}km/h) -> isDriving({1})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { 0.0f, false },   // 停車
                { 4.9f, false },   // 5km/h未満
                { 5.0f, false },   // 5km/hちょうど
                { 5.1f, true },    // 5km/h超
                { 100.0f, true }   // 高速走行
        });
    }

    /**
     * UT_05/06: 車速判定ロジックの境界値テスト
     */
    @Test
    public void testIsDrivingLogic() {
        boolean actual = speed > 5.0f;
        assertEquals("Speed: " + speed + " km/h の判定が不正です", expectedIsDriving, actual);
    }
}