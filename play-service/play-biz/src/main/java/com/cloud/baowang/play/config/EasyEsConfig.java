package com.cloud.baowang.play.config;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EsMapperScan("com.cloud.baowang.play.mapper")
public class EasyEsConfig {
}
