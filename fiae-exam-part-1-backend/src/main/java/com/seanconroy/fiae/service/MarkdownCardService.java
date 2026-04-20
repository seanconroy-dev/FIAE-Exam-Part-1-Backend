package com.seanconroy.fiae.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import com.seanconroy.fiae.dto.CardContentDto;
import com.seanconroy.fiae.dto.MarkdownCardDto;

@ApplicationScoped
public class MarkdownCardService {

    @ConfigProperty(name = "content.root")
    String contentRoot;

    public List<Path> getAllMarkdownFiles() {
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
    }

    public int countMarkdownFiles() {
        return getAllMarkdownFiles().size();
    }

    public String readMarkdownFile(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read markdown file: " + path.getFileName(), e);
        }
    }

    public String readFirstMarkdownFile() {
        List<Path> files = getAllMarkdownFiles();

        if (files.isEmpty()) {
            throw new RuntimeException("No markdown files found");
        }

        return readMarkdownFile(files.get(0));
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
            return raw; // fallback: whole file
        }

        return raw.substring(end + 3).trim();
    }

    public MarkdownCardDto parseMarkdownCard(Path path) {
        String raw = readMarkdownFile(path);

        String frontmatter = extractFrontmatter(raw);
        String body = extractBody(raw);

        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

            Map<String, Object> yaml = mapper.readValue(frontmatter, Map.class);

            MarkdownCardDto dto = new MarkdownCardDto();

            dto.id = (String) yaml.get("id");
            dto.slug = (String) yaml.get("slug");
            dto.title = (String) yaml.get("title");
            dto.tags = (List<String>) yaml.get("tags");
            dto.topics = (List<String>) yaml.get("topics");
            dto.module = (String) yaml.get("module");
            dto.created = String.valueOf(yaml.get("created"));
            dto.updated = String.valueOf(yaml.get("updated"));
            dto.status = (String) yaml.get("status");
            dto.created = (String) yaml.get("created");
            dto.updated = (String) yaml.get("updated");
            dto.body = body;

            // ---- card (nested object) ----
            Map<String, Object> cardMap = (Map<String, Object>) yaml.get("card");

            CardContentDto card = new CardContentDto();

            if (cardMap != null) {
                card.type = (String) cardMap.get("type");
                card.question = (String) cardMap.get("question");
                card.answer = (String) cardMap.get("answer");
                card.examples = (List<String>) cardMap.get("examples");

                // images (this answers your second problem 👇)
                card.image = (String) cardMap.get("image");
                card.answerImage = (String) cardMap.get("answerImage");
            }

            dto.card = card;

            return dto;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse YAML for file: " + path.getFileName(), e);
        }
    }

    public List<MarkdownCardDto> getAllMarkdownCards() {
        List<Path> files = getAllMarkdownFiles();
        List<MarkdownCardDto> result = new ArrayList<>();

        for (Path path : files) {
            result.add(parseMarkdownCard(path));
        }

        return result;
    }
    public List<String> getAllMarkdownFileContents() {
    List<String> contents = new ArrayList<>();

    for (Path file : getAllMarkdownFiles()) {
        try {
            contents.add(Files.readString(file));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file, e);
        }
    }

    return contents;


    
}
}
