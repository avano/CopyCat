package xyz.vanan.copycat.rest;

import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import xyz.vanan.copycat.config.Config;
import xyz.vanan.copycat.database.Database;
import xyz.vanan.copycat.payload.Payload;

@Path("/")
public class Endpoint {
    @Inject
    Config config;

    @Inject
    Database db;

    @GET
    public RestResponse<String> landingPage() {
        // it's a cat!
        return RestResponse.ok("\uD83D\uDC08");
    }

    @POST
    @Path("/push")
    public RestResponse<Void> push(byte[] body) {
        if (body.length == 0) {
            return RestResponse.status(400, "Request body is required");
        }

        db.persist(new Payload(body));
        return RestResponse.ok();
    }

    @GET
    @Path("/list")
    @Produces("application/json")
    public RestResponse<List<Payload>> list(@QueryParam("count") int count) {
        return RestResponse.ok(db.list(count == 0 ? config.resultCount() : count));
    }

    @GET
    @Path("/peek")
    public RestResponse<byte[]> getLast() {
        final Payload last = db.getLast();
        if (last == null) {
            return RestResponse.notFound();
        } else {
            return RestResponse.ResponseBuilder.ok(last.getData()).header("Content-Type", last.getMediaType().toString()).build();
        }
    }

    @GET
    @Path("/type")
    public RestResponse<String> getMediaType() {
        final Payload last = db.getLast();
        if (last == null) {
            return RestResponse.notFound();
        } else {
            return RestResponse.ResponseBuilder.ok(last.getMediaType().toString()).build();
        }
    }
}
