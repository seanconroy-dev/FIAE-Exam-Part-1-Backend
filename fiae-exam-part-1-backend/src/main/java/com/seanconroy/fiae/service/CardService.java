package com.seanconroy.fiae.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seanconroy.fiae.dto.CardDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.util.List;

@ApplicationScoped
public class CardService {

    @Inject
    ObjectMapper objectMapper;

    public List<CardDto> getCards() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("seed/cards.json")) {
            if (inputStream == null) {
                throw new RuntimeException("cards.json not found");
            }

            return objectMapper.readValue(inputStream, new TypeReference<List<CardDto>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load cards.json", e);
        }
    }

    public CardDto getById(String id) {
        return getCards().stream()
            .filter(card -> card.id.equals(id))
            .findFirst()
            .orElse(null);
    }
    public List<CardDto> getByModule(String module) {
        return getCards().stream().filter(card -> module.equalsIgnoreCase(card.module)).toList();
    }
}