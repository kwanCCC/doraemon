package com.oneapm.redismq.client.exception;

/**
 * Null模式,for beautiful
 */
public class NullException extends RuntimeException {
    public NullException() {
        super();
    }

    public NullException(String message) {
        super(message);
    }

    public NullException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullException(Throwable cause) {
        super(cause);
    }

    protected NullException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
