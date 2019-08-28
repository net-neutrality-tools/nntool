package at.alladin.nntool.client.v2.task.service;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public interface MkitService extends Callable<MkitService.MkitResult> {

    enum MkitTestEnum {
        MKIT_NDT,
        MKIT_HTTP_INVALID_REQUEST_LINE,
        MKIT_HTTP_HEADER_FIELD_MANIPULATION,
        MKIT_MULTI_NDT,
        MKIT_DNS_INJECTION,
        MKIT_TCP_CONNECT,
        MKIT_MEEK_FRONTEND_REQUEST,
        MKIT_DASH,
        MKIT_CAPTIVE_PORTAL,
        MKIT_FACEBOOK_MESSENGER,
        MKIT_TELEGRAM_MESSENGER,
        MKIT_WEB_CONNECTIVITY,
        MKIT_WHATSAPP_MESSENGER
    }

    /**
     * Exception thrown if the requested NdtTestEnum is NOT supported by the underlying impl
     */
    final class UnsupportedMkitTestException extends IOException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public UnsupportedMkitTestException(String msg) {
            super(msg);
        }
    }

    interface MkitResult {
        /**
         *
         * @return a string representation of the result (which is valid json)
         */
        String toString();

        /**
         *
         * @return the result as JSONObject (for easy manipulating)
         */
        JSONObject toJson();
    }

    /**
     * NEEDS to be called before the call() method is called, otherwise call will always return null
     * Set the test to be executed (using one of the available enums)
     * @param testToExecute
     * @throws UnsupportedMkitTestException if the ndtTestEnum is not supported by the NdtServiceImpl
     */
    void setTestToExecute (final MkitTestEnum testToExecute) throws UnsupportedMkitTestException;

    /**
     * Set possible input for the given NDT test
     * setTestToExecute needs be called before addInput
     * @param input, may be null if no input is required
     */
    void addInput(final String input);

    /**
     * Set flags for the given NDT test
     * setTestToExecute needs be called before addFlags
     * Syntax:
     * 		FLAGNAME[ VALUE]
     * 	where the " VALUE" is optional (if not provided, true is assumed)
     * 	the whitespace is only there, if a value is actually provided
     * @param flags, may be null if no flags are requested
     */
    void addFlags(final String flags);

    /**
     *
     * @return the current progress of the NDT tests in percent
     */
    float getProgress ();

    /**
     * Cancel the currently ongoing measurement
     * (functionality of cancel depends on the possibilities of the underlying implementation)
     */
    void cancel ();

}
