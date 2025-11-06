package com.chiho.archBI.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 这是一个通用返回类
 *
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    /**
     * 通用返回类的全参构造函数
     * @param code 响应码
     * @param data 返回数据
     * @param message 返回提示信息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
