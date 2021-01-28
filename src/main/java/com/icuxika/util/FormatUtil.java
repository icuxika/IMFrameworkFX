package com.icuxika.util;

import java.text.DecimalFormat;

/**
 * 格式化工具类
 */
public class FormatUtil {

    /**
     * 将文件长度转换文件大小单位展示
     *
     * @param size 文件字节长度
     * @return 格式化表示
     */
    public static String fileSize2String(long size) {
        double result;

        if (size < 1024) return size + "Byte";

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        result = (double) size;
        result /= 1024.0;
        if (result < 1024) return decimalFormat.format(result) + "KiB";
        size /= 1024;

        result = (double) size;
        result /= 1024.0;
        if (result < 1024) return decimalFormat.format(result) + "MiB";
        size /= 1024;

        result = (double) size;
        result /= 1024.0;
        if (result < 1024) return decimalFormat.format(result) + "GiB";
        size /= 1024;

        result = (double) size;
        result /= 1024.0;
        if (result < 1024) return decimalFormat.format(result) + "TiB";
        size /= 1024;

        result = (double) size;
        result /= 1024.0;
        if (result < 1024) return decimalFormat.format(result) + "PiB";
        size /= 1024;

        result = (double) size;
        result /= 1024.0;
        if (result < 1024) return decimalFormat.format(result) + "EiB";
        size /= 1024;

        result = (double) size;
        result /= 1024.0;
        if (result < 1024) return decimalFormat.format(result) + "ZiB";

        return "超级大";
    }
}
