package com.recipehub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * DatabaseConfig establishes the primary DataSource for RecipeHub.
 * Connects directly to MySQL 8.0 on localhost:3306 using application.properties.
 * No H2 fallback is performed during normal startup.
 */
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        System.out.println(">>> [DatabaseConfig] Connecting to MySQL Database: " + mysqlUrl);
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url(mysqlUrl)
                .username(mysqlUsername)
                .password(mysqlPassword)
                .build();
    }
}
