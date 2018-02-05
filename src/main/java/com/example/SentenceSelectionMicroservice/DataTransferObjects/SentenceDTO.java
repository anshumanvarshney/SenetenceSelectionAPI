package com.example.SentenceSelectionMicroservice.DataTransferObjects;

public class SentenceDTO {
    int sentenceId;
    Double score;

    public SentenceDTO(int sentenceId, Double score) {
        this.sentenceId = sentenceId;
        this.score = score;
    }

    public int getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
