package com.seanconroy.fiae.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.seanconroy.fiae.entity.LearningProgress;
import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.repository.LearningProgressRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LearningProgressService {

    @Inject
    LearningProgressRepository learningProgressRepository;

    public List<LearningProgress> getAllProgressForUser(WhitelistUser user) {
        return learningProgressRepository.findByUser(user);
    }

    public Optional<LearningProgress> getProgressForCard(WhitelistUser user, String cardSlug) {
        return learningProgressRepository.findByUserAndCardSlug(user, cardSlug);
    }

    @Transactional
    public LearningProgress recordAnswer(WhitelistUser user, String cardSlug, boolean wasCorrect) {
        LearningProgress progress = learningProgressRepository.findByUserAndCardSlug(user, cardSlug)
                .orElseGet(() -> {
                    LearningProgress newProgress = new LearningProgress();
                    newProgress.user = user;
                    newProgress.cardSlug = cardSlug;
                    return newProgress;
                });

        progress.timesSeen++;

        if (wasCorrect) {
            progress.timesCorrect++;
        }

        progress.lastSeenAt = LocalDateTime.now();

        learningProgressRepository.persist(progress);

        return progress;
    }

    @Transactional
    public long deleteProgressForUser(WhitelistUser user) {
        return learningProgressRepository.deleteByUser(user);
    }

}
