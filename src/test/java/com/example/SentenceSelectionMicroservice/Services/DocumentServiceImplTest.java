package com.example.SentenceSelectionMicroservice.Services;

import com.example.SentenceSelectionMicroservice.Entity.DocumentQuestion;
import com.example.SentenceSelectionMicroservice.Repository.DocumentQuestionRepository;
import com.example.SentenceSelectionMicroservice.SentenceRanking.Features.LengthOfTheWordFeature;
import com.example.SentenceSelectionMicroservice.SentenceRanking.Features.NameEntityTagFeature;
import com.example.SentenceSelectionMicroservice.SentenceRanking.Features.PartsOfSpeechTagFeature;
import com.example.SentenceSelectionMicroservice.SentenceRanking.Features.TermFrequencyFeature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.example.SentenceSelectionMicroservice.Constant.PRECISION;
import static com.example.SentenceSelectionMicroservice.Constant.TEST_DOCID;

@RunWith( SpringRunner.class )
@SpringBootTest
public class DocumentServiceImplTest {

    private String docID = TEST_DOCID;

    @InjectMocks
    private DocumentQuestionImplementation documentQuestionImplementation;

    @Mock
    private DocumentQuestionRepository documentQuestionRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(documentQuestionImplementation);
    }

    @Test
    public void existsTest_DocId_returnTrue() {
        DocumentQuestion documentQuestion = new DocumentQuestion();
        documentQuestion.setDocId("anshuman");
        Mockito.when(documentQuestionRepository.findOne("anshuman")).thenReturn(documentQuestion);
        Assert.assertEquals(documentQuestionImplementation.getDocumentById("anshuman").getDocId(), "anshuman");
    }

    @Test
    public void existsTest2_DocId_returnTrue() {
        DocumentQuestion documentQuestion = new DocumentQuestion();
        documentQuestion.setDocId("anshuman2");
        Mockito.when(documentQuestionRepository.findOne("anshuman")).thenReturn(documentQuestion);
        Assert.assertEquals(documentQuestionImplementation.getDocumentById("anshuman").getDocId(), "anshuman2");
    }

    @Test
    public void check_Term_Frequency() throws IOException {
        TermFrequencyFeature termFrequencyFeature = new TermFrequencyFeature(docID, 14);

        Assert.assertEquals(termFrequencyFeature.getScore(0, "Jack"), 100.0, PRECISION); // Name Entity
        Assert.assertEquals(termFrequencyFeature.getScore(2, "Apple"), 15.38, PRECISION); // Name Entity
        Assert.assertEquals(termFrequencyFeature.getScore(6, "is"), 0.0, PRECISION); // Stop Word
        Assert.assertEquals(termFrequencyFeature.getScore(2, "works"), 7.69, PRECISION); // Normal Word
        Assert.assertEquals(termFrequencyFeature.getScore(13, "2001"), 7.69, PRECISION); // Date Entity
    }

    @Test
    public void check_Length() throws IOException {
        LengthOfTheWordFeature lengthOfTheWordFeature = new LengthOfTheWordFeature(docID, 14);
        Assert.assertEquals(lengthOfTheWordFeature.getScore(0, "Jack"), 36.36, PRECISION);
        Assert.assertEquals(lengthOfTheWordFeature.getScore(12, "20"), 18.18, PRECISION);
    }

    @Test
    public void check_POS() throws IOException {
        PartsOfSpeechTagFeature partsOfSpeechTagFeature = new PartsOfSpeechTagFeature(docID, 14);
        Assert.assertEquals(partsOfSpeechTagFeature.getScore(0, "Jack"), 33.33, PRECISION);
        Assert.assertEquals(partsOfSpeechTagFeature.getScore(0, "Anshuman"), 0.0, PRECISION);
    }

    @Test
    public void check_Name_Entity() throws IOException {
        NameEntityTagFeature nameEntityTagFeature = new NameEntityTagFeature(docID, 14);
        Assert.assertEquals(nameEntityTagFeature.getScore(0, "Jack"), 40.0, PRECISION);
        Assert.assertEquals(nameEntityTagFeature.getScore(0, "Anshuman"), 0.0, PRECISION);
    }
}
