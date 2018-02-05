package com.example.SentenceSelectionMicroservice.DataTransferObjects;

import java.util.List;

public class DocumentQuestionsDTO {
    String docId;
    Boolean status;
    List<SentenceDTO> sentences;
    public DocumentQuestionsDTO(String docId, List<SentenceDTO> sentences) {
        this.docId = docId;
        this.sentences = sentences;
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
}
