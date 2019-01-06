package com.juzicool.webwalker.core;

public class PromiseException extends Exception {
    public PromiseException() {
        super();
    }


    public PromiseException(String message) {
        super(message);
    }

    public PromiseException(String message, Throwable cause) {
        super(message, cause);
    }

    public PromiseException(Throwable cause) {
        super(cause);
    }
}
