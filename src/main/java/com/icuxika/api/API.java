package com.icuxika.api;

import com.google.gson.Gson;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

public class API {

    private static final Logger logger = LogManager.getLogger(API.class.getName());

    private static final OkHttpClient client = new OkHttpClient();

    private static final Gson gson = new Gson();

    private static final String PARAMETER_DATA_KEY = "data";

    /**
     * 后段接收文件时，包括单个文件、文件数组和文件列表的参数都应与此一致
     */
    private static final String PARAMETER_FILE_KEY = "file";

    public static <P, R> void post(boolean syncExecute, String url, P data, List<File> fileList, RequestProgressListener listener, APICallback<R> callback) {
        // 覆盖默认进度监听器
        callback.setRequestProgressListener(listener);
        post(syncExecute, url, data, fileList, callback);
    }

    public static <P, R> void post(boolean syncExecute, String url, APICallback<R> callback) {
        post(syncExecute, url, null, callback);
    }

    public static <P, R> void post(boolean syncExecute, String url, P data, APICallback<R> callback) {
        post(syncExecute, url, data, null, callback);
    }

    public static <P, R> void post(boolean syncExecute, String url, P data, List<File> fileList, APICallback<R> callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().addFormDataPart(PARAMETER_DATA_KEY, gson.toJson(data));
        // 不管是单个还是多个文件，都与后段通过PARAMETER_FILE_KEY来传递与接收
        if (fileList != null)
            fileList.forEach(file -> builder.addFormDataPart(PARAMETER_FILE_KEY, file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream"))));
        // multipart/form-data 对于此种形式，SpringBoot想要同时接收JSON数据与文件时，需要自定义请求参数处理器来对此进行处理
        builder.setType(MultipartBody.FORM);
        MultipartBody multipartBody = builder.build();
        ObservableRequestBody observableRequestBody = new ObservableRequestBody(multipartBody, callback.getRequestProgressListener());
        Request request = new Request.Builder()
                .url(url)
                .post(observableRequestBody)
                .build();

        if (syncExecute) {
            // 同步请求
            try (Response response = client.newCall(request).execute()) {
                callback.convertResponse(response);
            } catch (IOException e) {
                if (e instanceof ConnectException) {
                    logger.error("网络异常");
                }
//                callback.onException(e);
            }
        } else {
            // 异步请求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (e instanceof ConnectException) {
                        logger.error("网络异常");
                    }
//                    callback.onException(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    callback.convertResponse(response);
                    response.close();
                }
            });
        }
    }

    /**
     * 测试
     */
    public static void testPost() {
        API.post(false, "http://localhost:8080/test/test", new APICallback<ApiData<Void>>() {
            @Override
            void onSuccess(ApiData<Void> data) {
                System.out.println(data);
            }

            @Override
            void onProgress(long workDone, long max) {
                super.onProgress(workDone, max);
                System.out.println(((double) workDone) / ((double) max));
            }

            @Override
            void onException(Throwable throwable) {
                super.onException(throwable);
                throwable.printStackTrace();
            }

            @Override
            void onFailure(int code, String msg) {
                super.onFailure(code, msg);
                System.out.println("code: " + code + ", msg: " + msg);
            }
        });
    }

    public static void testSimpleDownload() {
        SimpleDownloadExecutor executor = new SimpleDownloadExecutor("https://cdn.ey118.com/client/pc/feiju_309.exe", "/Users/icuxika/Downloads/install.exe", new SimpleDownloadExecutor.Callback() {
            @Override
            public void onTaskCreated(SimpleDownloadExecutor.DownloadTask task) {
                task.progressProperty().addListener((observable, oldValue, newValue) -> {
                    System.out.println(newValue);
                });
            }
        });
        executor.execute();
    }

    public static void testDownload() {
        DownloadExecutor executor = new DownloadExecutor("https://cdn.ey118.com/client/pc/feiju_309.exe", "/Users/icuxika/Downloads/install.exe", new DownloadExecutor.Callback() {
            @Override
            public void onTaskCreated(List<DownloadExecutor.DownloadTask> downloadTaskList) {
                downloadTaskList.forEach(downloadTask -> {
                    downloadTask.progressProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue.doubleValue() == 1.0) {
                            System.out.println("one task finished");
                        }
                    });
                });
            }
        });
        executor.execute();
    }

}
