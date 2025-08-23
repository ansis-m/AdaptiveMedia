package com.adaptivemedia.assignment.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer {

    private static PostgreSQLContainer<?> container;

    public static PostgreSQLContainer<?> getInstance() {
        if (container == null) {
            container = new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");
            container.start();
        }
        return container;
    }
}