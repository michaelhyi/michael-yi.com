package com.michaelyi.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.michaelyi.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource("classpath:application-it.properties")
class AuthIT {
    private static final int REDIS_PORT = 6379;
    private static MySQLContainer<?> mysql = new MySQLContainer<>(
            "mysql:8.0.36"
    );
    private static GenericContainer<?> redis =
            new GenericContainer<>("redis:6.2.14")
                    .withExposedPorts(REDIS_PORT);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(
                redis.getMappedPort(REDIS_PORT)
        ));
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper mapper;
    private ObjectWriter writer;

    @BeforeAll
    static void beforeAll() {
        mysql.start();
        redis.start();
    }

    @BeforeEach
    void setUp() {
        writer = mapper.writer().withDefaultPrettyPrinter();
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void afterAll() {
        mysql.stop();
        redis.stop();
    }

    @Test
    void login() throws Exception {
        LoginRequest req = new LoginRequest("unauthorized password");

        String error = mvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResolvedException()
                .getMessage();

        assertEquals("Unauthorized.", error);

        req = new LoginRequest("authorized password");
        String res = mvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertFalse(jwtService.isTokenExpired(res));
        assertEquals("admin@michael-yi.com", jwtService.extractUsername(res));
    }

    @Test
    void validateToken() throws Exception {
        String unauthorizedToken = generateUnauthorizedToken();
        String error = mvc.perform(get("/v1/auth/validate-token")
                        .header(
                                "Authorization",
                                String.format("Bearer %s", unauthorizedToken)
                        )
                        .servletPath("/v1/auth/validate-token"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResolvedException()
                .getMessage();

        assertEquals("Unauthorized.", error);

        final String expiredToken = "eyJhbGciOiJIUzI1NiJ9"
                + ".eyJzdWIiOiJ0ZXN0QG1haWwuY29tIiwiaWF0IjoxNzA5MzI0OTUxLCJleHA"
                + "iOjE3MDkzMjQ5NTF9"
                + ".0kgPiP5MELw6Pq6i9tJMXDxDy7n4Eu-LprqHOD4O2QM";

        error = mvc.perform(get("/v1/auth/validate-token")
                        .header(
                                "Authorization",
                                String.format("Bearer %s", expiredToken)
                        )
                        .servletPath("/v1/auth/validate-token"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResolvedException()
                .getMessage();

        assertEquals("Unauthorized.", error);

        LoginRequest req = new LoginRequest("authorized password");
        String token = mvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writer.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mvc.perform(get("/v1/auth/validate-token")
                        .header("Authorization",
                                String.format("Bearer %s", token)))
                .andExpect(status().isOk());
    }

    private String generateUnauthorizedToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        byte[] keyBytes = Decoders
                .BASE64
                .decode(
                        "fakesigninkeyfakesigninkeyfakesigninkeyfakesigninkey"
                );

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject("unauthorized@michael-yi.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(
                        System.currentTimeMillis() + 1L))
                .signWith(
                        Keys.hmacShaKeyFor(keyBytes),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }
}
