package org.doraemon.treasure.gear.exception;

public class GearCannotGetServiceException extends Exception {
    public GearCannotGetServiceException() {
    }

    public GearCannotGetServiceException(String message) {
        super(message);
    }

    public GearCannotGetServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public GearCannotGetServiceException(Throwable cause) {
        super(cause);
    }

    public GearCannotGetServiceException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
