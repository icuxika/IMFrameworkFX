package com.icuxika.util;

public class SystemUtil {

    /**
     * 文件分隔符
     */
    public static String FILE_SEPARATOR = System.getProperty("file.separator");

    /**
     * 系统名称
     */
    public static String OS_NAME = System.getProperty("os.name");

    /**
     * 用户目录
     */
    public static String USER_HOME = System.getProperty("user.home");

    public static String OS_LOWER_WIN = "windows";

    public static String OS_LOWER_MAC = "mac";

    /**
     * 判断当前平台是否是 Windows 平台
     *
     * @return 是 否
     */
    public static boolean platformIsWindows() {
        return OS_NAME.toLowerCase().startsWith(OS_LOWER_WIN);
    }
}
