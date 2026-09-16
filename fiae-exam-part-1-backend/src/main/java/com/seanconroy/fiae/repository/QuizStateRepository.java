package com.seanconroy.fiae.repository;

import com.seanconroy.fiae.entity.QuizState;
import com.seanconroy.fiae.entity.WhitelistUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class QuizStateRepository implements PanacheRepository<QuizState> {

    public Optional<QuizState> findByUserAndModuleKey(WhitelistUser user, String moduleKey) {
        return find("user = ?1 and moduleKey = ?2", user, moduleKey).firstResultOptional();
    }

    public long deleteByUserAndModuleKey(WhitelistUser user, String moduleKey) {
        return delete("user = ?1 and moduleKey = ?2", user, moduleKey);
    }
}
