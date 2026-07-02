package com.seanconroy.fiae.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

@Table(name = "quiz_session_card")
@Entity
public class QuizSessionCard extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    public QuizSession session;
    @Column(name = "card_slug", nullable = false)
    public String cardSlug;
    @Column(name = "was_correct", nullable = false)
    public boolean wasCorrect;
    @Column(name = "answered_at", nullable = false)
    public LocalDateTime answeredAt;
}