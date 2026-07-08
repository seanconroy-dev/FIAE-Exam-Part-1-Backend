package com.seanconroy.fiae.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.seanconroy.fiae.dto.CreateQuizSessionRequestDto;
import com.seanconroy.fiae.dto.QuizAnswerResponseDto;
import com.seanconroy.fiae.dto.QuizSessionResponseDto;
import com.seanconroy.fiae.dto.RecordQuizAnswerRequestDto;
import com.seanconroy.fiae.entity.QuizSession;
import com.seanconroy.fiae.entity.QuizSessionCard;
import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.service.AuthContext;
import com.seanconroy.fiae.service.QuizSessionService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST resource for quiz session endpoints.
 *
 * This class handles HTTP concerns such as:
 * - authentication checks
 * - path parameters
 * - request DTOs
 * - response DTOs
 * - HTTP status codes
 */
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Path("/api/sessions")
public class QuizSessionResource {

    @Inject
    QuizSessionService quizSessionService;

    @Inject
    AuthContext authContext;

    /**
     * Starts a new quiz session for the authenticated user.
     *
     * @param request request body containing the selected module
     * @return created quiz session, or 403 if the user is not authenticated
     */
    @POST
    public Response startSession(CreateQuizSessionRequestDto request) {
        WhitelistUser user = authContext.getCurrentUser();

        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        QuizSession session = quizSessionService.startSession(user, request.module);

        QuizSessionResponseDto response = new QuizSessionResponseDto(
                session.sessionId,
                session.module,
                session.startedAt,
                session.completedAt,
                session.scoreCorrect,
                session.scoreWrong
        );

        return Response.ok(response).build();
    }

    /**
     * Records one answer for a quiz session owned by the authenticated user.
     *
     * @param sessionId quiz session ID from the URL path
     * @param request request body containing cardSlug and wasCorrect
     * @return saved answer, or 403 if the user is not authenticated or does not own the session
     */
    @POST
    @Path("/{sessionId}/answer")
    public Response recordAnswer(
            @PathParam("sessionId") UUID sessionId,
            RecordQuizAnswerRequestDto request
    ) {
        WhitelistUser user = authContext.getCurrentUser();

        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Optional<QuizSessionCard> cardResultOptional = quizSessionService.recordAnswer(
                user,
                sessionId,
                request.cardSlug,
                request.wasCorrect
        );

        if (cardResultOptional.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        QuizSessionCard cardResult = cardResultOptional.get();

        QuizAnswerResponseDto response = new QuizAnswerResponseDto(
                cardResult.cardSlug,
                cardResult.wasCorrect,
                cardResult.answeredAt
        );

        return Response.ok(response).build();
    }

    /**
     * Completes a quiz session and returns the final score.
     *
     * @param sessionId quiz session ID from the URL path
     * @return completed session, or 403 if the user is not authenticated or does not own the session
     */
    @POST
    @Path("/{sessionId}/complete")
    public Response completeSession(@PathParam("sessionId") UUID sessionId) {
        WhitelistUser user = authContext.getCurrentUser();

        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Optional<QuizSession> sessionOptional = quizSessionService.completeSession(user, sessionId);

        if (sessionOptional.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        QuizSession session = sessionOptional.get();

        QuizSessionResponseDto response = new QuizSessionResponseDto(
                session.sessionId,
                session.module,
                session.startedAt,
                session.completedAt,
                session.scoreCorrect,
                session.scoreWrong
        );

        return Response.ok(response).build();
    }

    /**
     * Returns one quiz session owned by the authenticated user.
     *
     * @param sessionId quiz session ID from the URL path
     * @return quiz session, or 403 if the user is not authenticated or does not own the session
     */
    @GET
    @Path("/{sessionId}")
    public Response getSession(@PathParam("sessionId") UUID sessionId) {
        WhitelistUser user = authContext.getCurrentUser();

        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Optional<QuizSession> sessionOptional = quizSessionService.getSessionForUser(user, sessionId);

        if (sessionOptional.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        QuizSession session = sessionOptional.get();

        QuizSessionResponseDto response = new QuizSessionResponseDto(
                session.sessionId,
                session.module,
                session.startedAt,
                session.completedAt,
                session.scoreCorrect,
                session.scoreWrong
        );

        return Response.ok(response).build();
    }

    /**
     * Returns all recorded answers for a quiz session owned by the authenticated user.
     *
     * @param sessionId quiz session ID from the URL path
     * @return answer list, or 403 if the user is not authenticated or does not own the session
     */
    @GET
    @Path("/{sessionId}/answers")
    public Response getSessionAnswers(@PathParam("sessionId") UUID sessionId) {
        WhitelistUser user = authContext.getCurrentUser();

        if (user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Optional<List<QuizSessionCard>> answerRowsOptional =
                quizSessionService.getAnswersForSession(user, sessionId);

        if (answerRowsOptional.isEmpty()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        List<QuizSessionCard> answerRows = answerRowsOptional.get();
        List<QuizAnswerResponseDto> responseList = new ArrayList<>();

        for (QuizSessionCard answerRow : answerRows) {
            QuizAnswerResponseDto dto = new QuizAnswerResponseDto(
                    answerRow.cardSlug,
                    answerRow.wasCorrect,
                    answerRow.answeredAt
            );

            responseList.add(dto);
        }

        return Response.ok(responseList).build();
    }
}