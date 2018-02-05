package com.example.SentenceSelectionMicroservice.Services;

import com.example.SentenceSelectionMicroservice.DataTransferObjects.DocumentQuestionsDTO;
import com.example.SentenceSelectionMicroservice.Entity.DocumentQuestion;
import com.example.SentenceSelectionMicroservice.Repository.DocumentQuestionRepository;
import com.example.SentenceSelectionMicroservice.SentenceRanking.SentenceRanker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import static com.example.SentenceSelectionMicroservice.Constant.DOC_LIMIT;
import static com.example.SentenceSelectionMicroservice.Constant.STRUCTURE_URL;

@Service
public class DocumentQuestionImplementation implements DocumentQuestionService {

    @Autowired
    DocumentQuestionRepository documentQuestionRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<String> getDocumentByStatus() {
        List<String> docIds = new ArrayList<>();
        List<DocumentQuestion> docIdsDTO = documentQuestionRepository.getDocumentByStatus();
        if(docIdsDTO != null) {
            for(DocumentQuestion docIdDTO: docIdsDTO) {
                docIds.add(docIdDTO.getDocId());
            }
        }
        return docIds;
    }

    @Override
    public DocumentQuestion getDocumentById(String docId) {
        return documentQuestionRepository.findOne(docId);
    }

    public Boolean updateStatusByDocId(String docId) {
        return null;
    }

    private List<Integer> getDocLength(List<String> docIDs) {
        List<Integer> docLengths = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response =  restTemplate.postForEntity(STRUCTURE_URL+"/count", docIDs, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            for(int key=0; key<root.size(); key++){
                docLengths.add(root.get(key).asInt());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return docLengths;
    }

    @Async
    private DocumentQuestionsDTO calculateDocumentScore(String docId, int docLength) throws IOException {
        SentenceRanker sentenceRanker = new SentenceRanker(docId, docLength);
        sentenceRanker.computeRank();
        return sentenceRanker.getScores();
    }

    @Override
    public List<Integer> processDocuments() {
        int processedDocs = 0, totalDocs = 0;
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response =  restTemplate.getForEntity(STRUCTURE_URL+"/new/"+DOC_LIMIT, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            List<String> docIds = new ArrayList<String> ();
            for(int i=0; i<root.size(); i++) {
                docIds.add(root.get(i).asText());
            }
            List<Integer> docLengths = getDocLength(docIds);
            totalDocs = docIds.size();
            for(int documentIndex=0; documentIndex<docIds.size(); documentIndex++) {
                try {
                    DocumentQuestionsDTO documentQuestionDTO = calculateDocumentScore(docIds.get(documentIndex), docLengths.get(documentIndex));
                    DocumentQuestion documentQuestion = new DocumentQuestion();
                    BeanUtils.copyProperties(documentQuestionDTO, documentQuestion);
                    processedDocs++;
                    save(documentQuestion);
                } catch (Exception e) {
                    System.out.println("DocumentQuestionImplementation.processDocuments " + e);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(Arrays.asList(processedDocs, totalDocs));
    }

    @Override
    public DocumentQuestion save(DocumentQuestion documentQuestion) {
        return documentQuestionRepository.save(documentQuestion);
    }
}
