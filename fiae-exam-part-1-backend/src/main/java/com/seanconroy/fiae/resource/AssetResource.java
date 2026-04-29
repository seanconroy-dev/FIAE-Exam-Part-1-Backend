package com.seanconroy.fiae.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Path("/api/assets")
public class AssetResource {

    private static final String RAW_BASE_URL =
            "https://raw.githubusercontent.com/seanconroy-dev/FIAE-Exam-Part-1-Content/main/assets";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @GET
    @Path("/{filename}")
    @Produces({"image/png", "image/jpeg", "image/webp", "image/gif"})
    public Response getAsset(@PathParam("filename") String filename) {
        try {
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            String url = RAW_BASE_URL + "/" + encodedFilename;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
            );

            if (response.statusCode() == 404) {
                throw new WebApplicationException("Asset not found", 404);
            }

            if (response.statusCode() != 200) {
                throw new WebApplicationException("Failed to load asset", 500);
            }

            String contentType = response.headers().firstValue("Content-Type")
                    .orElse("application/octet-stream");

            return Response.ok(response.body(), contentType).build();

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException("Failed to load asset", 500);
        }
    }
}