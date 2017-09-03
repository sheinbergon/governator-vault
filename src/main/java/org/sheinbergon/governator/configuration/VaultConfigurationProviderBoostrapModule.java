package org.sheinbergon.governator.configuration;

import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.netflix.governator.guice.BootstrapBinder;
import com.netflix.governator.guice.BootstrapModule;

import java.util.Map;

// TODO - Support in-app authentication using an additional constructor overload
// TODO - Support periodic token renewals using an additional constructor overload
public class VaultConfigurationProviderBoostrapModule implements BootstrapModule {

    private final String secretPath;
    private final Map<String, String> variableValues;
    private final VaultConfig vaultConfig;

    public VaultConfigurationProviderBoostrapModule(String secretPath, Map<String, String> variableValues, VaultConfig clientConfig) {
        this.secretPath = secretPath;
        this.variableValues = variableValues;
        this.vaultConfig = clientConfig;
    }

    public VaultConfigurationProviderBoostrapModule(String secretPath, Map<String, String> variableValues) throws VaultConfigurationException{
        this(secretPath, variableValues, VaultConfigBuilder.start().build());
    }

    @Override
    public void configure(BootstrapBinder bootstrapBinder) {
        try {
            bootstrapBinder.bindConfigurationProvider().toInstance(
                    VaultConfigurationProvider.
                            builder().
                            vaultConfig(vaultConfig).
                            secretPath(secretPath).
                            variableValues(variableValues).
                            build()
            );
        } catch (VaultConfigurationException vex) {
            throw new IllegalStateException("Could not wire Vault configuration provider - ", vex);
        }
    }
}