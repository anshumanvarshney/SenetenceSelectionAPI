package com.example.SentenceSelectionMicroservice;

import java.lang.String;

public class Constant {
    /**
     * Microservice IPs
     */
    public static final String NLPENGINE_URL="http://10.177.7.113:5000/nlp";
    public static final String STRUCTURE_URL="http://10.177.7.115:2305/indexer";

    /**
     * Feature Cnnfigurations
     */
    public static final int SCALE = 100;
    public static final String DOC_LIMIT = "3";
    public static final Double SENTENCE_RANK_THRESHOLD = Double.valueOf(10);
    public static final int PAGE_SIZE = 110;


    public static final Double LENGTH_OF_THE_WORD_BOOST = 0.3;
    public static final Double TERM_FREQUENCY_BOOST = 0.1;
    public static final Double NAME_ENTITY_TAG_BOOST = 0.2;
    public static final Double PARTS_OF_SPEECH_TAG_BOOST = 0.4;

    public static final double BOOST_PERSON = 0.4;
    public static final double BOOST_ORGANIZATION = 0.2;
    public static final double BOOST_OTHERS = 0.1;
    public static final double BOOST_QUANTITY=0.3;

    /**
     * Parts of Speech Tags
     */
    public static final String ADJECTIVE = "JJ";
    public static final String ADVERB = "RB";
    public static final String NOUN = "NN";
    public static final String PROPER_NOUN = "NNP";
    public static final String VERB = "VB";

    /**
     * Test Constants
     */
    public static final String TEST_DOCID = "6ae6954f-c707-40b5-b394-2f65e202ae6a";
    public static final Double PRECISION = 0.1;

    /**
     * API Endpoints
     */
    public static final String RELEVENT="/relevent";
    public static final String GET_STATUS="/status";
    public static final String GET_SENTENCE="/sentence/{docId}";
    public static final String PUT_STATUS="/status/{docId}";
    public static final String GET_EVAL="/eval";
}
