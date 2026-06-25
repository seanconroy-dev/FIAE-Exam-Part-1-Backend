package com.seanconroy.fiae.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "learning_progress",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "card_slug"})
        }
)
public class LearningProgress extends PanacheEntityBase {

    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        public WhitelistUser user;

        @Column(name = "card_slug", nullable = false)
    public String cardSlug;

    @Column(name = "times_seen", nullable = false)
    public int timesSeen = 0;

    @Column(name = "times_correct", nullable = false)
    public int timesCorrect = 0;

    @Column(name = "last_seen_at")
    public LocalDateTime lastSeenAt;

 
}

