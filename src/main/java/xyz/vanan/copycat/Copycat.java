package xyz.vanan.copycat;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import xyz.vanan.copycat.config.Config;

@ApplicationScoped
public class Copycat {
    @Inject
    Config config;

    void onStart(@Observes StartupEvent ev) {
        // validate config - the token must be present if token auth is enabled
        if (config.useToken() && config.token().isEmpty()) {
            throw new IllegalStateException("Token must be present when token auth is enabled!");
        }
    }
}
