package it.org.sheinbergon.governator.configuration.subjects;

import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(StaticConfigObject.class).toInstance(StaticConfigObject.empty());
    }

}
