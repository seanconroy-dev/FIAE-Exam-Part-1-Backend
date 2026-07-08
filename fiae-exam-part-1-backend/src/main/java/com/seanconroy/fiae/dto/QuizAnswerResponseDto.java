package com.seanconroy.fiae.dto;

import java.time.LocalDateTime;

/**
 * Response body for one recorded quiz answer.
 */
public class QuizAnswerResponseDto {

    public String cardSlug;
    public boolean wasCorrect;
    public LocalDateTime answeredAt;

    public QuizAnswerResponseDto() {
    }

    public QuizAnswerResponseDto(String cardSlug, boolean wasCorrect, LocalDateTime answeredAt) {
        this.cardSlug = cardSlug;
        this.wasCorrect = wasCorrect;
        this.answeredAt = answeredAt;
    }
}