/*
package com.cloud.baowang.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DbConnectionChecker implements ApplicationRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment env;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("====== DB Connection Checker ======");

        try {
            // 打印 Nacos / Spring Boot 拿到的数据库配置
            String url = env.getProperty("spring.datasource.url");
            String username = env.getProperty("spring.datasource.username");
            String password = env.getProperty("spring.datasource.password");

            System.out.println("DB URL: " + url);
            System.out.println("DB User: " + username);
            System.out.println("DB Password: " + (password != null ? "******" : null));

            // 测试连接
            try (Connection conn = dataSource.getConnection()) {
                System.out.println("数据库连接成功！ Connection = " + conn);
            } catch (Exception ex) {
                System.err.println("数据库连接失败: " + ex.getMessage());
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("====== 检查结束 ======");
    }
}

*/
