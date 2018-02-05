package com.example.SentenceSelectionMicroservice.Controller;

import com.example.SentenceSelectionMicroservice.Services.DocumentQuestionService;
import org.junit.After;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


public class MainControllerTest {

    @InjectMocks
    private MainController mainController;

    @Mock
    private DocumentQuestionService documentQuestionService;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.mainController).build();
    }

    @After
    public void tearDown(){
        Mockito.verifyNoMoreInteractions(documentQuestionService);
    }



}
