package com.oneapm.redismq.client.exception;

public class ConsumerTimeOutException extends RuntimeException {

    public ConsumerTimeOutException() {
    }

    public ConsumerTimeOutException(String message) {
        super(message);
    }

    public ConsumerTimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConsumerTimeOutException(Throwable cause) {
        super(cause);
    }

    public void valueOfError() {
        
    }
}
