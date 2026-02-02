package xyz.vanan.copycat.endpoint;

import static org.hamcrest.Matchers.is;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import xyz.vanan.copycat.config.Config;
import xyz.vanan.copycat.profile.AuthTestProfile;
import xyz.vanan.copycat.rest.Endpoint;

@QuarkusTest
@TestHTTPEndpoint(Endpoint.class)
@TestProfile(AuthTestProfile.class)
public class EndpointAuthTest {
    @Inject
    Config config;

    @Test
    public void shouldNotRequireAuthForRootTest() {
        when().get("/").then().statusCode(is(200));
    }

    @Test
    public void shouldReturnUnauthorizedWhenNoSecretSetTest() {
        when().get("/peek").then().statusCode(is(401));
    }

    @Test
    public void shouldReturnUnauthorizedWhenInvalidSecretSetTest() {
        given().headers("Authorization", "invalid").get("/peek").then().statusCode(is(401));
    }

    @Test
    public void shouldWorkWithCorrectAuthTest() {
        given().headers("Authorization", "Bearer " + config.token().get()).get("/list").then().statusCode(is(200));
    }
}
