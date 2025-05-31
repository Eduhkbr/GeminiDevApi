package com.eduhkbr.gemini.DevApi.exception;

/**
 * Exceção customizada para erros de negócio.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
