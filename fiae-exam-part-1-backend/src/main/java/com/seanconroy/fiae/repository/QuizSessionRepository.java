package com.seanconroy.fiae.repository;

import java.util.List;
import java.util.UUID;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import com.seanconroy.fiae.entity.QuizSession;
import com.seanconroy.fiae.entity.WhitelistUser;

@ApplicationScoped
public class QuizSessionRepository implements PanacheRepositoryBase<QuizSession, UUID> {

    public List<QuizSession> findByUser(WhitelistUser user) {
        return list("user", user);
    }

    public Optional<QuizSession> findByUserAndSessionId(WhitelistUser user, UUID sessionId) {
        return find("user = ?1 and sessionId = ?2", user, sessionId)
                .firstResultOptional();
    }
}
