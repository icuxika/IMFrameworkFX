package com.jfoenix;

import java.net.URL;

/**
 * 由此类所在路径决定相对路径基于/com/jfoenix
 */
public class JFoenixResource {

    public static URL load(String path) {
        return JFoenixResource.class.getResource(path);
    }

    private JFoenixResource() {
    }
}
