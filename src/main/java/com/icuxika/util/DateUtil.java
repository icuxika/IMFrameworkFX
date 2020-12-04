package com.icuxika.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 时间工具
 */
public class DateUtil {

    public static String DEFAULT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认格式化器 2020-12-21 10:10:10
     */
    public static DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN);

    /**
     * 根据时间戳获取系统默认 LocalDateTime 对象
     *
     * @param timeMillis 时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(long timeMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), ZoneId.systemDefault());
    }

    /**
     * 根据时间戳和指定时区获取 LocalDateTime 对象
     *
     * @param timeMillis 时间戳
     * @param zoneId     时区
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(long timeMillis, ZoneId zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), zoneId);
    }

    /**
     * 根据时间戳和指定时区获取 LocalDateTime 对象
     *
     * @param timeMillis 时间戳
     * @param zoneOffset 时区
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(long timeMillis, ZoneOffset zoneOffset) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), zoneOffset);
    }

    /**
     * 根据字符串获取 LocalDateTime 对象 以 yyyy-MM-dd HH:mm:ss 格式解析
     *
     * @param dateTime 字符串 如 2020-12-21 10:10:10
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DEFAULT_TIME_FORMATTER);
    }

    /**
     * 根据Date对象获取 LocalDateTime 对象
     *
     * @param date date
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 根据Date对象和时区获取 LocalDateTime 对象
     *
     * @param date   date
     * @param zoneId 时区
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(Date date, ZoneId zoneId) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneId);
    }

    /**
     * 根据Date对象和时区获取 LocalDateTime 对象
     *
     * @param date       date
     * @param zoneOffset 时区
     * @return LocalDateTime
     */
    public static LocalDateTime getDateTime(Date date, ZoneOffset zoneOffset) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneOffset);
    }

    /**
     * 获取两个LocalDateTime对象之间的天数
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数
     */
    public static long getDaysBetween(LocalDateTime start, LocalDateTime end) {
        return start.until(end, ChronoUnit.DAYS);
    }

    /**
     * 获取两个时间戳之间的天数
     *
     * @param start 开始时间戳
     * @param end   结束时间戳
     * @return 天数 如 2020-01-03，2020-01-05 返回 2，即不包括结束日期
     */
    public static long getDaysBetween(long start, long end) {
        return getDaysBetween(getDateTime(start), getDateTime(end));
    }

    /**
     * 获取两个Date对象之间的天数
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 天数
     */
    public static long getDaysBetween(Date start, Date end) {
        return getDaysBetween(getDateTime(start), getDateTime(end));
    }

    /**
     * 获取两个时间戳之间的天数（包括结束日期）
     *
     * @param start 开始时间戳
     * @param end   结束时间戳
     * @return 天数 如 如 2020-01-03，2020-01-05 返回 3，即包括结束日期
     */
    public static long getDaysBetweenInclusively(long start, long end) {
        return getDaysBetween(start, end) + 1;
    }

    /**
     * 获取时间戳在当前周的序号
     *
     * @param timeMillis 时间戳
     * @return 当前周的第几天
     */
    public static int getDayOfWeek(long timeMillis) {
        return getDateTime(timeMillis).getDayOfWeek().getValue();
    }

    /**
     * 获取时间戳在当月的序号
     *
     * @param timeMillis 时间戳
     * @return 当前月的第几天
     */
    public static int getDayOfMonth(long timeMillis) {
        return getDateTime(timeMillis).getDayOfMonth();
    }
}
