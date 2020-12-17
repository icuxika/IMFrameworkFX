package com.icuxika.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.icuxika.api.exception.ResponseNotOKException;
import com.icuxika.api.exception.ResponseNullBodyException;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * API 请求回调
 *
 * @param <T> 请求返回结果类型
 */
public abstract class APICallback<T> {

    private final Gson gson = new Gson();

    /**
     * 请求进度监听器
     */
    private RequestProgressListener requestProgressListener = this::onProgress;

    /**
     * 设置自定义请求进度监听器
     *
     * @param requestProgressListener listener
     */
    public void setRequestProgressListener(RequestProgressListener requestProgressListener) {
        this.requestProgressListener = requestProgressListener;
    }

    public RequestProgressListener getRequestProgressListener() {
        return this.requestProgressListener;
    }

    /**
     * 请求成功
     *
     * @param data 请求返回结果
     */
    abstract void onSuccess(T data);

    /**
     * 请求进度
     *
     * @param workDone 已完成
     * @param max      总数
     */
    void onProgress(long workDone, long max) {
    }

    /**
     * 请求成功但是由业务错误
     *
     * @param code 后段接口自定义的状态码
     * @param msg  消息
     */
    void onFailure(int code, String msg) {
    }

    /**
     * 对请求响应进行处理时遇到了异常
     *
     * @param throwable 异常
     */
    void onException(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    /**
     * 处理请求响应
     *
     * @param response 请求响应
     */
    public void convertResponse(Response response) {
        // 假设 T 此时为 ArrayList<String>
        Type genericSuperclass = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();

        Type type = typeArguments[0];
        // type -> java.util.ArrayList<java.lang.String>
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("缺少泛型参数");
        }

        Type rawType = ((ParameterizedType) type).getRawType();
        // rawType -> java.util.ArrayList

        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        // typeArgument -> java.lang.String

        ResponseBody body = response.body();

        if (body == null) {
            // 返回体为null
            onException(new ResponseNullBodyException("请求返回体为空"));
        } else {
            // 判断响应状态吗是否正常
            if (response.code() >= 300 && response.code() < 600) {
                onException(new ResponseNotOKException(response.message(), response.code()));
            }

            // 对返回内容进行反序列化数据解析
            try {
                String jsonString = body.string();

                if (rawType != ApiData.class) {
                    // 所传入的参数类型不是ApiData，直接解析，此处也可以限制只能解析ApiData格式的数据
                    T data = gson.fromJson(jsonString, type);
                    onSuccess(data);
                } else {
                    if (typeArgument == Void.class) {
                        // 当后端返回ApiData<ArrayList<String>>，但是此处以ApiData<Void>接收时，如果不进行当前分支判断，实际上data属性依然会有数据
                        SimpleResponse simpleResponse = gson.fromJson(jsonString, SimpleResponse.class);
                        ApiData<Void> apiData = new ApiData<>();
                        apiData.setCode(simpleResponse.getCode());
                        apiData.setMsg(simpleResponse.getMsg());
                        onSuccess((T) apiData);
                    } else {
                        // 解析目标为ApiData
                        ApiData apiData = null;
                        try {
                            apiData = gson.fromJson(jsonString, type);
                        } catch (Exception e) {
                            // JSON 反序列化出错，尝试只获取code和msg信息
                            try {
                                apiData = gson.fromJson(jsonString, new TypeToken<ApiData<Object>>() {
                                }.getType());
                            } catch (Exception ee) {
                                // 获取失败
                                onException(ee);
                            }
                        }

                        if (apiData == null) {
                            // 数据解析失败
                            onException(new RuntimeException(""));
                        } else {
                            // 判断code数据
                            if (apiData.getCode() == 200) {
                                // 属于正常操作成功
                                onSuccess((T) apiData);
                            } else {
                                // 此时请求响应是成功的，但是后端接口返回的状态码代表有业务错误
                                onFailure(apiData.getCode(), apiData.getMsg());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                onException(e);
            }
        }
    }
}
