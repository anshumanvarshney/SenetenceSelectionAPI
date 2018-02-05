package com.example.SentenceSelectionMicroservice.Entity;

import com.example.SentenceSelectionMicroservice.DataTransferObjects.SentenceDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = DocumentQuestion.COLLECTION_NAME)
public class DocumentQuestion {

    public static final String COLLECTION_NAME = "DocumentQuestion";

    @Id
    String docId;
    List<SentenceDTO> sentences;
    Boolean status;

    public DocumentQuestion() {
        this.status = false;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public List<SentenceDTO> getSentences() {
        return sentences;
    }

    public void setSentences(List<SentenceDTO> sentences) {
        this.sentences = sentences;
    }

    public class COLLECTION_NAME {
    }

    @Override
    public String toString() {
        return "DocumentQuestion{" +
                "docId='" + docId + '\'' +
                ", sentences=" + sentences +
                '}';
    }
}
