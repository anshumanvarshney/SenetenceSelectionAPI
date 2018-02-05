package com.example.SentenceSelectionMicroservice.Repository;

import com.example.SentenceSelectionMicroservice.Entity.DocumentQuestion;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentQuestionRepository extends MongoRepository<DocumentQuestion, String> {
    @Query(value="{status: false}", fields="{docId: true, _id: true}")
    List<DocumentQuestion> getDocumentByStatus();
}
