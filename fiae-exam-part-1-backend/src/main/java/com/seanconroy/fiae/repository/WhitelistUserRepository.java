package com.seanconroy.fiae.repository;

import com.seanconroy.fiae.entity.WhitelistUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class WhitelistUserRepository implements PanacheRepository<WhitelistUser> {
    public Optional<WhitelistUser> findByEmail(String email){
        return find("email",email).firstResultOptional();
    }
    public Optional<WhitelistUser> findByApiKeyHash(String apiKeyHash){
        return find("apiKeyHash",apiKeyHash).firstResultOptional();
    }
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}