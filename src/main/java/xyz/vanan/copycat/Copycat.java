package xyz.vanan.copycat;

import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import xyz.vanan.copycat.config.Config;

@ApplicationScoped
public class Copycat {
    private static final Logger LOG = Logger.getLogger(Copycat.class);

    @Inject
    Config config;

    void onStart(@Observes StartupEvent ev) {
        // validate config - the token must be present if token auth is enabled
        if (!config.useToken()) {
            LOG.info("Token authentication disabled");
        } else {
            if (config.token().isPresent()) {
                LOG.info("Token authentication configured");
            } else {
                throw new IllegalStateException("Token must be present when token auth is enabled!");
            }
        }
    }
}
