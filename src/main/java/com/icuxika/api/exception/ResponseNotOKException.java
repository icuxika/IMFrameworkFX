package com.icuxika.api.exception;

/**
 * 请求返回体为空
 */
public class ResponseNotOKException extends RuntimeException {

    private Integer code;

    public ResponseNotOKException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
