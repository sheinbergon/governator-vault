package org.sheinbergon.governator.configuration;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

// TODO - This class could be simplified using Lombok
public class VaultConfigBuilder {

    protected final VaultConfig vaultConfig;

    private VaultConfigBuilder() {
        this.vaultConfig = new VaultConfig();
    }

    public static VaultConfigBuilder start() {
        return new VaultConfigBuilder();
    }

    public VaultConfigBuilder address(String address) {
        this.vaultConfig.address(address);
        return this;
    }

    public VaultConfigBuilder token(String token) {
        this.vaultConfig.token(token);
        return this;
    }

    public VaultConfigBuilder openTimeout(Integer openTimeout) {
        this.vaultConfig.openTimeout(openTimeout);
        return this;
    }

    public VaultConfigBuilder readTimeout(Integer readTimeout) {
        this.vaultConfig.readTimeout(readTimeout);
        return this;
    }

    public VaultConfig build() throws VaultConfigurationException {
        try {
            return vaultConfig.build();
        } catch (VaultException | RuntimeException x) {
            throw new VaultConfigurationException(String.format("Could not build %s instance", VaultConfig.class.getSimpleName()), x);
        }
    }
}
