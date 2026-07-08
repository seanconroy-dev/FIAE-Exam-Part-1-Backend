package com.seanconroy.fiae.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.seanconroy.fiae.dto.QuizAnswerResponseDto;
import com.seanconroy.fiae.entity.QuizSession;
import com.seanconroy.fiae.entity.QuizSessionCard;
import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.repository.QuizSessionCardRepository;
import com.seanconroy.fiae.repository.QuizSessionRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * Provides business logic for quiz sessions and recorded quiz answers.
 */
@ApplicationScoped
public class QuizSessionService {

    @Inject
    QuizSessionRepository quizSessionRepository;

    @Inject
    QuizSessionCardRepository quizSessionCardRepository;

    /**
     * Creates a new quiz session for the authenticated user.
     *
     * @param user authenticated user
     * @param module selected quiz module
     * @return newly created quiz session
     */
    @Transactional
    public QuizSession startSession(WhitelistUser user, String module) {
        QuizSession session = new QuizSession();

        session.sessionId = UUID.randomUUID();
        session.user = user;
        session.module = module;
        session.startedAt = LocalDateTime.now();
        session.completedAt = null;
        session.scoreCorrect = null;
        session.scoreWrong = null;

        quizSessionRepository.persist(session);

        return session;
    }

    /**
     * Records one answer for a quiz session owned by the authenticated user.
     *
     * @param user authenticated user
     * @param sessionId quiz session ID
     * @param cardSlug answered card identifier
     * @param wasCorrect whether the answer was correct
     * @return saved answer, or empty if the session is not owned/found
     */
    @Transactional
    public Optional<QuizSessionCard> recordAnswer(
            WhitelistUser user,
            UUID sessionId,
            String cardSlug,
            boolean wasCorrect
    ) {
        Optional<QuizSession> sessionOptional =
                quizSessionRepository.findByUserAndSessionId(user, sessionId);

        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }

        QuizSession session = sessionOptional.get();

        QuizSessionCard cardResult = new QuizSessionCard();
        cardResult.session = session;
        cardResult.cardSlug = cardSlug;
        cardResult.wasCorrect = wasCorrect;
        cardResult.answeredAt = LocalDateTime.now();

        quizSessionCardRepository.persist(cardResult);

        return Optional.of(cardResult);
    }

    /**
     * Completes a quiz session and stores the final score.
     *
     * @param user authenticated user
     * @param sessionId quiz session ID
     * @return completed session, or empty if the session is not owned/found
     */
    @Transactional
    public Optional<QuizSession> completeSession(WhitelistUser user, UUID sessionId) {
        Optional<QuizSession> sessionOptional =
                quizSessionRepository.findByUserAndSessionId(user, sessionId);

        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }

        QuizSession session = sessionOptional.get();

        List<QuizSessionCard> cards =
                quizSessionCardRepository.findBySession(session);

        int scoreCorrect = 0;

        for (QuizSessionCard card : cards) {
            if (card.wasCorrect) {
                scoreCorrect++;
            }
        }

        int scoreWrong = cards.size() - scoreCorrect;

        session.completedAt = LocalDateTime.now();
        session.scoreCorrect = scoreCorrect;
        session.scoreWrong = scoreWrong;

        return Optional.of(session);
    }

    /**
     * Finds a quiz session owned by the authenticated user.
     *
     * @param user authenticated user
     * @param sessionId quiz session ID
     * @return quiz session, or empty if the session is not owned/found
     */
    public Optional<QuizSession> getSessionForUser(WhitelistUser user, UUID sessionId) {
        return quizSessionRepository.findByUserAndSessionId(user, sessionId);
    }

    /**
     * Returns all recorded answers for a quiz session owned by the authenticated user.
     *
     * @param user authenticated user
     * @param sessionId quiz session ID
     * @return answer list, or empty if the session is not owned/found
     */
    public Optional<List<QuizSessionCard>> getAnswersForSession(WhitelistUser user, UUID sessionId) {
        Optional<QuizSession> sessionOptional =
                quizSessionRepository.findByUserAndSessionId(user, sessionId);

        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }

        QuizSession session = sessionOptional.get();

        List<QuizSessionCard> answerRows =
                quizSessionCardRepository.findBySession(session);

        return Optional.of(answerRows);
    }

 
}