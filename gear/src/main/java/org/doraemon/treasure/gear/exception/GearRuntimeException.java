package org.doraemon.treasure.gear.exception;

public class GearRuntimeException extends RuntimeException {

    public GearRuntimeException() {
        super();
    }

    public GearRuntimeException(String message) {
        super(message);
    }

    public GearRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GearRuntimeException(Throwable cause) {
        super(cause);
    }

    protected GearRuntimeException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
