package xyz.vanan.copycat;

import java.io.IOException;
import java.io.InputStream;

public final class TestUtils {
    private TestUtils() {
    }

    public static byte[] readResource(String name) {
        byte[] result;
        try (InputStream is = TestUtils.class.getResourceAsStream(name)) {
            result = is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Unable to open resource", e);
        }
        return result;
    }
}
