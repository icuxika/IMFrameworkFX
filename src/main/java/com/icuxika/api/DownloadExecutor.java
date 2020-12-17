package com.icuxika.api;

import javafx.concurrent.Task;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分块文件下载器
 */
public class DownloadExecutor {

    /**
     * 文件远程地址
     */
    private final String url;

    /**
     * 文件下载保存路径
     */
    private final String path;

    /**
     * 下载任务集合
     */
    private final List<DownloadTask> downloadTaskList = new ArrayList<>();

    /**
     * 并行进行的任务数量
     */
    private int taskNumber = 4;

    /**
     * 当前正在运行的任务数量
     */
    private final AtomicInteger runningTaskNumber = new AtomicInteger(0);

    /**
     * 任务创建回调
     */
    private final Callback callback;

    public DownloadExecutor(String url, String path, Callback callback) {
        this.url = url;
        this.path = path;
        this.callback = callback;
    }

    public void execute() {
        InitializationTask task = new InitializationTask();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        task.setOnScheduled(event -> callback.onTaskCreating());
        task.setOnSucceeded(event -> callback.onTaskCreated(downloadTaskList));
        task.setOnFailed(event -> callback.onTaskCreationFailed());
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    class InitializationTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        long fileContentLength = responseBody.contentLength();
                        File targetFile = new File(path);
                        if (fileContentLength == -1) {
                            RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
                            downloadTaskList.add(new DownloadTask(url, targetFile, 0, -1, file));
                        } else {
                            long currentPartSize = fileContentLength / taskNumber + 1;
                            RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
                            file.setLength(fileContentLength);
                            file.close();
                            for (int i = 0; i < taskNumber; i++) {
                                RandomAccessFile currentPartFile = new RandomAccessFile(targetFile, "rw");
                                // 计算每个子任务开始位置
                                long startPos = i * currentPartSize;
                                // 定位子任务的下载位置
                                currentPartFile.seek(startPos);
                                // 创建下载任务
                                DownloadTask task = new DownloadTask(url, targetFile, startPos, currentPartSize, currentPartFile);
                                downloadTaskList.add(task);
                                Thread thread = new Thread(task);
                                thread.setDaemon(true);
                                thread.start();
                            }
                        }
                    }
                }
            }

            return null;
        }
    }

    class DownloadTask extends Task<Void> {

        /**
         * 文件远程地址
         */
        private final String url;

        /**
         * 目标文件
         */
        private final File targetFile;

        /**
         * 子任务文件开始下载位置
         */
        private final long startPos;

        /**
         * 子任务文件下载开始位置
         */
        private final long currentPartSize;

        /**
         * 子任务关联文件
         */
        private final RandomAccessFile currentPartFile;

        public DownloadTask(String url, File targetFile, long startPos, long currentPartSize, RandomAccessFile currentPartFile) {
            this.url = url;
            this.targetFile = targetFile;
            this.startPos = startPos;
            this.currentPartSize = currentPartSize;
            this.currentPartFile = currentPartFile;
        }

        public void cancelTask() {
            if (isRunning()) {
                cancel();
                // 对于还未开始执行的任务，因为进度不定，所以更新为0，正在进行的任务保留进度显示
                if (getProgress() < 0) updateProgress(0, 1);
            }
        }

        @Override
        protected Void call() throws Exception {
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        InputStream inputStream = responseBody.byteStream();
                        // 跳过 startPos 个字节只下载自己负责的部分
                        long skip = inputStream.skip(startPos);
                        byte[] buffer = new byte[1024];
                        int sum = 0;
                        int len;
                        while (((len = inputStream.read(buffer)) != -1) && (currentPartSize <= 0 || sum < currentPartSize)) {
                            currentPartFile.write(buffer, 0, len);
                            sum += len;
                            updateProgress(sum, currentPartSize);
                        }
                        currentPartFile.close();
                        inputStream.close();
                    }
                }
            }

            return null;
        }

        @Override
        protected void scheduled() {
            super.scheduled();
            runningTaskNumber.getAndIncrement();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            runningTaskNumber.getAndDecrement();
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            try {
                currentPartFile.close();
                runningTaskNumber.getAndDecrement();
                if (runningTaskNumber.get() == 0) {
                    if (targetFile.exists()) {
                        Files.delete(targetFile.toPath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void failed() {
            super.failed();
            try {
                currentPartFile.close();
                runningTaskNumber.getAndDecrement();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    interface Callback {

        /**
         * 下载任务创建中
         */
        default void onTaskCreating() {
            //
        }

        /**
         * 下载任务创建完成
         *
         * @param downloadTaskList 代执行任务列表
         */
        void onTaskCreated(List<DownloadTask> downloadTaskList);

        /**
         * 任务创建失败
         */
        default void onTaskCreationFailed() {
            //
        }
    }
}
