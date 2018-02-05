package com.example.SentenceSelectionMicroservice.Services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NLPEngine {
    String correctSentence(String sentence);

    List<List<Object>> getNamedEntity(String sentence);

    Map<String, List<String>> getPOSTags (String docId, int sentenceId) throws IOException;

    int termFrequency(String s, String docId);

    String getSentenceFromId(String docId, int sentenceId);

}
