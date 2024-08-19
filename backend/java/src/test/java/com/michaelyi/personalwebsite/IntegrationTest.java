package com.michaelyi.personalwebsite;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.michaelyi.personalwebsite.util.HttpResponse;

@Testcontainers
public abstract class IntegrationTest {
    private static final int REDIS_PORT = 6379;
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    protected static final ObjectWriter WRITER = MAPPER.writer();

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36");

    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>("redis:6.2.14")
            .withExposedPorts(REDIS_PORT);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add(
                "spring.data.redis.port",
                () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));
    }

    protected String getError(MockHttpServletResponse servletResponse) throws Exception {
        String json = servletResponse.getContentAsString();
        HttpResponse res = MAPPER.readValue(json, HttpResponse.class);
        return res.getError();
    }
}
