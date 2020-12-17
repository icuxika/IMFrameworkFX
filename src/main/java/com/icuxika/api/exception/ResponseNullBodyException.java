package com.icuxika.api.exception;

/**
 * 请求返回体为空
 */
public class ResponseNullBodyException extends RuntimeException {

    public ResponseNullBodyException(String message) {
        super(message);
    }
}
