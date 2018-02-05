package com.example.SentenceSelectionMicroservice.SentenceRanking.Features;

import com.example.SentenceSelectionMicroservice.Services.NLPEngineImplementation;
import java.io.IOException;
import java.util.*;
import static com.example.SentenceSelectionMicroservice.Constant.*;
import static java.lang.Math.max;

public class NameEntityTagFeature implements Feature {
    public static Double boost = NAME_ENTITY_TAG_BOOST;
    List<String> entity = new ArrayList<String>();
    List<String> category = new ArrayList<String>();
    List<Integer> freq = new ArrayList<Integer>();
    NLPEngineImplementation nlpEngineImplementation = new NLPEngineImplementation();

    private int maxfreq = 0;
    private String docId;
    private int docLength;
    private double totalScore;

    public NameEntityTagFeature(String docId, int docLength) throws IOException {

        this.docId = docId;
        this.docLength = docLength;

        System.out.println("NameEntityTagFeature.NameEntityTagFeature" + " DocId: " + this.docId + " [PARSING]");
        this.pruneSentence();
    }

    private void getNameEntity(int sentenceId) {
        String sentence = nlpEngineImplementation.getSentenceFromId(docId, sentenceId);
        List<List<Object>> namedEntity = null;
        try {
            namedEntity = nlpEngineImplementation.getNamedEntity(sentence);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int index = 0; index < namedEntity.size() - 1; index++) {
            entity.add(namedEntity.get(index).get(0).toString());
            category.add(namedEntity.get(index).get(3).toString());
        }
    }

    private void pruneSentence() {
        for (int position = 0; position < docLength; position++) {
            getNameEntity(position);
        }
        System.out.println("NameEntityTagFeature.pruneSentence" + docId + " [PARSED]");
    }

    public double nameEntityTag(String key) {
        maxfreq = 0;
        double score = 0;
        for (int position = 0; position < entity.size(); position++) {
            int freqVal = nlpEngineImplementation.termFrequency(entity.get(position), docId);
            freq.add(freqVal);
            maxfreq = max(maxfreq, freqVal);
        }
        List<Double> normalisedFreq = new ArrayList<Double>();
        for (int index = 0; index < entity.size(); index++) {
            normalisedFreq.add((double) (freq.get(index) * 2 * SCALE) / maxfreq);
        }
        for (int position = 0; position < entity.size(); position++) {

            if (category.get(position).equalsIgnoreCase("Person")) {
                normalisedFreq.set(position, normalisedFreq.get(position) * BOOST_PERSON);
            } else if (category.get(position).equalsIgnoreCase("ORG")) {
                normalisedFreq.set(position, normalisedFreq.get(position) * BOOST_ORGANIZATION);
            } else if (category.get(position).equalsIgnoreCase("QUANTITY")) {
                normalisedFreq.set(position, normalisedFreq.get(position) * BOOST_QUANTITY);
            }
                else
                normalisedFreq.set(position, normalisedFreq.get(position) * BOOST_OTHERS);
        }
        for (int position = 0; position < entity.size(); position++) {
            String token = entity.get(position).substring(1, entity.get(position).length() - 1);
            if (token.equals(key)) {
                score = normalisedFreq.get(position);
            }
        }
        return score;
    }

    @Override
    public Double getScore(int sentenceId, String key) {
        totalScore = nameEntityTag(key);
        return totalScore;
    }

    public static void main(String[] args) throws IOException {
        NameEntityTagFeature pos = new NameEntityTagFeature("6ae6954f-c707-40b5-b394-2f65e202ae6a", 7);
        System.out.println(pos.getScore(1, "Sayan"));
    }
}