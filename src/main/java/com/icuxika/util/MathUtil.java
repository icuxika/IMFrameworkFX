package com.icuxika.util;

/**
 * 数学工具
 */
public class MathUtil {

    /**
     * 区间限定值
     *
     * @param min   最小返回值
     * @param value 优先值
     * @param max   最大返回值
     * @param <T>   数值类型
     * @return 限定范围的值
     */
    public static <T extends Comparable<T>> T clamp(T min, T value, T max) {
        if (value.compareTo(min) < 0) {
            return min;
        } else {
            if (value.compareTo(max) > 0) {
                return max;
            } else {
                return value;
            }
        }
    }
}
