package com.seanconroy.fiae.resource;

import com.seanconroy.fiae.dto.ErrorResponseDto;
import com.seanconroy.fiae.dto.QuizStateConflictResponseDto;
import com.seanconroy.fiae.dto.QuizStateResponseDto;
import com.seanconroy.fiae.dto.QuizStateUpsertRequestDto;
import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.service.AuthContext;
import com.seanconroy.fiae.service.QuizStateService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Path("/api/quiz-state")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QuizStateResource {

    @Inject
    AuthContext authContext;

    @Inject
    QuizStateService quizStateService;

    @GET
    @Path("/{moduleKey}")
    public QuizStateResponseDto getQuizState(@PathParam("moduleKey") String moduleKey) {
        WhitelistUser currentUser = requireCurrentUser();
        String normalizedModuleKey = validateModuleKey(moduleKey);

        return quizStateService.getState(currentUser, normalizedModuleKey)
                .map(QuizStateResponseDto::new)
                .orElseThrow(() -> new NotFoundException("Quiz state not found for moduleKey: " + normalizedModuleKey));
    }

    @PUT
    @Path("/{moduleKey}")
    public Response putQuizState(@PathParam("moduleKey") String moduleKey, @Valid QuizStateUpsertRequestDto request) {
        WhitelistUser currentUser = requireCurrentUser();
        String normalizedModuleKey = validateModuleKey(moduleKey);

        QuizStateService.UpsertResult result = quizStateService.putState(currentUser, normalizedModuleKey, request);

        if (result.status() == QuizStateService.UpsertStatus.CONFLICT) {
            QuizStateResponseDto currentState = new QuizStateResponseDto(result.state());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new QuizStateConflictResponseDto(result.message(), currentState))
                    .build();
        }

        QuizStateResponseDto responseDto = new QuizStateResponseDto(result.state());

        if (result.status() == QuizStateService.UpsertStatus.CREATED) {
            return Response.status(Response.Status.CREATED).entity(responseDto).build();
        }

        return Response.ok(responseDto).build();
    }

    @DELETE
    @Path("/{moduleKey}")
    public Response deleteQuizState(@PathParam("moduleKey") String moduleKey) {
        WhitelistUser currentUser = requireCurrentUser();
        String normalizedModuleKey = validateModuleKey(moduleKey);

        quizStateService.deleteState(currentUser, normalizedModuleKey);

        return Response.noContent().build();
    }

    private WhitelistUser requireCurrentUser() {
        WhitelistUser currentUser = authContext.getCurrentUser();

        if (currentUser == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity(new ErrorResponseDto("Missing or invalid API key", 403))
                            .type(MediaType.APPLICATION_JSON)
                            .build());
        }

        return currentUser;
    }

    private String validateModuleKey(String moduleKey) {
        String decodedModuleKey = moduleKey == null ? null : URLDecoder.decode(moduleKey, StandardCharsets.UTF_8);

        if (decodedModuleKey == null || decodedModuleKey.isBlank()) {
            throw new BadRequestException("Path parameter 'moduleKey' cannot be blank");
        }

        return decodedModuleKey;
    }
}
