package com.seanconroy.fiae.resource;

import com.seanconroy.fiae.dto.CardDto;
import com.seanconroy.fiae.service.CardService;
import com.seanconroy.fiae.validation.QueryParamValidator;
import jakarta.inject.Inject;
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
import java.util.Set;

@Path("/api/cards")
public class CardResource {

    private static final Set<String> ALLOWED_QUERY_PARAMS = Set.of("module");

    @Inject
    CardService cardService;

    @Inject
    QueryParamValidator queryParamValidator;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CardDto> getCards(@Context UriInfo uriInfo,
                                  @QueryParam("module") String module) {

        queryParamValidator.validateAllowedParams(
            uriInfo.getQueryParameters(),
            ALLOWED_QUERY_PARAMS
        );

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
