package com.seanconroy.fiae.dto;

import java.time.LocalDateTime;

public class LearningProgressResponseDto {

    public String cardSlug;
    public int timesSeen;
    public int timesCorrect;
    public LocalDateTime lastSeenAt;

    public LearningProgressResponseDto() {
    }

    public LearningProgressResponseDto(String cardSlug, int timesSeen, int timesCorrect, LocalDateTime lastSeenAt) {
        this.cardSlug = cardSlug;
        this.timesSeen = timesSeen;
        this.timesCorrect = timesCorrect;
        this.lastSeenAt = lastSeenAt;
    }

}