package com.seanconroy.fiae.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.seanconroy.fiae.dto.CardContentDto;
import com.seanconroy.fiae.dto.MarkdownCardDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MarkdownCardService {

    @ConfigProperty(name = "content.root")
    String contentRoot;
    @Inject
    GitHubContentService gitHubContentService;

    //temp
    public List<Path> getAllMarkdownFiles() {
    List<Path> files = new ArrayList<>();

    files.add(Paths.get("Beurteilen marktgängiger IT-Systeme und Lösungen/ap1-0153-hypervisor-typ1-vs-typ2.md"));

    return files;
}

/*     public List<Path> getAllMarkdownFiles() {
        List<Path> files = new ArrayList<>();

        try {
            Path root = Paths.get(contentRoot);
            System.out.println("Scanning root: " + root.toAbsolutePath());

            if (!Files.exists(root)) {
                return files;
            }

            Files.walk(root)
                    .filter(path -> path.toString().endsWith(".md"))
                    .filter(path -> path.getFileName().toString().startsWith("ap1"))
                    .forEach(files::add);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read markdown files", e);
        }

        return files;
    } */

    public int countMarkdownFiles() {
        return getAllMarkdownFiles().size();
    }

   public String readMarkdownFile(Path path) {
    try {
        String relativePath = path.toString().replace("\\", "/");
        return gitHubContentService.fetchFile(relativePath);
    } catch (Exception e) {
        throw new RuntimeException("Failed to read markdown file: " + path.getFileName(), e);
    }
}

    public String extractFrontmatter(String raw) {
        int start = raw.indexOf("---");
        int end = raw.indexOf("---", start + 3);

        if (start == -1 || end == -1) {
            return "";
        }

        return raw.substring(start + 3, end).trim();
    }

    public String extractBody(String raw) {
        int start = raw.indexOf("---");
        int end = raw.indexOf("---", start + 3);

        if (start == -1 || end == -1) {
            return raw;
        }

        return raw.substring(end + 3).trim();
    }

    public MarkdownCardDto parseMarkdownCardFromRaw(String raw, String sourceName) {

        String frontmatter = extractFrontmatter(raw);
        String body = extractBody(raw);

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> yaml = mapper.readValue(frontmatter, Map.class);

            MarkdownCardDto dto = new MarkdownCardDto();

            dto.id = (String) yaml.get("id");
            dto.slug = (String) yaml.get("slug");
            dto.title = (String) yaml.get("title");
            dto.tags = yaml.get("tags") != null ? (List<String>) yaml.get("tags") : new ArrayList<>();
            dto.topics = yaml.get("topics") != null ? (List<String>) yaml.get("topics") : new ArrayList<>();
            dto.module = yaml.get("module") != null ? (String) yaml.get("module") : null;
            dto.status = yaml.get("status") != null ? (String) yaml.get("status") : null;
            dto.created = yaml.get("created") != null ? String.valueOf(yaml.get("created")) : null;
            dto.updated = yaml.get("updated") != null ? String.valueOf(yaml.get("updated")) : null;
            dto.body = body;

            Map<String, Object> cardMap = (Map<String, Object>) yaml.get("card");

            if (cardMap == null) {
                throw new RuntimeException("Missing 'card' block in: " + sourceName);
            }

            CardContentDto card = new CardContentDto();
            card.type = (String) cardMap.get("type");
            card.question = (String) cardMap.get("question");
            card.answer = (String) cardMap.get("answer");
            card.examples = cardMap.get("examples") != null ? (List<String>) cardMap.get("examples")
                    : new ArrayList<>();
            card.image = (String) cardMap.get("image");
            card.answerImage = (String) cardMap.get("answerImage");

            dto.card = card;

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Missing 'card' block in: " + sourceName);
        }
    }

    public MarkdownCardDto parseMarkdownCard(Path path) {

        String raw = readMarkdownFile(path);
        return parseMarkdownCardFromRaw(raw, path.getFileName().toString());
    }

    public List<MarkdownCardDto> getAllMarkdownCards() {
        List<Path> files = getAllMarkdownFiles();
        List<MarkdownCardDto> result = new ArrayList<>();

        for (Path path : files) {
            result.add(parseMarkdownCard(path));
        }

        return result;
    }

    public List<MarkdownCardDto> getMarkdownCardsByModule(String module) {
        return getAllMarkdownCards().stream()
                .filter(card -> card.getModule() != null)
                .filter(card -> card.getModule().equalsIgnoreCase(module))
                .toList();
    }

    public MarkdownCardDto getMarkdownCardBySlug(String slug) {
        return getAllMarkdownCards().stream()
                .filter(card -> card.getSlug() != null)
                .filter(card -> card.getSlug().equalsIgnoreCase(slug))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Markdown card not found: " + slug));
    }

    public String fetchMarkdownFromUrl(String url) {
        try {
            return java.net.http.HttpClient.newHttpClient()
                    .send(
                            java.net.http.HttpRequest.newBuilder()
                                    .uri(java.net.URI.create(url))
                                    .GET()
                                    .build(),
                            java.net.http.HttpResponse.BodyHandlers.ofString())
                    .body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch markdown from: " + url, e);
        }
    }

    public MarkdownCardDto testFetchFromGithub() {
        String url = "https://raw.githubusercontent.com/seanconroy-dev/FIAE-Exam-Part-1-Content/main/Beurteilen%20marktg%C3%A4ngiger%20IT-Systeme%20und%20L%C3%B6sungen/ap1-0162-schnittstellen-erkennen.md";

        String raw = fetchMarkdownFromUrl(url);

        return parseMarkdownCardFromRaw(raw, "github-test");
    }
}