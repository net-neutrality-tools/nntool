package com.zafaco.speed.jni.exception;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class AndroidJniCppException extends Exception {
    public AndroidJniCppException() {
        super();
    }

    public AndroidJniCppException(String message) {
        super(message);
    }

    public AndroidJniCppException(String message, Throwable cause) {
        super(message, cause);
    }
}
