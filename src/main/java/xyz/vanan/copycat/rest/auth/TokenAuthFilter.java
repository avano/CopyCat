package xyz.vanan.copycat.rest.auth;

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
    @Inject
    Config config;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!config.useToken() || "/".equals(requestContext.getUriInfo().getPath())) {
            // Don't do anything if the token auth is disabled or when the request is for "/"
            return;
        }

        if (!("Bearer " + config.token().get()).equals(requestContext.getHeaderString("Authorization"))) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
