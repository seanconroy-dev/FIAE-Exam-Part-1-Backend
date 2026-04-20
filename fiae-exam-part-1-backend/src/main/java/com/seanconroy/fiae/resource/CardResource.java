
package com.seanconroy.fiae.resource;

import com.seanconroy.fiae.dto.CardDto;
import com.seanconroy.fiae.dto.ListResponseDto;
import com.seanconroy.fiae.dto.MarkdownCardDto;
import com.seanconroy.fiae.dto.MarkdownCardListResponseDto;
import com.seanconroy.fiae.service.CardService;
import com.seanconroy.fiae.service.MarkdownCardService;
import com.seanconroy.fiae.validation.QueryParamValidator;


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
    public ListResponseDto<CardDto> getCards(
    @Context UriInfo uriInfo,
    @QueryParam("module") String module
) {

        queryParamValidator.validateAllowedParams(
            uriInfo.getQueryParameters(),
            ALLOWED_QUERY_PARAMS
        );
        if (module == null || module.isBlank()) {
    throw new BadRequestException("Query parameter 'module' cannot be blank");
}

       return new ListResponseDto<>(cardService.getByModule(module));
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
  public CardDto getById(@PathParam("id") String id) {
    return cardService.getById(id);
}
@GET
@Path("/all")
@Produces(MediaType.APPLICATION_JSON)
public ListResponseDto<CardDto> getAllCards() {
    return new ListResponseDto<>(cardService.getCards());
}
@Inject
MarkdownCardService markdownCardService;
@GET
@Path("/markdown/count")
@Produces(MediaType.TEXT_PLAIN)
public String countMarkdownFiles() {
    return String.valueOf(markdownCardService.countMarkdownFiles());
}
@GET
@Path("/markdown/first")
@Produces(MediaType.TEXT_PLAIN)
public String getFirstMarkdownFile() {
    return markdownCardService.readFirstMarkdownFile();
}
@GET
@Path("/markdown/frontmatter")
@Produces(MediaType.TEXT_PLAIN)
public String getFirstMarkdownFrontmatter() {
    String raw = markdownCardService.readFirstMarkdownFile();
    return markdownCardService.extractFrontmatter(raw);
}
@GET
@Path("/markdown/all")
@Produces(MediaType.APPLICATION_JSON)
public MarkdownCardListResponseDto getAllMarkdownCards() {
    return new MarkdownCardListResponseDto(
        markdownCardService.getAllMarkdownCards()
    );
}
@GET
@Path("/markdown/frontmatter-lines")
@Produces(MediaType.APPLICATION_JSON)
public List<String> getFirstMarkdownFrontmatterLines() {
    String raw = markdownCardService.readFirstMarkdownFile();
    String frontmatter = markdownCardService.extractFrontmatter(raw);
    return List.of(frontmatter.split("\n"));
}
@GET
@Path("/markdown/raw-count")
@Produces(MediaType.TEXT_PLAIN)
public String countMarkdownContents() {
    return String.valueOf(markdownCardService.getAllMarkdownFileContents().size());
}
@GET
@Path("/markdown")
@Produces(MediaType.APPLICATION_JSON)
public List<MarkdownCardDto> getMarkdownCards() {
    return markdownCardService.getAllMarkdownCards();
}
}
