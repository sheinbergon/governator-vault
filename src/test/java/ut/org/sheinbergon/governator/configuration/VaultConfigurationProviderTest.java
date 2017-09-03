package ut.org.sheinbergon.governator.configuration;

import com.bettercloud.vault.VaultException;
import org.junit.Test;
import org.sheinbergon.governator.configuration.TestUtils;
import org.sheinbergon.governator.configuration.VaultConfigurationException;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;
import static org.sheinbergon.governator.configuration.InitalizationUtils.vaultProvider;

public class VaultConfigurationProviderTest {

    @Test(expected = VaultConfigurationException.class)
    public void noSecertProvided() throws VaultConfigurationException {
        vaultProvider(UUID.randomUUID().toString(), null, null);
    }

    @Test(expected = VaultConfigurationException.class)
    public void noTokenProvided() throws VaultConfigurationException {
        assumeThat(Boolean.getBoolean(TestUtils.TRAVIS_FLAG_ENV_VARIABLE), is(Boolean.TRUE));
        vaultProvider(null, "secret", null);
    }
}
