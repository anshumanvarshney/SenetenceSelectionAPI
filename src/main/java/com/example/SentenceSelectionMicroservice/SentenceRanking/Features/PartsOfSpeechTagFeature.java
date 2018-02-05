package com.example.SentenceSelectionMicroservice.SentenceRanking.Features;

import com.example.SentenceSelectionMicroservice.Services.NLPEngineImplementation;
import java.io.IOException;
import java.util.*;
import static com.example.SentenceSelectionMicroservice.Constant.*;
import static java.lang.Double.max;

public class PartsOfSpeechTagFeature implements Feature{
    public static Double boost = PARTS_OF_SPEECH_TAG_BOOST;
    private static final String bigramRules[][] = {{ADVERB, ADJECTIVE}, // John is a VERY GOOD boy.
                                                   {ADJECTIVE, NOUN}, // John is a very GOOD BOY.
                                                   {VERB, NOUN}, // John LOVES APPLE
                                                   {NOUN, VERB}, // BOYS LOVES apple.
                                                   {PROPER_NOUN, VERB}}; // JACK LOVES apple.

    private String docId;
    private int docLength;
    private List<Map<String, Double>> sentenceScore;
    private double maxScore = 1;
    private NLPEngineImplementation nlpEngineImplementation;

    public PartsOfSpeechTagFeature(String docId, int docLength) throws IOException {
        this.docId = docId;
        this.docLength = docLength;
        this.sentenceScore = new ArrayList<>();
        this.nlpEngineImplementation = new NLPEngineImplementation();
        System.out.println("PartsOfSpeechTagFeature.PartsOfSpeechTagFeature "+"DocId: "+this.docId+" [PARSING]");
        this.pruneSentence();
    }

    private List<Object> getPOSTag(int sentenceId) throws IOException {
        Map<String, List<String>> posTags = nlpEngineImplementation.getPOSTags(this.docId, sentenceId);
        return Arrays.asList(posTags.get("pos"), posTags.get("token"));
    }

    public Map<String, Double> setScore(String key, Map<String, Double> score) {
        if(score.containsKey(key)) {
            score.put(key, score.get(key)+1);
            maxScore = max(maxScore, score.get(key)+1);
        }else {
            score.put(key, (double) 1);
        }
        return score;
    }

    private void normaliseScore() {
        for(Map<String, Double> j: sentenceScore) {
            for(Map.Entry<String, Double> entry: j.entrySet()) {
                j.put(entry.getKey(), entry.getValue()*(SCALE/maxScore));
            }
        }
    }

    private Map<String, Double> matchOnBigram(List<String> posTags, List<String> tokens, Map<String, Double> score) {
        for(int i=0; i<posTags.size()-1; i++) {
           Boolean isMatch = matchRules(posTags.get(i), posTags.get(i + 1));
           if(isMatch) {
               score = setScore(tokens.get(i), score);
               score = setScore(tokens.get(i+1), score);
           }
        }
        return score;
    }

    private Boolean matchRules(String firstTag, String secondTag) {
        for(String[] rule : bigramRules) {
            if(rule[0].equals(firstTag) && rule[1].equals(secondTag)) {
                return true;
            }
        }
        return false;
    }

    private void pruneSentence() throws IOException {
        for (int i=0; i < docLength; i++) {
            Map<String, Double> score = new HashMap<>();
            String sentence = nlpEngineImplementation.getSentenceFromId(docId, i);
            List parsed = getPOSTag(i);
            score = matchOnBigram((List<String>) parsed.get(0), (List<String>) parsed.get(1), score);
            sentenceScore.add(score);
        }
        normaliseScore();
        System.out.println("PartsOfSpeechTagFeature.pruneSentence"+"DocId: "+docId+" Sentence Score: "+sentenceScore+" [PARSED]");
    }

    @Override
    public Double getScore(int sentenceId, String key) {
        Double totalScore;
        if(sentenceScore.get(sentenceId).containsKey(key)) {
            totalScore = sentenceScore.get(sentenceId).get(key);
        } else {
            totalScore = Double.valueOf(0);
        }
        return totalScore;
    }

    public static void main(String[] args) throws IOException {
        PartsOfSpeechTagFeature pos = new PartsOfSpeechTagFeature("04e5aff5-3044-4be3-bbbc-e0f80cc77c37", 10);
    }
}
