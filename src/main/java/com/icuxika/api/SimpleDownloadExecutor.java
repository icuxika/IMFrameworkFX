package com.icuxika.api;

import javafx.concurrent.Task;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.*;
import java.nio.file.Files;

/**
 * 一般文件下载器
 */
public class SimpleDownloadExecutor {

    /**
     * 文件远程地址
     */
    private final String url;

    /**
     * 文件下载存储路径
     */
    private final String path;

    /**
     * 下载请求回调
     */
    private final Callback callback;

    private final OkHttpClient client = new OkHttpClient();

    public SimpleDownloadExecutor(String url, String path, Callback callback) {
        this.url = url;
        this.path = path;
        this.callback = callback;
    }

    public void execute() {
        DownloadTask task = new DownloadTask();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        callback.onTaskCreated(task);
    }

    public class DownloadTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        long fileContentLength = responseBody.contentLength();
                        try (InputStream inputStream = responseBody.byteStream()) {
                            // TODO 检查path对应的文件是否存在
                            OutputStream outputStream = new FileOutputStream(path);

                            byte[] buffer = new byte[1024];
                            int sum = 0;
                            int len;
                            while ((len = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, len);
                                sum += len;
                                updateProgress(sum, fileContentLength);
                            }
                            outputStream.close();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            try {
                // 任务取消，删除创建的文件
                File file = new File(path);
                if (file.exists()) {
                    Files.delete(file.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void failed() {
            super.failed();
            try {
                // 任务失败，删除创建的文件
                File file = new File(path);
                if (file.exists()) {
                    Files.delete(file.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Callback {
        void onTaskCreated(DownloadTask task);
    }
}
