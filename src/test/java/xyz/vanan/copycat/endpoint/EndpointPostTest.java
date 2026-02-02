package xyz.vanan.copycat.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import xyz.vanan.copycat.payload.Payload;
import xyz.vanan.copycat.rest.Endpoint;

@QuarkusTest
@TestHTTPEndpoint(Endpoint.class)
public class EndpointPostTest {
    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    public void cleanup() {
        em.createQuery("DELETE FROM " + Payload.class.getSimpleName()).executeUpdate();
    }

    @Test
    public void shouldRejectEmptyBodyTest() {
        when().post("/push").then().statusCode(400);
    }

    @Test
    public void shouldAddIntoDbTest() {
        String content = "Hello world";
        given().body(content.getBytes()).post("/push").then().statusCode(200);
        assertThat(getEntity().getData()).isNotNull().isEqualTo(content.getBytes());
    }

    @Test
    public void shouldStoreCreationTimestampTest() {
        given().body("Hello world").post("/push").then().statusCode(200);
        assertThat(Duration.between(getEntity().getCreatedAt(), Instant.now())).isLessThan(Duration.ofSeconds(5));
    }

    private Payload getEntity() {
        final List<Payload> result = em.createQuery("SELECT p FROM Payload p ORDER BY p.id ASC", Payload.class).getResultList();
        assertThat(result).hasSize(1);
        return result.getFirst();
    }
}
