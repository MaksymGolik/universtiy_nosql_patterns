package com.nosqlcourse.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;
public class DataSourceConfiguration {
    public static DataSource getDataSource(){
        return DataSourceBuilder
                .create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/test")
                .username("root")
                .password("root")
                .build();

    }

    public static String getMongoUri(){
        return "mongodb://localhost:27017";
    }
}
