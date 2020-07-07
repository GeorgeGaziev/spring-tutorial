package com.example.springtutorial;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;

@Configuration
public class MainConfig {

    @Bean
    public BasicDataSource dataSource() throws URISyntaxException {
//        URI dbUri = new URI(System.getenv("JAWSDB_URL"));
//        String username = dbUri.getUserInfo().split(":")[0];
//        String password = dbUri.getUserInfo().split(":")[1];

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(System.getenv("JAWSDB_URL"));
//        basicDataSource.setUsername(username);
//        basicDataSource.setPassword(password);

        return basicDataSource;
    }
}