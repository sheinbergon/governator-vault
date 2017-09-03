package org.sheinbergon.governator.configuration.auth.impl;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;
import org.sheinbergon.governator.configuration.auth.AuthBackend;


// TODO - This class could be simplified using Lombok
public class AppRoleBackend extends AuthBackend {

    public static AppRoleBackend of(String roleName, String roleId, String secretId) {
        return new AppRoleBackend(roleName, roleId, secretId);
    }

    private final String roleName;
    private final String roleId;
    private final String secretId;

    private AppRoleBackend(String roleName, String roleId, String secretId) {
        this.roleName = roleName;
        this.roleId = roleId;
        this.secretId = secretId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getSecretId() {
        return secretId;
    }

    @Override
    public Type getType() {
        return Type.APP_ROLE;
    }

    @Override
    protected String doAuthenticate(Vault vault) throws VaultException {
        AuthResponse authResponse = vault.auth().loginByAppRole(getRoleName(), getRoleId(), getSecretId());
        return authResponse.getAuthClientToken();
    }
}