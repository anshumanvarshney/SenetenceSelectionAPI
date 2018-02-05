package com.example.SentenceSelectionMicroservice.Services;

import com.example.SentenceSelectionMicroservice.Entity.DocumentQuestion;
import java.util.*;

public interface DocumentQuestionService {
    List<String> getDocumentByStatus();

    DocumentQuestion getDocumentById(String docId);

    List<Integer> processDocuments();

    DocumentQuestion save(DocumentQuestion documentQuestion);
}
