package com.example.SentenceSelectionMicroservice.SentenceRanking;

import com.example.SentenceSelectionMicroservice.DataTransferObjects.DocumentQuestionsDTO;
import com.example.SentenceSelectionMicroservice.DataTransferObjects.SentenceDTO;
import com.example.SentenceSelectionMicroservice.SentenceRanking.Features.LengthOfTheWordFeature;
import com.example.SentenceSelectionMicroservice.SentenceRanking.Features.NameEntityTagFeature;
import com.example.SentenceSelectionMicroservice.SentenceRanking.Features.PartsOfSpeechTagFeature;
import com.example.SentenceSelectionMicroservice.SentenceRanking.Features.TermFrequencyFeature;
import com.example.SentenceSelectionMicroservice.Services.NLPEngineImplementation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.*;
import static com.example.SentenceSelectionMicroservice.Constant.SCALE;
import static com.example.SentenceSelectionMicroservice.Constant.SENTENCE_RANK_THRESHOLD;
import static com.example.SentenceSelectionMicroservice.Constant.STRUCTURE_URL;
import static java.lang.Double.max;

public class SentenceRanker {
    private String docId;
    private int docLength;
    private List<Double> scores;
    private NLPEngineImplementation nlpEngineImplementation;
    /**
     * Declare New Features Below
     */
    private PartsOfSpeechTagFeature partsOfSpeechTagFeature;
    private TermFrequencyFeature termFrequencyFeature;
    private LengthOfTheWordFeature lengthOfTheWordFeature;
    private NameEntityTagFeature nameEntityTagFeature;

    public SentenceRanker(String docId, int docLength) throws IOException {
        this.docId = docId;
        this.docLength = docLength;
        this.nlpEngineImplementation = new NLPEngineImplementation();
        this.scores = new ArrayList<>();
        /**
         * Initialise New Features Below
         */
        this.partsOfSpeechTagFeature = new PartsOfSpeechTagFeature(docId, docLength);
        this.termFrequencyFeature = new TermFrequencyFeature(docId, docLength);
        this.lengthOfTheWordFeature = new LengthOfTheWordFeature(docId, docLength);
        this.nameEntityTagFeature = new NameEntityTagFeature(docId, docLength);
    }

    private String getSentenceFromId(int sentenceId) {
        String sentence = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            List<Integer> request = new ArrayList<>();
            request.add(sentenceId);
            ResponseEntity<String> response =  restTemplate.postForEntity(STRUCTURE_URL+"/"+docId, request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            for (Iterator<String> it = root.fieldNames(); it.hasNext(); ) {
                String elts = it.next();
                sentence = root.get(elts).asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentence;
    }

    private List<String> getTokensFromString(int sentenceId) throws IOException {
        List<String> tokens;
        Map<String, List<String>> response = nlpEngineImplementation.getPOSTags(this.docId, sentenceId);
        tokens = response.get("token");
        return tokens;
    }

    private Double getScore(int sentenceId, String token) {
        Double score = Double.valueOf(1);
        List<Double> f = new ArrayList<>();
        /**
         * Add Score For Each Feature To Below
         */
        f.add(partsOfSpeechTagFeature.getScore(sentenceId, token)*partsOfSpeechTagFeature.boost);
        f.add(termFrequencyFeature.getScore(sentenceId, token)*termFrequencyFeature.boost);
        f.add(lengthOfTheWordFeature.getScore(sentenceId, token)*lengthOfTheWordFeature.boost);
        f.add(nameEntityTagFeature.getScore(sentenceId, token)*nameEntityTagFeature.boost);

        for(Double fVal: f) {
            fVal = (fVal == 0)? 1: fVal;
            score *= fVal;
        }
        return score;
    }

    private void normaliseScores(Double maxScore) {
        List<Double> tmpScores = new ArrayList<>();
        for(int i=0; i<docLength; i++) {
            Double normScore = scores.get(i)*(SCALE/maxScore);
            if(maxScore == 0) normScore = Double.valueOf(0);
            tmpScores.add(normScore);
        }
        scores = tmpScores;
    }

    /**
     * Computes the scores from each feature with the weight
     */
    public void computeRank() throws IOException {
        Double sentenceScore = Double.valueOf(0);
        Double maxSentenceScore = Double.valueOf(0);
        for(int i=0; i<docLength; i++) {
            String sentence = getSentenceFromId(i);
            System.out.println("SentenceRanker.computeRank" + " SentId: "+i+" Sentence: "+sentence);
            List<String> tokens = getTokensFromString(i);

            sentenceScore = Double.valueOf(0);
            for(String token: tokens) {
                sentenceScore += getScore(i, token);
            }
            maxSentenceScore = max(maxSentenceScore, sentenceScore);
            scores.add(i, sentenceScore);
        }
        normaliseScores(maxSentenceScore);
    }

    /**
     * Get the documentQuestionDTOs who's score is more than the threshold
     * @return List of Document Question DTOs
     */
    public DocumentQuestionsDTO getScores() {
        List<SentenceDTO> sentences = new ArrayList<>();
        if(scores != null)
        for(int i=0; i<scores.size(); i++) {
            if(scores.get(i) >= SENTENCE_RANK_THRESHOLD) {
                SentenceDTO sentence = new SentenceDTO(i, scores.get(i));
                sentences.add(sentence);

                System.out.println("SentenceRanker.getScores " + "DocId: " + docId + " SentenceId: " + i + " Score: " + scores.get(i));
            }
        }
        DocumentQuestionsDTO documentQuestion = new DocumentQuestionsDTO(docId, sentences);
        return  documentQuestion;
    }

    public static void main(String[] args) throws IOException {
        SentenceRanker sentenceRanker = new SentenceRanker("04e5aff5-3044-4be3-bbbc-e0f80cc77c37", 7);
        sentenceRanker.computeRank();
        sentenceRanker.getScores();
    }
}
