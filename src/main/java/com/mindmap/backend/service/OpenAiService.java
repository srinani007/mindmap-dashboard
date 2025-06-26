package com.mindmap.backend.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final RestTemplate restTemplate;
    private final String OPENAI_URL = "https://api.openai.com/v1/completions";
    @Value("${openai.api.key}")
    private String apiKey;


    public OpenAiService() {
        this.restTemplate = new RestTemplate();
    }

    public String askOpenAi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo-instruct");
        body.put("prompt", prompt);
        body.put("temperature", 0.7);
        body.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, entity, Map.class);
            List<Map<String, String>> choices = (List<Map<String, String>>) response.getBody().get("choices");
            return choices.get(0).get("text");
        } catch (Exception e) {
            return "❌ AI Error: " + e.getMessage();
        }
    }


    public String answerQuestion(String question) {
        String prompt = """
        You are an expert assistant. Please provide a clear and informative answer to the following question:

        Question: %s

        Answer:
        """.formatted(question);

        return askOpenAi(prompt);
    }
}
