package xyz.vanan.copycat.rest.auth;

import org.jboss.logging.Logger;

import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import xyz.vanan.copycat.config.Config;

/**
 * Incoming request filter that checks the authentication header when the token auth is active.
 */
@ApplicationScoped
@Provider
public class TokenAuthFilter implements ContainerRequestFilter {
    private static final Logger LOG = Logger.getLogger(TokenAuthFilter.class);

    @Inject
    Config config;

    @Inject
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOG.debugf("Received request from %s for %s", request.remoteAddress().host(), requestContext.getUriInfo().getPath());
        if (!config.useToken() || "/".equals(requestContext.getUriInfo().getPath())) {
            LOG.trace("Permitted");
            // Don't do anything if the token auth is disabled or when the request is for "/"
            return;
        }

        if (!("Bearer " + config.token().get()).equals(requestContext.getHeaderString("Authorization"))) {
            LOG.trace("Rejected - invalid token");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
