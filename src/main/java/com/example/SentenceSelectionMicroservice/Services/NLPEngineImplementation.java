package com.example.SentenceSelectionMicroservice.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.util.*;
import static com.example.SentenceSelectionMicroservice.Constant.NLPENGINE_URL;
import static com.example.SentenceSelectionMicroservice.Constant.PAGE_SIZE;
import static com.example.SentenceSelectionMicroservice.Constant.STRUCTURE_URL;

public class NLPEngineImplementation implements NLPEngine {
    private RestTemplate restTemplate = new RestTemplate();
    private static JsonNode pageCache;

    public NLPEngineImplementation() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        pageCache = mapper.readTree("{}");
    }

    @Override
    public String correctSentence(String sentence) {
        String corrected = "";
        try {
            JSONObject request = new JSONObject();
            request.put("sentence", sentence);
            ResponseEntity<String> response = restTemplate.postForEntity(NLPENGINE_URL+"/grammar", request, String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if(root.get("is_correct").booleanValue()) {
                corrected = sentence;
            } else {
                corrected = root.get("correct_sentence").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return corrected;
    }

    @Override
    public List<List<Object>> getNamedEntity(String sentence) {
        List<List<Object>> listResponse = new ArrayList<>();
        JSONObject request = new JSONObject();
        request.put("sentence", sentence);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(NLPENGINE_URL+"/ner/extract", request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            for(int i=0; i<root.size(); i++) {
                listResponse.add(Arrays.asList(root.get(i).get(0), root.get(i).get(1),
                        root.get(i).get(2), root.get(i).get(3)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listResponse;
    }

    private List<String> getList(String key, JsonNode root) {
        List<String> dep = new ArrayList<String>();
        for(int i=0; i<root.get(key).size(); i++) {
            String data = root.get(key).get(i).asText();
            dep.add(data);
        }
        return dep;
    }

    @Override
    public Map<String, List<String>> getPOSTags (String docId, int sentenceId) throws IOException {
        Map<String, List<String>> listResponse = new HashMap<String, List<String>>();

        if(!pageCache.has(String.valueOf(sentenceId))) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(STRUCTURE_URL+"/nlp/tokenInfo/"+docId)
                    .queryParam("size", PAGE_SIZE)
                    .queryParam("page",  Double.valueOf(Math.ceil(sentenceId / PAGE_SIZE)).intValue() );

            ResponseEntity<String> response = restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
            ObjectMapper mapper = new ObjectMapper();
            pageCache = mapper.readTree(response.getBody());
        }

        for(JsonNode token: pageCache.get(String.valueOf(sentenceId)).get("token")) {
            List<String> list = getList("token", pageCache.get(String.valueOf(sentenceId)));
            listResponse.put("token", list);
        }

        for(JsonNode token: pageCache.get(String.valueOf(sentenceId)).get("pos")) {
            List<String> list = getList("pos", pageCache.get(String.valueOf(sentenceId)));
            listResponse.put("pos", list);
        }
        return listResponse;
    }

    @Override
    public int termFrequency(String key, String docId) {
        int val=0;
        key=key.trim();
        if(!(key.length()==0)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);

            HttpEntity<String> entity = new HttpEntity<String>(key, headers);
            val=restTemplate.postForObject(STRUCTURE_URL+ "/word/"+docId, entity, Integer.class);
        }
        return val;
    }

    @Override
    public String getSentenceFromId(String docId, int sentenceId) {
        String sentence = "";
        try {
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

    public static void main(String[] args) throws IOException {
        NLPEngineImplementation nlpEngineImplementation = new NLPEngineImplementation();
        nlpEngineImplementation.getPOSTags("04e5aff5-3044-4be3-bbbc-e0f80cc77c37", 111);
    }
}
