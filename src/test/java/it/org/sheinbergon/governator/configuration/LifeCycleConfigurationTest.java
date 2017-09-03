package it.org.sheinbergon.governator.configuration;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.netflix.governator.lifecycle.LifecycleManager;
import com.netflix.governator.lifecycle.LifecycleMethods;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import it.org.sheinbergon.governator.configuration.subjects.StaticConfigObject;
import org.sheinbergon.governator.configuration.VaultConfigurationException;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.sheinbergon.governator.configuration.InitalizationUtils.*;

public class LifeCycleConfigurationTest {

    private final static String SECRET_PATH = "secret/governator/test";
    private final static Map<String, String> EMPTY_VARIABLE_VALUES = Maps.newHashMap();
    private final static Map<String, Object> STATIC_TEST_DATA_SET = new HashMap<String, Object>() {
        {
            put("vault.governator.test.text", "abcd");
            put("vault.governator.test.integer", 1);
            put("vault.governator.test.boolean", true);
            put("vault.governator.test.decimal", 0.5);
        }
    };

    private static Vault vault;

    @BeforeClass
    public static void init() throws VaultConfigurationException {
        vault = new Vault(vaultConfig());
    }

    @Before
    public void setup() throws VaultException {
        vault.logical().delete(SECRET_PATH);
    }

    @Test
    public void staticPropertyInjection() throws Exception {
        vault.logical().write(SECRET_PATH, STATIC_TEST_DATA_SET);
        StaticConfigObject subject = StaticConfigObject.empty();
        LifecycleManager manager = lifceCycleManager(SECRET_PATH, EMPTY_VARIABLE_VALUES);
        manager.add(subject, null, new LifecycleMethods(subject.getClass()));
        manager.start();
        assertTestDataSet(subject);
    }

    @Test
    public void moduleInjection() throws Exception {
        vault.logical().write(SECRET_PATH, STATIC_TEST_DATA_SET);
        Injector injector = lifeCycleInjector(SECRET_PATH, EMPTY_VARIABLE_VALUES);
        StaticConfigObject subject = injector.getInstance(StaticConfigObject.class);
        assertTestDataSet(subject);
    }


    private static void assertTestDataSet(StaticConfigObject subject) {
        assertThat(subject.getBool(), is(true));
        assertThat(subject.getInteger(), is(1));
        assertThat(subject.getDecimal(), is(0.5));
        assertThat(subject.getText(), is("abcd"));
    }
}
