package org.sheinbergon.governator.configuration.auth;

public class AuthBackendException extends Exception {
    public AuthBackendException(String message) {
        super(message);
    }

    public AuthBackendException(String message, Throwable cause) {
        super(message, cause);
    }
}
