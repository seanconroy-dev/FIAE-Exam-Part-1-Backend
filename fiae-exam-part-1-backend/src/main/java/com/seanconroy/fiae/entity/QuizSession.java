package com.seanconroy.fiae.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "quiz_session")
@Entity
public class QuizSession extends PanacheEntityBase {
@Id
@Column(name = "session_id")
public UUID sessionId;
@Column(name = "module")
public String module;
@Column(name = "started_at", nullable = false)
public LocalDateTime startedAt;
@Column(name = "completed_at")
public LocalDateTime completedAt;
@Column(name = "score_correct")
public Integer scoreCorrect;
@Column(name = "score_wrong")
public Integer scoreWrong;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
public WhitelistUser user;
}
