package xyz.vanan.copycat.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.Mockito;

import java.util.List;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import jakarta.inject.Inject;
import xyz.vanan.copycat.TestUtils;
import xyz.vanan.copycat.config.Config;
import xyz.vanan.copycat.database.Database;
import xyz.vanan.copycat.payload.Payload;
import xyz.vanan.copycat.rest.Endpoint;

@QuarkusTest
@TestHTTPEndpoint(Endpoint.class)
public class EndpointGetTest {
    @Inject
    Config config;

    @InjectMock
    Database db;

    @ParameterizedTest
    @ValueSource(strings = {"/peek", "/type"})
    public void endpointsShouldReturnNotFoundWhenDbIsEmptyTest(String endpoint) {
        Mockito.when(db.getLast()).thenReturn(null);
        given().get(endpoint).then().statusCode(is(404));
    }

    @Test
    public void peekShouldReturnContentInTheBodyTest() {
        String text = "Hello world";
        Payload entity = new Payload(1, text.getBytes());
        Mockito.when(db.getLast()).thenReturn(entity);
        given().get("/peek").then().statusCode(is(200)).body(is(text));
    }

    @Test
    public void typeShouldReturnMimeTypeTest() {
        Payload entity = new Payload(1, TestUtils.readResource("/test.jpg"));
        Mockito.when(db.getLast()).thenReturn(entity);
        given().get("/type").then().statusCode(is(200)).body(is("image/jpg"));
    }

    @Test
    public void listShouldReturnListTest() {
        Payload payload = new Payload(1, "Hello world".getBytes());
        Mockito.when(db.list(config.resultCount())).thenReturn(List.of(payload));
        final List<Payload> received = given().get("/list").then().statusCode(is(200)).extract().as(new TypeRef<>() {
        });
        assertThat(received).hasSize(1).first().isEqualTo(payload);
    }

    @Test
    public void listShouldRespectQueryParamTest() {
        Payload payload1 = new Payload(1, "Hello world".getBytes());
        Payload payload2 = new Payload(2, "World hello".getBytes());
        // if the query param is not working, this method is invoked
        Mockito.when(db.list(config.resultCount())).thenReturn(List.of(payload2, payload1));
        Mockito.when(db.list(1)).thenReturn(List.of(payload2));
        final List<Payload> received = given().get("/list?count=1").then().statusCode(is(200)).extract().as(new TypeRef<>() {
        });
        assertThat(received).hasSize(1).first().isEqualTo(payload2);
    }
}
