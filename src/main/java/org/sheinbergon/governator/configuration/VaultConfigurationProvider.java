package org.sheinbergon.governator.configuration;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.google.common.collect.Maps;
import com.netflix.governator.configuration.*;
import org.sheinbergon.governator.configuration.auth.AuthBackend;
import org.sheinbergon.governator.configuration.auth.AuthBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Configuration provider backed by Hashicorp Vault (https://www.vaultproject.io/)
 */
public class VaultConfigurationProvider extends AbstractObjectConfigurationProvider {

    private final static ScheduledExecutorService RENEWAL_TASK_SCHEDUELED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Logger log = LoggerFactory.getLogger(VaultConfigurationProvider.Builder.class);

        private Optional<AuthBackend> authBackend = Optional.empty();
        private Optional<VaultConfig> vaultConfig = Optional.empty();
        private Optional<Long> renewalInterval = Optional.empty();
        private Optional<String> secretPath = Optional.empty();
        private Optional<Map<String, String>> variableValues = Optional.empty();

        private Builder() {
        }

        public Builder variableValues(Map<String, String> variableValues) {
            this.variableValues = Optional.ofNullable(variableValues);
            return this;
        }

        public Builder secretPath(String secretPath) {
            this.secretPath = Optional.ofNullable(secretPath);
            //this.secretPath =
            return this;
        }

        public Builder renewalInterval(Long renewalInterval) {
            this.renewalInterval = Optional.ofNullable(renewalInterval);
            return this;
        }

        public Builder authBackend(AuthBackend authBackend) {
            this.authBackend = Optional.ofNullable(authBackend);
            return this;
        }

        public Builder vaultConfig(VaultConfig vaultConfig) {
            this.vaultConfig = Optional.ofNullable(vaultConfig);
            return this;
        }

        public VaultConfigurationProvider build() throws VaultConfigurationException {
            try {
                return vaultConfig.map(config -> {
                    // Acquire Token
                    Optional<String> token = Optional.ofNullable(authBackend.flatMap(backend -> {
                        try {
                            return backend.authenticate(config);
                        } catch (AuthBackendException vx) {
                            throw new RuntimeException("Vault exception caught while trying to authenticate");
                        }
                    }).orElse(config.getToken()));

                    // Verify input values
                    String _token = token.orElseThrow(() -> new IllegalArgumentException("Vault token was not provided/acquired"));
                    String _secretPath = secretPath.orElseThrow(() -> new IllegalArgumentException("Vault secret path was not provided"));
                    Map<String, String> _variableValues = variableValues.orElse(Maps.newHashMap());

                    // Construct the provider instance with null-safe values
                    final VaultConfigurationProvider provider = new VaultConfigurationProvider(
                            new Vault(config.token(_token)),
                            new ConfigurationKey(_secretPath, KeyParser.parse(_secretPath)),
                            _variableValues
                    );

                    // Launch the self renewal task
                    renewalInterval.ifPresent(interval -> {
                        RENEWAL_TASK_SCHEDUELED_EXECUTOR.scheduleAtFixedRate(() -> {
                            try {
                                provider.vault.auth().renewSelf(0L);
                            } catch (VaultException | RuntimeException | Error x) {
                                log.error("Couldn't renew vault authentication token", x);
                            }
                        }, interval, interval, TimeUnit.MILLISECONDS);
                    });
                    return provider;
                }).orElseThrow(() -> new VaultConfigurationException("No Vault configuration provided"));
            } catch (RuntimeException rx) {
                throw new VaultConfigurationException(String.format("Could not build %s", VaultConfigurationProvider.class.getSimpleName()), rx);
            }
        }
    }

    private final Vault vault;
    private final ConfigurationKey secretPath;
    private final Map<String, String> variableValues;

    private VaultConfigurationProvider(Vault client, ConfigurationKey secretPath, Map<String, String> variableValues) {
        this.vault = client;
        this.secretPath = secretPath;
        this.variableValues = variableValues;
    }

    @PreDestroy
    public void destroy() {
        RENEWAL_TASK_SCHEDUELED_EXECUTOR.shutdownNow();
    }

    @Override
    public boolean has(ConfigurationKey key) {
        Boolean keyExists;
        try {
            keyExists = vault.logical().read(secretPath.getKey(variableValues)).getData().containsKey(key.getKey(variableValues));
        } catch (VaultException vex) {
            keyExists = false;
        }
        return keyExists;
    }

    @Override
    public Property<Boolean> getBooleanProperty(ConfigurationKey key, Boolean defaultValue) {
        Boolean value;
        try {
            value = Boolean.parseBoolean(vault.logical().read(secretPath.getKey(variableValues)).getData().get(key.getKey(variableValues)));
        } catch (VaultException vex) {
            value = defaultValue;
        }
        return Property.from(value);
    }

    @Override
    public Property<Integer> getIntegerProperty(ConfigurationKey key, Integer defaultValue) {
        Integer value;
        try {
            value = Integer.parseInt(vault.logical().read(secretPath.getKey(variableValues)).getData().get(key.getKey(variableValues)));
        } catch (VaultException vex) {
            value = defaultValue;
        }
        return Property.from(value);
    }

    @Override
    public Property<Long> getLongProperty(ConfigurationKey key, Long defaultValue) {
        Long value;
        try {
            value = Long.parseLong(vault.logical().read(secretPath.getKey(variableValues)).getData().get(key.getKey(variableValues)));
        } catch (VaultException vex) {
            value = defaultValue;
        }
        return Property.from(value);
    }

    @Override
    public Property<Double> getDoubleProperty(ConfigurationKey key, Double defaultValue) {
        Double value;
        try {
            value = Double.parseDouble(vault.logical().read(secretPath.getKey(variableValues)).getData().get(key.getKey(variableValues)));
        } catch (VaultException vex) {
            value = defaultValue;
        }
        return Property.from(value);
    }

    @Override
    public Property<String> getStringProperty(ConfigurationKey key, String defaultValue) {
        String value;
        try {
            value = vault.logical().read(secretPath.getKey(variableValues)).getData().get(key.getKey(variableValues));
        } catch (VaultException vex) {
            value = defaultValue;
        }
        return Property.from(value);
    }

    @Override
    public Property<Date> getDateProperty(ConfigurationKey key, Date defaultValue) {
        Date value;
        try {
            value = new DateWithDefaultProperty(Property.from(vault.logical().read(secretPath.getKey(variableValues)).getData().get(key.getKey(variableValues))), defaultValue).get();
        } catch (VaultException vex) {
            value = defaultValue;
        }
        return Property.from(value);
    }
}
