package com.mindmap.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.mindmap.backend.dto.QuestionDTO;
import com.mindmap.backend.model.Question;
import com.mindmap.backend.model.Topic;
import com.mindmap.backend.repository.QuestionRepository;
import com.mindmap.backend.repository.TopicRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:5173") // Update in production
public class QuestionController {

    private final QuestionRepository questionRepo;
    private final TopicRepository topicRepo;

    public QuestionController(QuestionRepository q, TopicRepository t) {
        this.questionRepo = q;
        this.topicRepo = t;
    }

    @GetMapping("/export/{topicId}")
    public ResponseEntity<List<QuestionDTO>> exportQuestions(@PathVariable Long topicId) {
        List<Question> questions = questionRepo.findByTopicId(topicId);

        List<QuestionDTO> dtoList = questions.stream()
                .map(q -> new QuestionDTO(
                        q.getTopic().getId(),
                        q.getId(),
                        q.getQuestion(),
                        q.getAnswer(),
                        q.isStarred(),
                        q.getTimestamp()
                ))
                .toList();

        return ResponseEntity.ok(dtoList);
    }




    /** POST a new question */
    @PostMapping
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionDTO dto) {
        Topic topic = topicRepo.findById(dto.getTopicId())
                .orElseThrow(() -> new RuntimeException("❌ Topic ID " + dto.getTopicId() + " not found"));

        Question q = new Question();
        q.setQuestion(dto.getQuestion());
        q.setAnswer(dto.getAnswer());
        q.setStarred(dto.isStarred());
        q.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now());
        q.setTopic(topic);

        if (dto.getTopicId() == null) {
            return ResponseEntity.badRequest().body("❌ Topic ID is required.");
        }
        System.out.println("📦 DTO Payload: " + dto);

        questionRepo.save(q);
        return ResponseEntity.ok(Map.of("message", "✅ Question created."));
    }

    /** PUT to update an existing question */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @RequestBody QuestionDTO dto) {
        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Question ID " + id + " not found"));

        q.setQuestion(dto.getQuestion());
        q.setAnswer(dto.getAnswer());
        q.setStarred(dto.isStarred());
        q.setTimestamp(dto.getTimestamp() != null ? dto.getTimestamp() : q.getTimestamp());

        questionRepo.save(q);
        return ResponseEntity.ok(Map.of("message", "✅ Question created."));
    }

    /** DELETE question by ID */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        System.out.println("💥 Deleting Question ID: " + id);

        Question question = questionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Question not found"));

        Topic topic = question.getTopic();

        if (topic != null) {
            topic.getQuestions().remove(question); // 🔥 IMPORTANT: remove from parent's list
        }

        questionRepo.delete(question); // Then delete from repo

        return ResponseEntity.ok("✅ Question deleted.");
    }

}
