package at.alladin.nettest.qos.android.exception;

public class ClientNotYetRegisteredException extends RuntimeException {

    public ClientNotYetRegisteredException() {

    }

    public ClientNotYetRegisteredException(String message) {
        super(message);
    }
}
