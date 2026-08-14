package com.ds1.exception;

/**
 * 业务异常 — 携带 HTTP 状态码，由 {@link GlobalExceptionHandler} 统一转换为响应。
 * 相比直接抛 {@link RuntimeException}，能让「登录失败」返回 401、「资源不存在」返回 404，
 * 而不是一律 400。
 */
public class BusinessException extends RuntimeException {

    private final int status;

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
