package com.seanconroy.fiae.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

import com.seanconroy.fiae.dto.CardDto;
import com.seanconroy.fiae.service.CardService;

@Path("/api/cards")
public class CardResource {

    @Inject
    CardService cardService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CardDto> getCards(@Context UriInfo uriInfo,
                                  @QueryParam("module") String module) {

        var queryParams = uriInfo.getQueryParameters();

        for (String param : queryParams.keySet()) {
            if (!param.equals("module")) {
                throw new BadRequestException("Invalid query parameter: " + param);
            }
        }

        if (module != null) {
            return cardService.getByModule(module);
        }

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