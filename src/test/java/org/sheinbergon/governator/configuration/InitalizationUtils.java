package org.sheinbergon.governator.configuration;

import com.bettercloud.vault.VaultConfig;
import com.google.inject.Injector;
import com.netflix.governator.guice.BootstrapModule;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.lifecycle.LifecycleManager;
import com.netflix.governator.lifecycle.LifecycleManagerArguments;
import it.org.sheinbergon.governator.configuration.subjects.TestModule;

import java.util.Map;

public class InitalizationUtils {

    private final static String VAULT_TOKEN = "1913f7f4-ff4b-4ac2-baf0-5e14655ad8b3";
    private final static String VAULT_URL = "http://127.0.0.1:8222";

    public static Injector lifeCycleInjector(String secretPath, Map<String, String> variableValues) throws Exception {
        Injector injector = LifecycleInjector.builder().
                withBootstrapModule(bootStrapModule(secretPath, variableValues)).
                withModules(new TestModule()).
                requiringExplicitBindings().
                build().
                createInjector();

        LifecycleManager manager = injector.getInstance(LifecycleManager.class);
        manager.start();
        return injector;
    }


    public static LifecycleManager lifceCycleManager(String secretPath, Map<String, String> variableValues) throws Exception {
        LifecycleManagerArguments arguments = new LifecycleManagerArguments();
        arguments.setConfigurationProvider(vaultProvider(VAULT_TOKEN, secretPath, variableValues));
        return new LifecycleManager(arguments);
    }

    private static BootstrapModule bootStrapModule(String secretPath, Map<String, String> variableValues) throws VaultConfigurationException {
        return new VaultConfigurationProviderBoostrapModule(
                secretPath, variableValues,
                vaultConfig(VAULT_TOKEN));
    }

    public static VaultConfigurationProvider vaultProvider(String token, String secretPath, Map<String, String> variableValues) throws VaultConfigurationException {
        return VaultConfigurationProvider.builder()
                .vaultConfig(vaultConfig(token))
                .secretPath(secretPath)
                .variableValues(variableValues)
                .build();
    }

    public static VaultConfig vaultConfig() throws VaultConfigurationException {
        return vaultConfig(VAULT_TOKEN);
    }

    private static VaultConfig vaultConfig(String token) throws VaultConfigurationException {
        return VaultConfigBuilder
                .start()
                .token(token)
                .address(VAULT_URL)
                .build();
    }

}
