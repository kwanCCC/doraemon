package org.doraemon.treasure.gear.exception;

public class GearException extends Exception {
    public GearException() {
    }

    public GearException(String message) {
        super(message);
    }

    public GearException(String message, Throwable cause) {
        super(message, cause);
    }

    public GearException(Throwable cause) {
        super(cause);
    }

    public GearException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
