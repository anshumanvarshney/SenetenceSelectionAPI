package com.example.SentenceSelectionMicroservice.SentenceRanking.Features;

import com.example.SentenceSelectionMicroservice.Services.NLPEngineImplementation;
import java.io.IOException;
import java.util.*;
import static com.example.SentenceSelectionMicroservice.Constant.LENGTH_OF_THE_WORD_BOOST;
import static com.example.SentenceSelectionMicroservice.Constant.SCALE;
import static com.example.SentenceSelectionMicroservice.Constant.TEST_DOCID;

public class LengthOfTheWordFeature implements Feature {

    public static Double boost = LENGTH_OF_THE_WORD_BOOST;
    private String docId;
    private int docLength;
    private List<Map<String, Double>> sentenceScore;
    private NLPEngineImplementation nlpEngineImplementation;
    private int maxLength;

    public LengthOfTheWordFeature(String docId, int docLength) throws IOException {
        this.docId = docId;
        this.docLength = docLength;
        this.sentenceScore = new ArrayList<>();
        this.maxLength = 0;
        this.nlpEngineImplementation = new NLPEngineImplementation();
        this.pruneSentence();
    }

    private Map<String, Double> calculation(String sentence){
        Map<String, Double> score = new HashMap<String, Double>();
        String[] arr = sentence.split(" ");
        Map<String, Double> lengthMap = new HashMap<>();
        for (String word : arr) {
            Double val = lengthMap.get(word.length());
            if (val == null) {
                val = 0.0;
            }
            lengthMap.put(word, (double) word.length());
            this.maxLength = Math.max(this.maxLength, word.length());
        }
        score = returnLength(lengthMap, Double.valueOf(SCALE));
        return score;
    }

    private void pruneSentence() {
        for (int i = 0; i < docLength; i++) {
            Map<String, Double> returnedScore = new HashMap<String, Double>();
            String sentence = nlpEngineImplementation.getSentenceFromId(docId, i);
            returnedScore = calculation(sentence);
            sentenceScore.add(returnedScore);
        }
        System.out.println("LengthOfTheWordFeature.pruneSentence"+docId+" Sentence Score [UNSCALED]: "+sentenceScore + " [PARSED]");
    }

    private Map<String, Double> returnLength(Map<String, Double> lengthMap, Double value) {
        for (Map.Entry<String, Double> entry : lengthMap.entrySet()) {
            lengthMap.put(entry.getKey(), (entry.getValue() * value));
        }
        return lengthMap;
    }

    @Override
    public Double getScore(int sentenceId, String key) {
        Double totalScore = Double.valueOf(0);
        if (sentenceScore.get(sentenceId).containsKey(key)) {
            totalScore = sentenceScore.get(sentenceId).get(key)/maxLength;
        } else {
            totalScore = Double.valueOf(0);
        }
        return totalScore;
    }

    public static void main(String[] args) throws IOException {
        LengthOfTheWordFeature lengthOfTheWordFeature = new LengthOfTheWordFeature(TEST_DOCID, 14);
    }
}

