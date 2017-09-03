package it.org.sheinbergon.governator.configuration.subjects;

import com.netflix.governator.annotations.Configuration;

public class StaticConfigObject {

    public static StaticConfigObject empty() {
        return new StaticConfigObject();
    }

    private StaticConfigObject() {
    }

    @Configuration(value = "vault.governator.test.text")
    private String text;
    @Configuration(value = "vault.governator.test.integer")
    private int integer;
    @Configuration(value = "vault.governator.test.boolean")
    private boolean bool;
    @Configuration(value = "vault.governator.test.decimal")
    private double decimal;

    public String getText() {
        return text;
    }

    public int getInteger() {
        return integer;
    }

    public boolean getBool() {
        return bool;
    }

    public double getDecimal() {
        return decimal;
    }
}
