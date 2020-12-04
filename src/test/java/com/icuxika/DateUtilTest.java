package com.icuxika;

import com.icuxika.util.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@DisplayName("日期工具类测试")
public class DateUtilTest {

    @Test
    @DisplayName("日期间隔天数测试")
    public void test() {
        LocalDateTime start = LocalDateTime.of(2020, 12, 11, 10, 10);
        LocalDateTime end = LocalDateTime.of(2020, 12, 13, 10, 10);
        long startSecond = start.toEpochSecond(ZoneOffset.of("+8"));
        long endSecond = end.toEpochSecond(ZoneOffset.of("+8"));
        Assertions.assertEquals(2, DateUtil.getDaysBetween(startSecond * 1000, endSecond * 1000));

        start = DateUtil.getDateTime("2020-12-21 10:10:10");
        end = DateUtil.getDateTime("2020-12-23 10:10:10");
        Assertions.assertEquals(2, DateUtil.getDaysBetween(start, end));

        Assertions.assertEquals(19, DateUtil.getDaysBetween(DateUtil.getDateTime(new Date()), end));

        System.out.println(DateUtil.getDateTime("2020-12-21 10:10:10"));
    }
}
