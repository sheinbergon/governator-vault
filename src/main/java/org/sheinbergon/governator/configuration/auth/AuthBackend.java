package org.sheinbergon.governator.configuration.auth;


import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

import java.util.Optional;

public abstract class AuthBackend {

    public enum Type {
        APP_ROLE
    }

    public abstract Type getType();

    protected abstract String doAuthenticate(Vault vault) throws VaultException;

    public Optional<String> authenticate(VaultConfig config) throws AuthBackendException {
        try {
            Vault vault = new Vault(config);
            return Optional.ofNullable(doAuthenticate(vault));
        } catch (RuntimeException | VaultException x) {
            throw new AuthBackendException("Could not authenticate backend", x);
        }
    }
}
