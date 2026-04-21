package com.seanconroy.fiae.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Files;

import java.nio.file.Paths;

@Path("/api/assets")
public class AssetResource {

    @ConfigProperty(name = "content.root")
    String contentRoot;

    @GET
    @Path("/{filename}")
    @Produces({"image/png", "image/jpeg", "image/webp", "image/gif"})
    public Response getAsset(@PathParam("filename") String filename) {
        try {
           java.nio.file.Path assetPath = Paths.get(contentRoot, "assets", filename);

            if (!Files.exists(assetPath) || !Files.isReadable(assetPath)) {
                throw new WebApplicationException("Asset not found", 404);
            }

            String contentType = Files.probeContentType(assetPath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
            }

            return Response.ok(assetPath.toFile(), contentType).build();

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException("Failed to load asset", 500);
        }
    }
}