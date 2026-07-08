package com.seanconroy.fiae.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response body for quiz session data.
 */
public class QuizSessionResponseDto {

    public UUID sessionId;
    public String module;
    public LocalDateTime startedAt;
    public LocalDateTime completedAt;
    public Integer scoreCorrect;
    public Integer scoreWrong;

    public QuizSessionResponseDto() {
    }

    public QuizSessionResponseDto(
            UUID sessionId,
            String module,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            Integer scoreCorrect,
            Integer scoreWrong
    ) {
        this.sessionId = sessionId;
        this.module = module;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.scoreCorrect = scoreCorrect;
        this.scoreWrong = scoreWrong;
    }
}