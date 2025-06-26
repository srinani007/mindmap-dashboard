package com.mindmap.backend.controller;

import com.mindmap.backend.model.Question;
import com.mindmap.backend.model.Topic;
import com.mindmap.backend.repository.QuestionRepository;
import com.mindmap.backend.repository.TopicRepository;
import com.mindmap.backend.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiController {

    private final TopicRepository topicRepo;
    private final QuestionRepository questionRepo;
    private final OpenAiService openAiService; // ⬅️ Create this next

    public AiController(TopicRepository topicRepo, QuestionRepository questionRepo, OpenAiService openAiService) {
        this.topicRepo = topicRepo;
        this.questionRepo = questionRepo;
        this.openAiService = openAiService;
    }

    @GetMapping("/ask/{topicId}")
    public ResponseEntity<String> askAi(@PathVariable Long topicId) {
        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic not found"));

        List<Question> questions = questionRepo.findByTopicId(topicId);
        StringBuilder context = new StringBuilder("Topic: " + topic.getName() + "\n");

        for (Question q : questions) {
            context.append("Q: ").append(q.getQuestion()).append("\n");
            context.append("A: ").append(q.getAnswer()).append("\n\n");
        }

        String prompt = """
                Read the following questions and answers. 
                1. Suggest 3 missing important questions for this topic.
                2. Summarize the topic in a paragraph.
                3. Optionally provide advice for mastering this topic.

                """ + context;

        String response = openAiService.askOpenAi(prompt);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ask-question")
    public ResponseEntity<String> askCustomQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Question cannot be empty.");
        }

        String aiResponse = openAiService.answerQuestion(question);
        return ResponseEntity.ok(aiResponse);
    }
}
