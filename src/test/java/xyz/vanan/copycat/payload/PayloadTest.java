package xyz.vanan.copycat.payload;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import io.quarkus.test.junit.QuarkusTest;
import xyz.vanan.copycat.TestUtils;

@QuarkusTest
public class PayloadTest {
    public static Stream<Arguments> images() {
        return Stream.of(
            Arguments.of("/test.jpg", PayloadType.JPG),
            Arguments.of("/test.png", PayloadType.PNG)
        );
    }

    @ParameterizedTest
    @MethodSource("images")
    public void shouldProcessImageTest(String resource, PayloadType type) {
        assertThat(Utils.getPayloadType(TestUtils.readResource(resource))).isEqualTo(type);
    }

    @Test
    public void shouldReturnPlainTextMimeTypeTest() {
        assertThat(Utils.getPayloadType("This is a text".getBytes())).isEqualTo(PayloadType.TEXT);
    }
}
