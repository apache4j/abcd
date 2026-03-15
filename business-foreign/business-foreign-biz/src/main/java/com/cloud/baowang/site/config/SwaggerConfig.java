package com.cloud.baowang.site.config;


import com.google.common.collect.Lists;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Value("${springdoc.gateway.url}")
    private String gatewayUrl;

    @Bean
    public OpenAPI swaggerOpenApi() {
        return new OpenAPI()
                .info(new Info().title("海外包网平台[business-foreign]微服务")
                        .description("business-foreign微服务")
                        .version("v1.0.0"))
                .servers(Lists.newArrayList(new Server().url(gatewayUrl)))
                .externalDocs(new ExternalDocumentation()
                        .description("business-foreign微服务API文档"));
    }

}
