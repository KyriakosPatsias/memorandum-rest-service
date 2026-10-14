package com.kyriakospatsias;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles(value = "test")
@Import(TestSecurityConfig.class)
public class ApplicationIT {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:18.6-trixie")
            .withInitScripts("schema-psql.sql", "init-test-data.sql");

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void authenticateRestTemplate() {
        String token = TestSecurityConfig.generateToken();
        restTemplate.getRestTemplate().getInterceptors()
                .add((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(token);
                    return execution.execute(request, body);
                });
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired 
    private NotesRepository repository;

    @Test
    void shouldReturnANote() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/notes/1", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String title = documentContext.read("$.title");
        assertThat(title).isEqualTo("Test note");
    }

    @Test
    void shouldNotReturnANoteWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/notes/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

}
