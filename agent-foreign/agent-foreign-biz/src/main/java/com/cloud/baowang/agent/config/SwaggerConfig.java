package com.cloud.baowang.agent.config;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;


@Configuration
public class SwaggerConfig {

   @Value("${springdoc.gateway.url}")
   private String gatewayUrl;

    @Bean
    public OpenAPI swaggerOpenApi() {
        return new OpenAPI()
                .info(new Info().title("海外包网平台[agent-foreign]微服务")
                        .description("agent-foreign微服务")
                        .version("v1.0.0"))
                .servers(Lists.newArrayList(new Server().url(gatewayUrl)))
                .externalDocs(new ExternalDocumentation()
                        .description("agent-foreign微服务API文档"));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi.setServers(
                List.of(new io.swagger.v3.oas.models.servers.Server().url("/agent"))
        );
    }

}
