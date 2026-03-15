package com.cloud.baowang.agent.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowCustomizer(MeterRegistry registry) {
        return factory -> factory.addBuilderCustomizers(builder ->
                builder.setServerOption(UndertowOptions.ENABLE_STATISTICS, true)
        );
    }

}

