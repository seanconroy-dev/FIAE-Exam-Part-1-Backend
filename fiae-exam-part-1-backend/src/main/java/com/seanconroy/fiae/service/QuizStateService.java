package com.seanconroy.fiae.service;

import com.seanconroy.fiae.dto.QuizStateUpsertRequestDto;
import com.seanconroy.fiae.entity.QuizState;
import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.repository.QuizStateRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class QuizStateService {

    @Inject
    QuizStateRepository quizStateRepository;

    public Optional<QuizState> getState(WhitelistUser user, String moduleKey) {
        return quizStateRepository.findByUserAndModuleKey(user, moduleKey);
    }

    @Transactional
    public UpsertResult putState(WhitelistUser user, String moduleKey, QuizStateUpsertRequestDto request) {
        validateRequest(moduleKey, request);

        Optional<QuizState> existing = quizStateRepository.findByUserAndModuleKey(user, moduleKey);

        if (existing.isEmpty()) {
            QuizState created = new QuizState();
            created.user = user;
            created.moduleKey = moduleKey;
            applySnapshot(created, request, Instant.now());
            quizStateRepository.persist(created);
            return UpsertResult.created(created);
        }

        QuizState current = existing.get();

        if (request.revision < current.revision) {
            return UpsertResult.conflict("Incoming revision is older than stored revision", current);
        }

        boolean identical = isIdenticalSnapshot(current, moduleKey, request);

        if (Objects.equals(request.revision, current.revision)) {
            if (identical) {
                return UpsertResult.idempotent(current);
            }
            return UpsertResult.conflict("Incoming snapshot differs for the same revision", current);
        }

        applySnapshot(current, request, Instant.now());
        quizStateRepository.persist(current);
        return UpsertResult.updated(current);
    }

    @Transactional
    public void deleteState(WhitelistUser user, String moduleKey) {
        quizStateRepository.deleteByUserAndModuleKey(user, moduleKey);
    }

    private void validateRequest(String moduleKey, QuizStateUpsertRequestDto request) {
        if (moduleKey == null || moduleKey.isBlank()) {
            throw new BadRequestException("Path parameter 'moduleKey' cannot be blank");
        }

        if (request.queueSlugs == null) {
            throw new BadRequestException("queueSlugs must not be null");
        }

        Set<String> unique = new java.util.HashSet<>();
        for (String slug : request.queueSlugs) {
            if (slug == null || slug.isBlank()) {
                throw new BadRequestException("queueSlugs must not contain null or blank values");
            }
            if (!unique.add(slug)) {
                throw new BadRequestException("queueSlugs must not contain duplicate values");
            }
        }

        if (request.resultsBySlug == null) {
            throw new BadRequestException("resultsBySlug must not be null");
        }

        for (Map.Entry<String, String> entry : request.resultsBySlug.entrySet()) {
            String slug = entry.getKey();
            String result = entry.getValue();

            if (slug == null || slug.isBlank()) {
                throw new BadRequestException("resultsBySlug keys must not be null or blank");
            }

            if (!unique.contains(slug)) {
                throw new BadRequestException("resultsBySlug contains slug that is not present in queueSlugs: " + slug);
            }

            if (!"correct".equals(result) && !"wrong".equals(result)) {
                throw new BadRequestException("resultsBySlug values must be exactly 'correct' or 'wrong'");
            }
        }

        if (request.currentIndex == null || request.currentIndex < 0) {
            throw new BadRequestException("currentIndex must be greater than or equal to 0");
        }

        int queueSize = request.queueSlugs.size();
        if (request.currentIndex > queueSize) {
            throw new BadRequestException("currentIndex must not exceed queueSlugs length");
        }

        if (request.completed == null) {
            throw new BadRequestException("completed must not be null");
        }

        if (request.completed) {
            if (request.currentIndex != queueSize) {
                throw new BadRequestException("completed quiz must use currentIndex equal to queueSlugs length");
            }
        } else {
            if (queueSize == 0) {
                if (request.currentIndex != 0) {
                    throw new BadRequestException("when queueSlugs is empty and completed is false, currentIndex must be 0");
                }
            } else if (request.currentIndex >= queueSize) {
                throw new BadRequestException("when completed is false, currentIndex must be within queueSlugs bounds");
            }
        }

        if (request.revision == null || request.revision < 0) {
            throw new BadRequestException("revision must be greater than or equal to 0");
        }

        if (request.startedAt == null) {
            throw new BadRequestException("startedAt must not be null");
        }
    }

    private boolean isIdenticalSnapshot(QuizState current, String moduleKey, QuizStateUpsertRequestDto request) {
        return Objects.equals(current.moduleKey, moduleKey)
                && Objects.equals(current.moduleName, request.moduleName)
                && Objects.equals(current.queueSlugs, request.queueSlugs)
                && current.currentIndex == request.currentIndex
                && Objects.equals(current.resultsBySlug, request.resultsBySlug)
                && current.completed == request.completed
                && current.revision == request.revision
                && Objects.equals(current.startedAt, request.startedAt);
    }

    private void applySnapshot(QuizState state, QuizStateUpsertRequestDto request, Instant now) {
        state.moduleName = request.moduleName;
        state.queueSlugs = new ArrayList<>(request.queueSlugs);
        state.currentIndex = request.currentIndex;
        state.resultsBySlug = new LinkedHashMap<>(request.resultsBySlug);
        state.completed = request.completed;
        state.revision = request.revision;
        state.startedAt = request.startedAt;
        state.updatedAt = now;
    }

    public record UpsertResult(UpsertStatus status, QuizState state, String message) {
        public static UpsertResult created(QuizState state) {
            return new UpsertResult(UpsertStatus.CREATED, state, null);
        }

        public static UpsertResult updated(QuizState state) {
            return new UpsertResult(UpsertStatus.UPDATED, state, null);
        }

        public static UpsertResult idempotent(QuizState state) {
            return new UpsertResult(UpsertStatus.IDEMPOTENT, state, null);
        }

        public static UpsertResult conflict(String message, QuizState state) {
            return new UpsertResult(UpsertStatus.CONFLICT, state, message);
        }
    }

    public enum UpsertStatus {
        CREATED,
        UPDATED,
        IDEMPOTENT,
        CONFLICT
    }
}
