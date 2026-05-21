package com.seanconroy.fiae.service;

import com.seanconroy.fiae.entity.WhitelistUser;
import com.seanconroy.fiae.repository.WhitelistUserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@ApplicationScoped
public class WhitelistService {

    private static final Logger LOG = Logger.getLogger(WhitelistService.class);

    private final  SecureRandom secureRandom = new SecureRandom();

    @Inject
    WhitelistUserRepository whitelistUserRepository;

    public String hashApiKey(String plaintext) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Could not hash API key", e);
        }
    }
    public Optional<WhitelistUser> resolveUserFromApiKey(String plaintext) {
        if( plaintext == null || plaintext.isBlank()){
            return Optional.empty();
        }
        String hash = hashApiKey(plaintext);
        return whitelistUserRepository.findByApiKeyHash(hash)
        .filter(user -> user.isActive);
    }
    @Transactional
    public CreatedWhitelistUser createUser(String email,String githubUsername){
        if ( whitelistUserRepository.existsByEmail(email)) {
            throw new IllegalStateException("Whitelist user already exists");
        }
        String plaintextApiKey = generateApiKey();
        String apiKeyHash = hashApiKey(plaintextApiKey);

        WhitelistUser user = new WhitelistUser();
        user.email = email;
        user.githubUsername = githubUsername;
        user.apiKeyHash = apiKeyHash;
        user.createdAt = LocalDateTime.now();
        user.isActive = true;

        whitelistUserRepository.persist(user);

        LOG.infof("Created whitelist user for email=%s",email);

        return new CreatedWhitelistUser(user,plaintextApiKey);
    }
    private String generateApiKey() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return HexFormat.of().formatHex(randomBytes);
    }
    public record CreatedWhitelistUser(
        WhitelistUser user,
        String plaintextApiKey
    ){
        
    }
}