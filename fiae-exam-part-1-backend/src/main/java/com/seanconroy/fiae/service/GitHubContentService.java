package com.seanconroy.fiae.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class GitHubContentService {

    private static final String BASE_URL = "https://raw.githubusercontent.com/seanconroy-dev/FIAE-Exam-Part-1-Content/main";

    public String fetchFile(String path) {
        try {
            String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8).replace("+", "%20").replace("%2F",
                    "/");
            String url = BASE_URL + "/" + encodedPath;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("GitHub file not found: " + path);
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch from GitHub", e);
        }
    }
}
