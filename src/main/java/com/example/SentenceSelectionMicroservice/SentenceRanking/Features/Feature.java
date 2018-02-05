package com.example.SentenceSelectionMicroservice.SentenceRanking.Features;

public interface Feature {
    Double getScore(int sentenceId, String key);
}
