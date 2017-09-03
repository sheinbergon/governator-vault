package org.sheinbergon.governator.configuration;

public class VaultConfigurationException extends Exception {
    public VaultConfigurationException(String message) {
        super(message);
    }

    public VaultConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
