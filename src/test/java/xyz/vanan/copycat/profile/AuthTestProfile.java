package xyz.vanan.copycat.profile;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * Test profile that configures the auth to be required.
 */
public class AuthTestProfile implements QuarkusTestProfile {
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "use-token", "true",
            "token", "test"
        );
    }
}
