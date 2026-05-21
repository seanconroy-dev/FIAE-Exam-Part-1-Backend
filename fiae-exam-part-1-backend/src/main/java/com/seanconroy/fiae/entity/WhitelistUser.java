package com.seanconroy.fiae.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "whitelist_user")
public class WhitelistUser extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(name = "github_username")
    public String githubUsername;

    @Column(name = "api_key_hash", nullable = false)
    public String apiKeyHash;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    public boolean isActive = true;
}