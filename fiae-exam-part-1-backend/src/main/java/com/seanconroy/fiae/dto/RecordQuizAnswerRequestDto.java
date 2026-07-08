package com.seanconroy.fiae.dto;

/**
 * Request body for recording one quiz answer.
 */
public class RecordQuizAnswerRequestDto {

    public String cardSlug;
    public boolean wasCorrect;

    public RecordQuizAnswerRequestDto() {
    }

    public RecordQuizAnswerRequestDto(String cardSlug, boolean wasCorrect) {
        this.cardSlug = cardSlug;
        this.wasCorrect = wasCorrect;
    }

    public String getCardSlug() {
        return cardSlug;
    }

    public void setCardSlug(String cardSlug) {
        this.cardSlug = cardSlug;
    }

    public boolean getWasCorrect() {
        return wasCorrect;
    }

    public void setWasCorrect(boolean wasCorrect) {
        this.wasCorrect = wasCorrect;
    }
}