package com.seanconroy.fiae.resource;

import com.seanconroy.fiae.dto.LearningProgressResponseDto;
import com.seanconroy.fiae.dto.ListResponseDto;
import com.seanconroy.fiae.dto.RecordAnswerRequestDto;
import com.seanconroy.fiae.entity.LearningProgress;
import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.service.AuthContext;
import com.seanconroy.fiae.service.LearningProgressService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/progress")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProgressResource {

    @Inject
    AuthContext authContext;

    @Inject
    LearningProgressService learningProgressService;

    @GET
    public ListResponseDto<LearningProgressResponseDto> getProgress() {
        WhitelistUser currentUser = requireCurrentUser();

        List<LearningProgressResponseDto> progress = learningProgressService.getAllProgressForUser(currentUser)
                .stream()
                .map(LearningProgressResponseDto::new)
                .toList();

        return new ListResponseDto<>(progress);
    }

    @POST
    @Path("/{cardSlug}/answer")
    public Response recordAnswer(@PathParam("cardSlug") String cardSlug, @Valid RecordAnswerRequestDto request) {
        WhitelistUser currentUser = requireCurrentUser();
        LearningProgress progress = learningProgressService.recordAnswer(currentUser, cardSlug, request.correct);
        return Response.ok(new LearningProgressResponseDto(progress)).build();
    }

    private WhitelistUser requireCurrentUser() {
        WhitelistUser currentUser = authContext.getCurrentUser();

        if (currentUser == null) {
            throw new jakarta.ws.rs.ForbiddenException("Missing or invalid API key");
        }

        return currentUser;
    }
}
