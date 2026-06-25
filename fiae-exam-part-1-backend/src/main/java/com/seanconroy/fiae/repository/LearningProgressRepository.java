package com.seanconroy.fiae.repository;

import java.util.List;
import java.util.Optional;

import com.seanconroy.fiae.entity.LearningProgress;
import com.seanconroy.fiae.entity.WhitelistUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LearningProgressRepository implements PanacheRepository<LearningProgress> {

    public List<LearningProgress> findByUser(WhitelistUser user) {
        return list("user", user);
    }

    public Optional<LearningProgress> findByUserAndCardSlug(WhitelistUser user, String cardSlug) {
        return find("user = ?1 and cardSlug = ?2", user, cardSlug).firstResultOptional();
    }

    public long deleteByUser(WhitelistUser user) {
        return delete("user", user);
    }
}