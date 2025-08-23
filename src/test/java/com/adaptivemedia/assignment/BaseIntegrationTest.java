package com.adaptivemedia.assignment;

import com.adaptivemedia.assignment.config.PostgresTestContainer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @BeforeAll
    static void beforeAll() {

        if (!postgres.isRunning()) {
            postgres.start();
        }

        System.out.println("=== TESTCONTAINERS DEBUG ===");
        System.out.println("Container running: " + postgres.isRunning());
        System.out.println("JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("Username: " + postgres.getUsername());
        System.out.println("Password: " + postgres.getPassword());
        System.out.println("============================");
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        System.out.println("=== Setting database properties ===");
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jooq.sql-dialect", () -> "POSTGRES");
    }
}