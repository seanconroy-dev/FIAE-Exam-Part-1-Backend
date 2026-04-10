package com.seanconroy.fiae;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CardService {

    public List<CardDto> getCards() {
        CardDto card1 = new CardDto();
        card1.id = "1";
        card1.title = "Test Card";

        CardDto card2 = new CardDto();
        card2.id = "2";
        card2.title = "Second Card";

        return List.of(card1, card2);
    }

    public CardDto getById(String id) {
        return getCards().stream()
            .filter(card -> card.id.equals(id))
            .findFirst()
            .orElse(null);
    }
}
