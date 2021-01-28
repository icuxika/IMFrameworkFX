package com.icuxika.util;

import java.io.File;

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

    /**
     * 判断当前平台是否是 macOS 平台
     *
     * @return 是 否
     */
    public static boolean platformIsMac() {
        return OS_NAME.toLowerCase().startsWith(OS_LOWER_MAC);
    }

    /**
     * 生成缓存目录路径
     *
     * @return 路径
     */
    private static String getCacheDirectory() {
        StringBuilder stringBuilder = new StringBuilder();
        if (platformIsWindows()) {
            String appDataDir = System.getenv("LOCALAPPDATA");
            if (appDataDir != null) {
                stringBuilder.append(appDataDir);
            } else {
                stringBuilder.append(USER_HOME).append(FILE_SEPARATOR).append("AppData").append(FILE_SEPARATOR).append("Local");
            }
        } else {
            stringBuilder.append(USER_HOME);
        }
        stringBuilder.append(FILE_SEPARATOR);
        stringBuilder.append("IMFrameworkFX");
        stringBuilder.append(FILE_SEPARATOR);
        return stringBuilder.toString();
    }

    public static String getFileCacheDirectory() {
        return getCacheDirectory() + "file" + FILE_SEPARATOR;
    }

    public static String getImageCacheDirectory() {
        return getCacheDirectory() + "image" + FILE_SEPARATOR;
    }

    public static String getOtherCacheDirectory() {
        return getCacheDirectory() + "other" + FILE_SEPARATOR;
    }

    private static void createCacheDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println("创建缓存目录：" + dir);
            }
        }
    }

    static {
        createCacheDir(getFileCacheDirectory());
        createCacheDir(getImageCacheDirectory());
        createCacheDir(getOtherCacheDirectory());
    }
}
