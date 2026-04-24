package com.seanconroy.fiae.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GitHubContentService {

    private static final String RAW_BASE_URL =
            "https://raw.githubusercontent.com/seanconroy-dev/FIAE-Exam-Part-1-Content/main";

    private static final String API_BASE_URL =
            "https://api.github.com/repos/seanconroy-dev/FIAE-Exam-Part-1-Content/contents";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<String> cachedMarkdownFiles;

    public String fetchFile(String path) {
        try {
            String encodedPath = encodePath(path);
            String url = RAW_BASE_URL + "/" + encodedPath;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException("GitHub file not found: " + path);
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch from GitHub", e);
        }
    }

   public List<String> listMarkdownFiles() {
    if (cachedMarkdownFiles != null) {
        return cachedMarkdownFiles;
    }

    cachedMarkdownFiles = listMarkdownFilesFromPath("");
    return cachedMarkdownFiles;
}
    private List<String> listMarkdownFilesFromPath(String path) {
        try {
            String url = API_BASE_URL;

            if (path != null && !path.isBlank()) {
                url += "/" + encodePath(path);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/vnd.github+json")
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
               throw new RuntimeException("GitHub API listing failed for path: " + path + " with status " + response.statusCode() + " and body: " + response.body());
            }

            JsonNode nodes = objectMapper.readTree(response.body());
            List<String> files = new ArrayList<>();

            for (JsonNode node : nodes) {
                String type = node.get("type").asText();
                String filePath = node.get("path").asText();

                if ("dir".equals(type)) {
                    files.addAll(listMarkdownFilesFromPath(filePath));
                }

                if ("file".equals(type)
                        && filePath.endsWith(".md")
                        && node.get("name").asText().startsWith("ap1")) {
                    files.add(filePath);
                }
            }

            return files;

        } catch (Exception e) {
            throw new RuntimeException("Failed to list markdown files from GitHub", e);
        }
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%2F", "/");
    }
}