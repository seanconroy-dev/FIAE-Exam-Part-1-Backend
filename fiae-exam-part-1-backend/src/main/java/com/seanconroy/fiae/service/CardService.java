package com.seanconroy.fiae.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seanconroy.fiae.dto.CardDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import org.jboss.logging.Logger;
import java.io.InputStream;
import java.util.List;

@ApplicationScoped
public class CardService {

    private static final Logger LOG = Logger.getLogger(CardService.class);

    @Inject
    ObjectMapper objectMapper;

    public List<CardDto> getCards() {

        LOG.info("Loading cards from JSON file");

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("seed/cards.json")) {

            if (inputStream == null) {
                LOG.warn("cards.json not found");
                throw new RuntimeException("cards.json not found");
            }

            List<CardDto> cards = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<CardDto>>() {
                    });

            LOG.infof("Loaded %d cards", cards.size());

            return cards;

        } catch (Exception e) {
            // Do not log here.
            // Unexpected exceptions are logged centrally in GlobalExceptionMapper
            // to avoid duplicate error entries.
            throw new RuntimeException("Failed to load cards.json", e);
        }
    }

    public CardDto getById(String id) {

        LOG.infof("Looking up card by id: %s", id);

        CardDto result = getCards().stream()
                .filter(card -> id.equals(card.id))
                .findFirst()
                .orElse(null);

        if (result == null) {
            LOG.warnf("No card found for id: %s", id);
            throw new NotFoundException("Card not found for id: " + id);
        }

        return result;
    }

    public List<CardDto> getByModule(String module) {

        LOG.infof("Filtering cards by module: %s", module);

        List<CardDto> result = getCards().stream()
                .filter(card -> module.equalsIgnoreCase(card.module))
                .toList();

        if (result.isEmpty()) {
            LOG.warnf("No cards found for module: %s", module);
        }

        return result;
    }
}