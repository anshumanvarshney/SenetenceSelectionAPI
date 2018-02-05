package com.example.SentenceSelectionMicroservice.SentenceRanking.Features;

import com.example.SentenceSelectionMicroservice.Services.NLPEngineImplementation;
import java.io.IOException;
import java.util.*;
import static com.example.SentenceSelectionMicroservice.Constant.SCALE;
import static com.example.SentenceSelectionMicroservice.Constant.TERM_FREQUENCY_BOOST;

public class TermFrequencyFeature implements Feature {

    public static Double boost = TERM_FREQUENCY_BOOST;
    private String docId;
    private int docLength;
    private List<Map<String, Double>> sentenceScore;
    private NLPEngineImplementation nlpEngineImplementation;

    public TermFrequencyFeature(String docId, int docLength) throws IOException {
        this.docId = docId;
        this.docLength = docLength;
        this.sentenceScore = new ArrayList<>();
        this.nlpEngineImplementation = new NLPEngineImplementation();
        System.out.println("TermFrequencyFeature.TermFrequencyFeature"+"DocId: "+this.docId+"[PARSING]");
        this.pruneSentence();
    }

    private List<String> getTokens(int sentenceId) throws IOException {
        Map<String, List<String>> tags = nlpEngineImplementation.getPOSTags(this.docId,sentenceId);
        List<String> tokens = tags.get("token");
        return tokens;
    }

    private Map<String, Double> calculation(String sentence,int sentenceId) throws IOException {
        List<String> tokens = getTokens(sentenceId);
        Double maxCount = 0.0;
        Map<String, Double> term = new HashMap<>();
        Map<String, Double> score = new HashMap<>();
        for (String word : tokens) {
            int freq = 0;
            if(!".".equals(word)) {
                freq = nlpEngineImplementation.termFrequency(word, docId);
            }
            term.put(word, Double.valueOf(freq));
            maxCount = Math.max(maxCount, freq);
        }
        Double value = SCALE / maxCount;
        score = termFrequency(term, value);
        return score;
    }

    private void pruneSentence() throws IOException {
        Map<String, Double> returnedScore = new HashMap<String, Double>();
        for (int i = 0; i < docLength; i++) {
            String sentence = nlpEngineImplementation.getSentenceFromId(docId, i);
            returnedScore = calculation(sentence,i);
            sentenceScore.add(returnedScore);
        }
        System.out.println("TermFrequencyFeature.pruneSentence"+docId+" Sentence Score: "+sentenceScore+" [PARSED]");
    }

    private Map<String, Double> termFrequency(Map<String, Double> term, Double value) {
        for (Map.Entry<String, Double> entry : term.entrySet()) {
            term.put(entry.getKey(), (entry.getValue() * value));
        }
        return term;
    }

    @Override
    public Double getScore(int sentenceId, String key) {
        Double totalScore = Double.valueOf(0);
        if (sentenceScore.get(sentenceId).containsKey(key)) {
            totalScore = sentenceScore.get(sentenceId).get(key);
        } else {
            totalScore = Double.valueOf(0);
        }
        return totalScore;
    }

    public static void main(String[] args) throws IOException {
        TermFrequencyFeature termFrequency = new TermFrequencyFeature("2473d2bc-53ad-4778-a5dd-8992361b31dc", 3);
    }
}