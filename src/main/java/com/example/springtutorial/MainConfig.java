package com.example.springtutorial;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.URISyntaxException;

@Configuration
public class MainConfig {

    @Bean
    public DataSource dataSource() throws URISyntaxException {

        String dbUrl = "jdbc:" + System.getenv("JAWSDB_URL");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dbUrl);

        return dataSource;
    }
}