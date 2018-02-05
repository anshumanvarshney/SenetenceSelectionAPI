package com.example.SentenceSelectionMicroservice.Controller;

import com.example.SentenceSelectionMicroservice.Entity.DocumentQuestion;
import com.example.SentenceSelectionMicroservice.Services.DocumentQuestionService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.example.SentenceSelectionMicroservice.Constant.*;
import java.util.*;

@RestController
@RequestMapping(RELEVENT)
@CrossOrigin(origins = "*")
public class MainController {

    @Autowired
    DocumentQuestionService documentQuestionService;

    /**
     * GET: /eval
     * Ranks and select relevant sentences from document
     * @return {
     *     total_docs: Integer,
     *     parsed_docs: Integer
     * }
     */
    @RequestMapping(method= RequestMethod.GET, value=GET_EVAL)
    public ResponseEntity<?> EvaluateDocuments(){
        List<Integer> processDocuments = documentQuestionService.processDocuments();
        JSONObject response = new JSONObject();
        response.put("total_docs", processDocuments.get(1));
        response.put("parsed_docs", processDocuments.get(0));
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET: /status
     * Get all the docIds ready to parse for the Question Generator Microservice
     * @return [ <docIds></String> ]
     */
    @RequestMapping(method= RequestMethod.GET, value=GET_STATUS)
    public ResponseEntity<?> getAll(){
        Object response = null;
        List<String> docIds = documentQuestionService.getDocumentByStatus();
        if(docIds != null) {
            response = docIds;
        }else {
            JSONObject respJSON = new JSONObject();
            respJSON.put("message", "Cant find docIDs");
            response = respJSON;
        }
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * GET: /sentence/{docId}
     * Returns the QuestionDocumentDTO by its id
     * @return <documentDTO><DocumentQuestionDTO/>
     */
    @RequestMapping(method = RequestMethod.GET, value = GET_SENTENCE)
    public ResponseEntity<?> DocumentById(@PathVariable("docId") String docId){
        Object response = null;
        DocumentQuestion docIds = documentQuestionService.getDocumentById(docId);
        if(docIds != null) {
            response = docIds;
        }else {
            JSONObject respJSON = new JSONObject();
            respJSON.put("message", "Cant find docIDs");
            response = respJSON;
        }
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * PUT: /status/{docId}
     * Updates the status of document in Indexer Microservice
     * @param docId
     * @return
     */
    // TODO: add service
    @RequestMapping(method = RequestMethod.PUT, value = PUT_STATUS)
    public ResponseEntity<?> UpdateStatus(@PathVariable("docId") String docId){
        return  new ResponseEntity<>("", HttpStatus.OK);
    }
}
