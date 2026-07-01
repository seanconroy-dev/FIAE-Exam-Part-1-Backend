package com.seanconroy.fiae.resource;

import org.hibernate.boot.internal.Abstract;

import com.seanconroy.fiae.service.AuthContext;
import com.seanconroy.fiae.service.LearningProgressService;
import com.seanconroy.fiae.dto.LearningProgressResponseDto;
import com.seanconroy.fiae.entity.LearningProgress;
import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.dto.UpdateProgressRequestDto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.POST;

@Produces(MediaType.APPLICATION_JSON)

@ApplicationScoped
@Path("/api/progress")
public class LearningProgressResource {
    
    @Inject
    AuthContext authContext;

    @Inject
    LearningProgressService learningProgressService;

    @GET
    public Response getAllProgress() {
        WhitelistUser user = authContext.getCurrentUser();

        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(learningProgressService.getAllProgressForUser(user)).build();
    }

    @GET
    @Path("/{cardSlug}")
    public Response getProgressForCard(@PathParam("cardSlug") String cardSlug) {
        WhitelistUser user = authContext.getCurrentUser();

        if ( user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        LearningProgressResponseDto progressDto = learningProgressService.getProgressForCard(user, cardSlug)
                .map(progress -> new LearningProgressResponseDto(
                        progress.cardSlug,
                        progress.timesSeen,
                        progress.timesCorrect,
                        progress.lastSeenAt
                ))
                .orElseGet(() -> new LearningProgressResponseDto(
                    cardSlug,
                    0,
                    0,
                    null
                ));
        return Response.ok(progressDto).build();
}
@POST
@Path("/{cardSlug}")
public Response recordAnswer(@PathParam("cardSlug") String cardSlug, UpdateProgressRequestDto request) {
    WhitelistUser user = authContext.getCurrentUser();

    if (user == null) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }

    LearningProgress progress = learningProgressService.recordAnswer(
            user,
            cardSlug,
            request.wasCorrect
    );

    LearningProgressResponseDto response = new LearningProgressResponseDto(
            progress.cardSlug,
            progress.timesSeen,
            progress.timesCorrect,
            progress.lastSeenAt
    );

    return Response.ok(response).build();
}
}