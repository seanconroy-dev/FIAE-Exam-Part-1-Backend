package com.seanconroy.fiae.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import com.seanconroy.fiae.dto.CardDto;
import com.seanconroy.fiae.service.CardService;


@Path("/api/cards")
public class CardResource {

    @Inject
    CardService cardService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CardDto> getCards() {
        return cardService.getCards();
    }
    @GET
@Path("/{id}")
@Produces(MediaType.APPLICATION_JSON)
public CardDto getById(@PathParam("id") String id) {
    CardDto card = cardService.getById(id);
    if (card == null) {
        throw new NotFoundException("Card not found");
    }
    return card;
}
}
